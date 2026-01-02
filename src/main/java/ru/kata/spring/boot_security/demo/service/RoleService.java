package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;
import java.util.Set;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);
    Role getRoleByName(String name);

    void saveRole(Role role);
    void updateRole(Role role);
    void deleteRole(Long id);

    void deleteAllRoles();

    Set<Role> getRolesByNames(Set<String> roleNames);

    void createDefaultRoles();
}