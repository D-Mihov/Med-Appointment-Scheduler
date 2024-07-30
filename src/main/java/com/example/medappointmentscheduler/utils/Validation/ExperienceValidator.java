package com.example.medappointmentscheduler.utils.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ExperienceValidator implements ConstraintValidator<ValidExperience, String> {

    private final MessageSource messageSource;

    public ExperienceValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ValidExperience constraintAnnotation) {
    }

    @Override
    public boolean isValid(String experience, ConstraintValidatorContext context) {
        Locale locale = LocaleContextHolder.getLocale();

        if (experience == null || experience.trim().isEmpty()) {
            String message = messageSource.getMessage("signup.doctor.form.experience.notEmpty", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (!experience.matches("^[0-9]+$")) {
            String message = messageSource.getMessage("signup.doctor.form.experience.invalid", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
