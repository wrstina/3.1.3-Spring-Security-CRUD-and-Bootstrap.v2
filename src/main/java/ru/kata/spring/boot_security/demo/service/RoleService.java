package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.Collection;
import java.util.List;

public interface RoleService {

    /** Вернуть все роли из БД. */
    List<Role> getAllRoles();

    /** Найти роль по имени (например, "ROLE_USER"). */
    Role getRoleByName(String name);

    /**
     * Батч-поиск ролей по id. Если каких-то id нет — они просто игнорируются.
     * Возвращаем именно список ролей, а НЕ int.
     */
    List<Role> findByIds(Collection<Long> ids);
}