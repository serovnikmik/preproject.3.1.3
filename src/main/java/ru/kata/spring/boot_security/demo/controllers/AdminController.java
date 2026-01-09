package ru.kata.spring.boot_security.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService){
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        model.addAttribute("user", new User()); // добавляем пустого пользователя для формы
        model.addAttribute("hasFormErrors", false);
        return "admin/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "admin/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                         Model model) {

        // Проверка уникальности username при создании
        if (userService.getUserByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.username",
                    "Пользователь с таким логином уже существует");
        }

        if (bindingResult.hasErrors()){
            // При ошибках возвращаем на главную страницу с теми же данными
            model.addAttribute("listOfUsers", userService.getAllUsers());
            model.addAttribute("hasFormErrors", true); // устанавливаем флаг ошибок
            return "admin/index"; // остаемся на той же странице
        }

        Set<Role> roles = new HashSet<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
        } else {
            // Получаем роль USER по имени
            Role userRole = roleService.getRoleByName("ROLE_USER");
            if (userRole != null) {
                roles.add(userRole);
            }
        }

        userService.save(user, roles);
        return "redirect:/admin";
    }

    @GetMapping("/update/{id}")
    public String editForm(@PathVariable("id") int id, Model model) {
        User user = userService.getUserByIdWithRoles(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") int id,
                             @ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        logger.info("=== UPDATE USER START ===");
        logger.info("ID: {}", id);
        logger.info("User from form: {}", user);
        logger.info("RoleIds: {}", roleIds);
        logger.info("BindingResult errors: {}", bindingResult.hasErrors());

        // Проверка уникальности username (кроме текущего пользователя)
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != id) {
            bindingResult.rejectValue("username", "error.username",
                    "Пользователь с таким логином уже существует");
            logger.info("Username already exists error");
        }

        if (bindingResult.hasErrors()) {
            logger.info("Validation errors found: {}", bindingResult.getAllErrors());
            return "redirect:/admin";
        }

        // Устанавливаем ID (на всякий случай)
        user.setId(id);

        // Собираем роли
        Set<Role> roles = new HashSet<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        logger.info("Collected roles: {}", roles);

        // Если пароль пустой - оставляем старый
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User existing = userService.getUser(id);
            user.setPassword(existing.getPassword());
            logger.info("Password is empty, keeping old password");
        } else {
            // Шифруем новый пароль
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.info("Password changed, encrypted new password");
        }

        // Устанавливаем роли
        user.setRoles(roles);
        logger.info("Final user object before update: {}", user);

        // Обновляем
        try {
            userService.update(user);
            logger.info("User updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user: ", e);
        }

        logger.info("=== UPDATE USER END ===");
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}