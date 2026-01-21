package ru.kata.spring.boot_security.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private PasswordEncoder passwordEncoder;
    private final RoleService roleService;

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
        log.debug("Getting all users");
        return userDAO.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(int id) {
        log.debug("Getting user by id: {}", id);
        return userDAO.getUser(id);
    }

    @Override
    @Transactional
    public void save(User user) {
        log.info("Saving user: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.save(user);

        log.info("User saved successfully: {}", user.getUsername());
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
        log.info("Updating user with id: {}", user.getId());

        User existingUser = userDAO.getUser(user.getId());


        if (existingUser != null) {
            String rawPassword = user.getPassword();

            if (rawPassword == null || rawPassword.isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
            else if (passwordEncoder.matches(rawPassword, existingUser.getPassword())) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(rawPassword));
                log.debug("Password changed for user: {}", user.getUsername());
            }
        }

        userDAO.update(user);
        log.info("User updated successfully: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void delete(int id) {
        log.info("Deleting user with id: {}", id);
        userDAO.delete(id);
        log.info("User deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void deleteAllUsers() {
        log.warn("Deleting all users!");
        userDAO.deleteAllUsers();
        log.warn("All users deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        return userDAO.getUserByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsernameWithRoles(String username) {
        log.debug("Getting user with roles by username: {}", username);
        return userDAO.getUserByUsernameWithRoles(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsersWithRoles() {
        log.debug("Getting all users with roles");
        return userDAO.getAllUsersWithRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdWithRoles(int id) {
        log.debug("Getting user with roles by id: {}", id);
        return userDAO.getUserByIdWithRoles(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return userDAO.existsByUsername(username);
    }

    @Transactional
    public void createUserWithRoles(User user, Set<String> roleNames) {
        log.info("Creating user with roles: {}, roles: {}",
                user.getUsername(), roleNames);

        Set<Role> roles = roleService.getRolesByNames(roleNames);
        user.setRoles(roles);

        save(user);

        log.info("User created with roles: {}", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username for security: {}", username);
        User user = getUserByUsernameWithRoles(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        log.debug("User found: {} with roles: {} password is \"user\": {}",
                username, user.getRoles(), passwordEncoder.matches("user", user.getPassword()));

        return user;
    }
}