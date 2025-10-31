package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UtilRole;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * SRP: контроллер только обрабатывает HTTP и собирает модель.
 * Конвертация ролей вынесена в UtilRole.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UtilRole utilRole;

    public AdminController(UserService userService, RoleService roleService, UtilRole utilRole) {
        this.userService = userService;
        this.roleService = roleService;
        this.utilRole = utilRole;
    }

    /** Не даём биндеру писать напрямую в user.roles — собираем роли вручную. */
    @InitBinder("userForm")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("roles");
    }

    @GetMapping
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("userForm", new User());
        if (principal != null) {
            model.addAttribute("currentUser", userService.getUserByUsername(principal.getName()));
        }
        return "admin";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("userForm") User form,
                             BindingResult binding,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                             Model model, Principal principal) {
        if (binding.hasErrors()) {
            // показать страницу без 400
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("allRoles", roleService.getAllRoles());
            if (principal != null) {
                model.addAttribute("currentUser", userService.getUserByUsername(principal.getName()));
            }
            return "admin";
        }
        Set<Role> roles = utilRole.resolveRoles(roleIds, roleNames);
        form.setRoles(roles);

        userService.createUser(form);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("userForm") User form,
                             BindingResult binding,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                             Model model, Principal principal) {
        if (binding.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("allRoles", roleService.getAllRoles());
            if (principal != null) {
                model.addAttribute("currentUser", userService.getUserByUsername(principal.getName()));
            }
            return "admin";
        }
        Set<Role> roles = utilRole.resolveRoles(roleIds, roleNames);
        form.setId(id);
        form.setRoles(roles);

        userService.updateUser(id, form);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}