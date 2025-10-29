package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
public class PageController {

    private final UserService userService;
    private final RoleService roleService;

    public PageController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // все пользователи для админки
        model.addAttribute("roles", roleService.getAllRoles()); // все роли для формы
        model.addAttribute("newUser", new User()); // пустой пользователь для создания
        return "admin"; // возвращаем admin.html
    }

    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        model.addAttribute("user", userService.getUserByUsername(principal.getName())); // данные текущего пользователя
        return "user";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
