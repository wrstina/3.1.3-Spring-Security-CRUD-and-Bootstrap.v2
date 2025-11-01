package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findByUsername(String username);
    User getById(Long id);

    User save(User user);     // create
    User update(User user);   // update
    void deleteById(Long id);
}