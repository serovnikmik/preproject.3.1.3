package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();
    void deleteAllUsers();
    User getUser(int id);
    void save(User user);
    void save(User user, Set<Role> roles);
    void delete(int id);
    void update(User user);

    User getUserByUsername(String username);
    User getUserByUsernameWithRoles(String username);

    List<User> getAllUsersWithRoles();
    User getUserByIdWithRoles(int id);
    boolean existsByUsername(String username);
}