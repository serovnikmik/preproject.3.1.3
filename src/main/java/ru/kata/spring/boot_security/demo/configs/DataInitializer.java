package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;


@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(RoleService roleService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createDefaultRoles();

        createDefaultAdminDirectly();
        createDefaultUserDirectly();
    }

    @Transactional
    public void createDefaultRoles() {
        if (roleService.getRoleByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role("ROLE_ADMIN");
            roleService.saveRole(adminRole);
        }

        if (roleService.getRoleByName("ROLE_USER") == null) {
            Role userRole = new Role("ROLE_USER");
            roleService.saveRole(userRole);
        }
    }

    @Transactional
    public void createDefaultAdminDirectly() {
        String adminUsername = "admin";

        // Проверяем через EntityManager
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                        Long.class
                )
                .setParameter("username", adminUsername)
                .getSingleResult();

        if (count == 0) {
            logger.info("Creating admin user directly...");

            // Получаем роли
            Role adminRole = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.name = 'ROLE_ADMIN'", Role.class)
                    .getSingleResult();

            Role userRole = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.name = 'ROLE_USER'", Role.class)
                    .getSingleResult();

            // Создаем пользователя
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Administrator");
            admin.setEmail("admin@mail.ru");
            admin.setAge(30);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);

            // Сохраняем
            entityManager.persist(admin);

            logger.info("Admin created successfully");
        }
    }

    @Transactional
    public void createDefaultUserDirectly() {
        String userUsername = "user";

        // Проверяем через EntityManager
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                        Long.class
                )
                .setParameter("username", userUsername)
                .getSingleResult();

        if (count == 0) {
            logger.info("Creating default user directly...");

            // Получаем роли
            Role userRole = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.name = 'ROLE_USER'", Role.class)
                    .getSingleResult();

            // Создаем пользователя
            User admin = new User();
            admin.setUsername(userUsername);
            admin.setPassword(passwordEncoder.encode("user"));
            admin.setName("DefaultUser");
            admin.setEmail("user@mail.ru");
            admin.setAge(40);

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            admin.setRoles(roles);

            // Сохраняем
            entityManager.persist(admin);

            logger.info("Default user created successfully");
        }
    }
}