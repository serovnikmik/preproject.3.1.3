package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import javax.persistence.*;
import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public RoleDAOImpl(LocalContainerEntityManagerFactoryBean lcemfb) {
        EntityManagerFactory emf = lcemfb.getObject();
        em = emf.createEntityManager();
    }

    @Override
    public List<Role> getAllRoles() {
        TypedQuery<Role> q = em.createQuery("SELECT r FROM Role r ORDER BY r.id", Role.class);
        return q.getResultList();
    }

    @Override
    public Role getRoleById(Long id) {
        Role roleToFind = new Role();
        try {
            roleToFind = em.find(Role.class, id);
        } catch (Exception e) {
            logger.error("Failed to find role with id={}", id, e);
        }
        return roleToFind;
    }

    @Override
    public Role getRoleByName(String name) {
        Role role = null;
        try {
            TypedQuery<Role> q = em.createQuery(
                    "SELECT r FROM Role r WHERE r.name = :name", Role.class);
            q.setParameter("name", name);
            role = q.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("Role with name '{}' not found", name);
        } catch (Exception e) {
            logger.error("Failed to find role with name='{}'", name, e);
        }
        return role;
    }

    @Override
    public void save(Role role) {
        try {
            em.persist(role);
            logger.info("Role saved: {}", role.getName());
        } catch (EntityExistsException e) {
            logger.error("Failed to save role with name='{}'. Role already exists",
                    role.getName(), e);
        }
    }

    @Override
    public void update(Role role) {
        try {
            em.merge(role);
            logger.info("Role updated: {}", role.getName());
        } catch (Exception e) {
            logger.error("Failed to update role with id={}, name='{}'",
                    role.getId(), role.getName(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            em.createQuery("DELETE FROM Role r WHERE r.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            logger.info("Role deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Failed to delete role with id={}", id, e);
        }
    }

    @Override
    public void deleteAllRoles() {
        try {
            em.createNativeQuery("TRUNCATE TABLE role").executeUpdate();
            logger.info("All roles deleted");
        } catch (Exception e) {
            logger.error("Failed to delete all roles", e);
        }
    }
}