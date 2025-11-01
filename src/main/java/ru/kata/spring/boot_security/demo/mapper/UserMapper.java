package ru.kata.spring.boot_security.demo.mapper;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// адаптер между веб-слоем (DTO) и доменной моделью (User), изолирует контроллер от деталей доменной модели: контроллер видит DTO, а не JPA-сущности
// разделяет ответственность: маппер — только преобразование данных;
// сервис — бизнес-правила (проверка уникальности логина, кодирование пароля, нормализация ролей)
@Component
public class UserMapper {
    public User fromCreateDto(UserCreateDto dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword()); // кодировать будет сервис
        u.setEmail(dto.getEmail());
        u.setAge(dto.getAge());
        u.setRoles(toRolesByIds(dto.getRoleIds()));
        return u;
    }

    // обновляем поля сущности по правилам из UserUpdateDto:
    // password: не заполнен — не менять
    // roleIds: не заполнены — не менять, empty — очистить роли
    public void merge(UserUpdateDto dto, User target) {
        if (dto.getUsername() != null) target.setUsername(dto.getUsername());
        if (dto.getEmail() != null)    target.setEmail(dto.getEmail());
        target.setAge(dto.getAge());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            target.setPassword(dto.getPassword()); // кодировать будет сервис
        }
        List<Long> roleIds = dto.getRoleIds();
        if (roleIds != null) {
            target.setRoles(toRolesByIds(roleIds)); // empty -> пустой сет (очистить роли)
        }
    }

    private Set<Role> toRolesByIds(List<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> {
                    Role r = new Role();
                    r.setId(id);
                    return r;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

