package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPanel(@RequestParam(value = "editId", required = false) Long editId,
                             Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("newUser", new UserCreateDto());

        if (editId != null) {
            var user = userService.getUserById(editId);
            var updateDto = new UserUpdateDto(
                    user.getUsername(),
                    "", // пароль не показываем
                    user.getEmail(),
                    user.getAge(),
                    user.getRoles().stream()
                            .map(r -> r.getId())
                            .toList()
            );
            model.addAttribute("editUser", updateDto);
            model.addAttribute("editId", editId);
        }

        return "admin";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("newUser") UserCreateDto userDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin";
        }

        try {
            userService.createUser(userDto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("usernameError", e.getMessage());
            return "admin";
        }

        return "redirect:/admin";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("editUser") UserUpdateDto userDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("editId", id);
            return "admin";
        }

        userService.updateUser(id, userDto);
        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}