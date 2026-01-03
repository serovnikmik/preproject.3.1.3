package ru.kata.spring.boot_security.demo.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.UserDAOImpl;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueUsernameValidator implements
        ConstraintValidator<UniqueUsername, String>,
        ApplicationContextAware {

    private static final Logger logger =
            LoggerFactory.getLogger(UniqueUsernameValidator.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UniqueUsernameValidator.applicationContext = applicationContext;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        logger.info("checking isValid username = {}", username);
        if (username == null || username.trim().isEmpty()) {
            return true;
        }
        UserService userService = applicationContext.getBean(UserService.class);
        logger.info("username unique? -> {}", !userService.existsByUsername(username));
        return !userService.existsByUsername(username);
    }
}