package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    // преобразует роли из формы (по имени) в реальные роли из бд
    private Set<Role> resolveRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(roleService.getRoleByName("ROLE_USER"));
        }
        return roles.stream()
                .map(r -> roleService.getRoleByName(r.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // шифруем пароль
        user.setRoles(resolveRoles(user.getRoles()));                 // резолвим роли по имени
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existing = getUserById(id); // находим если существует по id

        // базовые поля для обновления из бд
        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setAge(updatedUser.getAge());

        // пароль меняем только если пришёл новый и непустой
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // роли: если не пришли — оставляем прежние; если пришли — резолвим по имени
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existing.setRoles(resolveRoles(updatedUser.getRoles()));
        }

        return userRepository.save(existing); // сохраняем изменения
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
