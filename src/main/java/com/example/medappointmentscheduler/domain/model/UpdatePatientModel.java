package com.example.medappointmentscheduler.domain.model;

import com.example.medappointmentscheduler.utils.Validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdatePatientModel {
    @NotBlank(message = "{signup.form.firstName.notEmpty}")
    private String firstName;
    @NotBlank(message = "{signup.form.lastName.notEmpty}")
    private String lastName;
    @ValidEmail
    private String email;
    @NotBlank(message = "{signup.form.phone.notEmpty}")
    @Pattern(regexp="(^$|[0-9]{10})", message = "{signup.form.phone.invalid}")
    private String phone;
    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{signup.form.dateOfBirth.notEmpty}")
    private LocalDate dateOfBirth;
    @NotBlank(message = "{signup.form.gender.notEmpty}")
    private String gender;
    private String medicalHistory;
    private String userRole;
}
