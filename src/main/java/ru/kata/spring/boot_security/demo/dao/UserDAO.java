package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDAO {

    List<User> getAllUsers();
    User getUser(int userID);

    void save(User user);
    void update(User user);
    void delete(int id);

    void saveUsers(List<User> list);
    void deleteAllUsers();

    User getUserByUsername(String username);
    User getUserByUsernameWithRoles(String username);
    User getUserByIdWithRoles(int id);
    List<User> getAllUsersWithRoles();

    boolean existsByUsername(String username);



}
