package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.service.RoleService;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleService roleService;

    public DataInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data initialization...");

        // ТОЛЬКО роли!
        roleService.createDefaultRoles();

        logger.info("Data initialization completed (roles created)");
        logger.info("Admin user will be created by UserService via @PostConstruct");
    }
}