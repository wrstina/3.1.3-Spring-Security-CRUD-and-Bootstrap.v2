package ru.kata.spring.boot_security.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority { // сущность JPA для ролей с реализацией интерфейса Spring Security GrantedAuthority

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() { // возвращает имя роли для Spring Security
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}