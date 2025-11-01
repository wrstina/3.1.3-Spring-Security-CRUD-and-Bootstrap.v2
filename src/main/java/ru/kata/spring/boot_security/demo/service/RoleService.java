package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();

    Role getRoleById(Long id);
    Role getRoleByName(String name);
    Role saveRole(Role role);
}