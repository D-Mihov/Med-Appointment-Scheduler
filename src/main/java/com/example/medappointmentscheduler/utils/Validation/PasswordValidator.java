package com.example.medappointmentscheduler.utils.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final MessageSource messageSource;

    public PasswordValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        Locale locale = LocaleContextHolder.getLocale();

        if (password == null || password.trim().isEmpty()) {
            String message = messageSource.getMessage("signup.form.password.notEmpty", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (password.length() < 6 || password.length() > 30) {
            String message = messageSource.getMessage("signup.form.password.size", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
