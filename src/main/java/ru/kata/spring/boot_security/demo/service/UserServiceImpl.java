package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

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

    // Security
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> (UserDetails) u)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Read only
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found, id=" + id));
    }

    // Create/Update/Delete
    @Override
    public User save(User user) {
        // регистронезависимая уникальность username при создании/смене
        if (user.getId() == null && userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        user.setRoles(normalizeRoles(user.getRoles()));

        // пароль: обязателен при создании; кодируем, если задан
        if (user.getId() == null) {
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(getById(user.getId()).getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) throw new IllegalArgumentException("User id is required");

        User existing = getById(user.getId());

        // регистронезависимая уникальность username при создании/смене
        String newUsername = user.getUsername();
        if (newUsername != null && !newUsername.equalsIgnoreCase(existing.getUsername())
                && userRepository.existsByUsernameIgnoreCase(newUsername)) {
            throw new IllegalArgumentException("Username already exists: " + newUsername);
        }

        user.setRoles(normalizeRoles(user.getRoles()));

        // если пароль: пустой — оставляем старый; не пустой — кодируем
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // helpers
    private Set<Role> normalizeRoles(Set<Role> roles) {
        if (roles == null) roles = Collections.emptySet();

        Set<Role> managed = roles.stream()
                .filter(Objects::nonNull)
                .map(r -> r.getId() != null ? roleService.getRoleById(r.getId()) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (managed.isEmpty()) {
            managed = Set.of(roleService.getRoleByName("ROLE_USER"));
        }
        return managed;
    }
}