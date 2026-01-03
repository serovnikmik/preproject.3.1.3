package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private PasswordEncoder passwordEncoder; // Для шифрования паролей
    private final RoleService roleService; // Для работы с ролями

    @Autowired
    public UserServiceImpl(UserDAO userDAO,
                           RoleService roleService) {
        this.userDAO = userDAO;
        this.roleService = roleService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.debug("Getting all users");
        return userDAO.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(int id) {
        logger.debug("Getting user by id: {}", id);
        return userDAO.getUser(id);
    }

    @Override
    @Transactional
    public void save(User user) {
        logger.info("Saving user: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.save(user);

        logger.info("User saved successfully: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void save(User user, Set<Role> roles) {
        user.setRoles(roles);
        save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        logger.info("Updating user with id: {}", user.getId());

        User existingUser = userDAO.getUser(user.getId());

        if (existingUser != null) {
            String rawPassword = user.getPassword();
            String encodedPassword = existingUser.getPassword();

            if (rawPassword == null || rawPassword.isEmpty()
                    || rawPassword.equals(encodedPassword)
                    || passwordEncoder.matches(rawPassword, encodedPassword)) {
                user.setPassword(encodedPassword);
            } else {
                user.setPassword(passwordEncoder.encode(rawPassword));
                logger.debug("Password changed for user: {}", user.getUsername());
            }
        }

        userDAO.update(user);
        logger.info("User updated successfully: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void delete(int id) {
        logger.info("Deleting user with id: {}", id);
        userDAO.delete(id);
        logger.info("User deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllUsers() {
        logger.warn("Deleting all users!");
        userDAO.deleteAllUsers();
        logger.warn("All users deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        return userDAO.getUserByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsernameWithRoles(String username) {
        logger.debug("Getting user with roles by username: {}", username);
        return userDAO.getUserByUsernameWithRoles(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsersWithRoles() {
        logger.debug("Getting all users with roles");
        return userDAO.getAllUsersWithRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdWithRoles(int id) {
        logger.debug("Getting user with roles by id: {}", id);
        return userDAO.getUserByIdWithRoles(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        logger.debug("Checking if username exists: {}", username);
        return userDAO.existsByUsername(username);
    }

    @Transactional
    public void createUserWithRoles(User user, Set<String> roleNames) {
        logger.info("Creating user with roles: {}, roles: {}",
                user.getUsername(), roleNames);

        Set<Role> roles = roleService.getRolesByNames(roleNames);
        user.setRoles(roles);

        save(user);

        logger.info("User created with roles: {}", user.getUsername());
    }
}