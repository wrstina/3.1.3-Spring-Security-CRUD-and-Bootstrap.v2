package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String admin(@RequestParam(value = "editId", required = false) Long editId,
                        Model model) {
        model.addAttribute("users", userService.getAllUsers());   // таблица пользователей
        model.addAttribute("roles", roleService.getAllRoles());   // чтобы отрисовать список ролей
        model.addAttribute("newUser", new User());                // форма создания

        if (editId != null) {
            model.addAttribute("editUser", userService.getUserById(editId)); // форма редактирования
        }
        return "admin"; // один шаблон admin.html и для списка, и для редактирования
    }

    private Set<Role> toRolesFromNames(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return null;
        }
        return roleNames.stream() // конвертируем список ролей из формы в Set<Role> с заполненным name
                .map(n -> {
                    Role r = new Role();
                    r.setName(n);
                    return r;
                })
                .collect(Collectors.toSet());
    }

    private Set<Role> toRolesFromIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return null;
        }
        Map<Long, String> idToName = roleService.getAllRoles().stream()
                .collect(Collectors.toMap(Role::getId, Role::getName));
        return roleIds.stream()
                .map(id -> {
                    String name = idToName.get(id);
                    if (name == null) return null; // неизвестный id — пропустим
                    Role r = new Role();
                    r.setName(name);
                    return r;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @PostMapping("/users/create")
    public String create(@ModelAttribute("newUser") User user,
                         @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        Set<Role> roles = toRolesFromNames(roleNames);
        if (roles == null) {
            roles = toRolesFromIds(roleIds);
        }
        user.setRoles(roles);

        userService.createUser(user);
        return "redirect:/admin";
    }

     //  если передан новый пароль — перешифруется и обновится
     // если пустой — пароль оставляем прежним
    @PostMapping("/users/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute User form,
                         @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        boolean noRolesProvided = (roleNames == null && roleIds == null);

        if (noRolesProvided) {
            form.setRoles(userService.getUserById(id).getRoles());
        } else {
            Set<Role> roles = toRolesFromNames(roleNames);
            if (roles == null) {
                roles = toRolesFromIds(roleIds);
            }
            form.setRoles(roles);
        }

        userService.updateUser(id, form);
        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
