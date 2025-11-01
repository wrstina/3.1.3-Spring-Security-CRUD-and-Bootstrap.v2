package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController { // нет бизнес-логики: только маршрутизация

    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    public AdminController(UserService userService, RoleService roleService, UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String adminPage(Model model) {
        // Имена атрибутов оставлены прежними (HTML не меняем)
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute("newUser") @Valid UserCreateDto dto) {
        userService.save(userMapper.fromCreateDto(dto));
        return "redirect:/admin";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("editUser") @Valid UserUpdateDto dto) {
        var existing = userService.getById(id);
        userMapper.merge(dto, existing);
        userService.update(existing);
        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}