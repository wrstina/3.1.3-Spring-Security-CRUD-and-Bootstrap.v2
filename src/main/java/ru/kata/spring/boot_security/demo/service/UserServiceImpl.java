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

    private Set<Role> resolveRoles(Set<Role> roles) { // преобразует роли из формы в реальные роли из БД
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // шифруем пароль
        user.setRoles(resolveRoles(user.getRoles())); // устанавливаем роли
        return userRepository.save(user); // сохраняем в бд
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existing = getUserById(id); // находим существующего пользователя
        existing.setName(updatedUser.getName());
        existing.setLastName(updatedUser.getLastName());
        existing.setAge(updatedUser.getAge());
        existing.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // шифрует новый пароль
        }

        existing.setRoles(resolveRoles(updatedUser.getRoles()));
        return userRepository.save(existing); // сохраняем изменения в бд
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
