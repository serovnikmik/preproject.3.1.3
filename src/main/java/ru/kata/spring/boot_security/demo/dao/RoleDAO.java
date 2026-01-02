package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;

public interface RoleDAO {
    List<Role> getAllRoles();

    Role getRoleById(Long id);
    Role getRoleByName(String name);

    void save(Role role);
    void update(Role role);
    void delete(Long id);

    void deleteAllRoles();
}