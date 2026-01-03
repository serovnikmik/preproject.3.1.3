package ru.kata.spring.boot_security.demo.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Пользователь с таким логином уже существует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}