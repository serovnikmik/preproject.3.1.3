package ru.kata.spring.boot_security.demo.controllers;

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

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds){
        if (bindingResult.hasErrors()){
            return "admin/create";
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
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             Model model) {

        // Проверка уникальности username (кроме текущего пользователя)
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != id) {
            bindingResult.rejectValue("username", "error.username",
                    "Пользователь с таким логином уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
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

        // Если пароль пустой - оставляем старый
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User existing = userService.getUser(id);
            user.setPassword(existing.getPassword());
        } else {
            // Шифруем новый пароль
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Устанавливаем роли
        user.setRoles(roles);

        // Обновляем
        userService.update(user);

        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}