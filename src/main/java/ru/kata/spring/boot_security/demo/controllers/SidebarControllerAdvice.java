package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@ControllerAdvice
public class SidebarControllerAdvice {

    @ModelAttribute("currentRole")
    public String getCurrentRole(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri != null) {
            if (uri.contains("/admin") || uri.startsWith("/admin/")) {
                return "ROLE_ADMIN";
            } else if (uri.contains("/user") || uri.startsWith("/user/")) {
                return "ROLE_USER";
            }
        }

        return "ROLE_USER";
    }

    @ModelAttribute("userHasAdminRole")
    public boolean userHasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            return authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @ModelAttribute("userHasUserRole")
    public boolean userHasUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            return authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        }
        return false;
    }
}