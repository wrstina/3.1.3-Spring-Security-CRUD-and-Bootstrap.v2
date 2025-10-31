package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.Collection;
import java.util.List;

public interface RoleService {

    // вернуть все роли из бд
    List<Role> getAllRoles();

    // найти роль по имени
    Role getRoleByName(String name);

     // поиск ролей по id. Если каких-то id нет — они игнорируются
    List<Role> findByIds(Collection<Long> ids);
}