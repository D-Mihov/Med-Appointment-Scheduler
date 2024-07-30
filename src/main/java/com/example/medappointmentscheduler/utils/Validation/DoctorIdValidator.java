package com.example.medappointmentscheduler.utils.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DoctorIdValidator implements ConstraintValidator<ValidDoctorId, String> {

    private final MessageSource messageSource;

    public DoctorIdValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ValidDoctorId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String doctorId, ConstraintValidatorContext context) {
        Locale locale = LocaleContextHolder.getLocale();

        if (doctorId == null || doctorId.trim().isEmpty()) {
            String message = messageSource.getMessage("signup.doctor.form.doctorId.notEmpty", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (!doctorId.matches("^[A-Z]{2}\\d{4}$")) {
            String message = messageSource.getMessage("signup.form.doctorID.invalid", null, locale);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
