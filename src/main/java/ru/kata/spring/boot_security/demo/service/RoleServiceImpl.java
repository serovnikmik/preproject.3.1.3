package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.RoleDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleDAO roleDAO;

    @Autowired
    public RoleServiceImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        logger.debug("Getting all roles");
        return roleDAO.getAllRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        logger.debug("Getting role by id: {}", id);
        return roleDAO.getRoleById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        logger.debug("Getting role by name: {}", name);
        return roleDAO.getRoleByName(name);
    }

    @Override
    @Transactional
    public void saveRole(Role role) {
        logger.info("Saving role: {}", role.getName());
        roleDAO.save(role);
    }

    @Override
    @Transactional
    public void updateRole(Role role) {
        logger.info("Updating role with id: {}", role.getId());
        roleDAO.update(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        logger.info("Deleting role with id: {}", id);
        roleDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteAllRoles() {
        logger.warn("Deleting all roles!");
        roleDAO.deleteAllRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByNames(Set<String> roleNames) {
        logger.debug("Getting roles by names: {}", roleNames);
        if (roleNames == null || roleNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = getRoleByName(roleName);
            if (role != null) {
                roles.add(role);
            } else {
                logger.warn("Role with name '{}' not found", roleName);
            }
        }
        return roles;
    }

    @Override
    @Transactional
    public void createDefaultRoles() {
        logger.info("Creating default roles if they don't exist");

        // Проверяем и создаем роль ADMIN если её нет
        if (getRoleByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role("ROLE_ADMIN");
            saveRole(adminRole);
            logger.info("Created default role: ROLE_ADMIN");
        }

        // Проверяем и создаем роль USER если её нет
        if (getRoleByName("ROLE_USER") == null) {
            Role userRole = new Role("ROLE_USER");
            saveRole(userRole);
            logger.info("Created default role: ROLE_USER");
        }
    }
}