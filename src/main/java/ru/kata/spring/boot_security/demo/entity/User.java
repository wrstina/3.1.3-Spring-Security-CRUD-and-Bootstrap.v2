package ru.kata.spring.boot_security.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // users.username (UNIQUE, NOT NULL, VARCHAR(50))
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 20 characters long")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can contain only Latin letters, digits, and underscores")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // users.password (NOT NULL, VARCHAR(100)); валидируем как raw или bcrypt
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$|^\\$2[aby]\\$.+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    @Column(nullable = false, length = 100)
    private String password;

    // users.email (NULLABLE, VARCHAR(100))
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(length = 100)
    private String email;

    // users.age (NULLABLE INT) — оставим без @NotNull, можно не заполнять
    @Min(value = 1, message = "Age must be > 0")
    @Max(value = 120, message = "Age must not exceed 120")
    @Column
    private int age;

    // user_roles (user_id, role_id)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {}

    public User(String username,String password,String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return roles; } // возвращает роли пользователя для Spring Security

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }


    // методы Spring Security для проверки статуса аккаунта пользователся
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", roles=" + roles +
                '}';
    }
}