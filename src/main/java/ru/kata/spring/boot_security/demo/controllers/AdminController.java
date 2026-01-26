package ru.kata.spring.boot_security.demo.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping("")
    public String adminPage(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        model.addAttribute("user", new User());
        model.addAttribute("hasFormErrors", false);
        model.addAttribute("currentUser", currentUser);
        return "admin/index";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                         Model model) {

        if (userService.getUserByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.username",
                    "Пользователь с таким логином уже существует");
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("listOfUsers", userService.getAllUsers());
            model.addAttribute("hasFormErrors", true);
            return "admin/index";
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
            Role userRole = roleService.getRoleByName("ROLE_USER");
            if (userRole != null) {
                roles.add(userRole);
            }
        }

        userService.save(user, roles);
        return "redirect:/admin";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") int id,
                             @ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             Model model) {

        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != id) {
            bindingResult.rejectValue("username", "error.username",
                    "Пользователь с таким логином уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        }

        user.setId(id);

        Set<Role> roles = new HashSet<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        user.setRoles(roles);

        userService.update(user);

        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = currentAuth.getName();

        User userToDelete = userService.getUserByIdWithRoles(id);

        if (userToDelete != null && userToDelete.getUsername().equals(currentUsername)) {
            new SecurityContextLogoutHandler().logout(request, response, currentAuth);

            userService.delete(id);

            return "redirect:/login?logout";
        } else {
            userService.delete(id);
            return "redirect:/admin";
        }
    }
}