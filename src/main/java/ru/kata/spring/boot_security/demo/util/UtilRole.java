package ru.kata.spring.boot_security.demo.util;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Конвертация id/имён ролей вынесена из контроллера (SRP).
 * Использует контракт RoleService: getAllRoles(), getRoleByName(String).
 */
@Component
public class UtilRole {

    private final RoleService roleService;

    public UtilRole(RoleService roleService) {
        this.roleService = roleService;
    }

    // преобразует коллекцию id -> Set<Role>. Отсутствующие id игнорируются
    public Set<Role> rolesFromIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();

        Map<Long, Role> byId = roleService.getAllRoles().stream()
                .collect(Collectors.toMap(Role::getId, r -> r));

        LinkedHashSet<Role> out = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null) continue;
            Role r = byId.get(id);
            if (r != null) out.add(r);
        }
        return out;
    }

    // преобразует коллекцию имён -> Set<Role>. Пустые имена игнорируются
    public Set<Role> rolesFromNames(Collection<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();

        LinkedHashSet<Role> out = new LinkedHashSet<>();
        for (String raw : names) {
            if (raw == null) continue;
            String name = raw.trim();
            if (name.isEmpty()) continue;
            Role r = roleService.getRoleByName(name);
            if (r != null) out.add(r);
        }
        return out;
    }

    // универсально: объединяем найденное по id и по имени
    public Set<Role> resolveRoles(Collection<Long> roleIds, Collection<String> roleNames) {
        LinkedHashSet<Role> out = new LinkedHashSet<>();
        out.addAll(rolesFromIds(roleIds));
        out.addAll(rolesFromNames(roleNames));
        return out;
    }
}