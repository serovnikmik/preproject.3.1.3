package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {

    // ==== СУЩЕСТВУЮЩИЕ МЕТОДЫ ====
    List<User> getAllUsers();
    void deleteAllUsers();
    User getUser(int id);
    void save(User user);
    void delete(int id);
    void update(User user);

    User getUserByUsername(String username);
    User getUserByUsernameWithRoles(String username);

    List<User> getAllUsersWithRoles();
    User getUserByIdWithRoles(int id);
    boolean existsByUsername(String username);

    void createDefaultAdmin();
}