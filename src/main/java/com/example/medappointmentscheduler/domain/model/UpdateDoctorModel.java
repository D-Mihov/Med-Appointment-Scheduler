package com.example.medappointmentscheduler.domain.model;

import com.example.medappointmentscheduler.utils.Validation.ValidDoctorId;
import com.example.medappointmentscheduler.utils.Validation.ValidEmail;
import com.example.medappointmentscheduler.utils.Validation.ValidEmailAddAppointment;
import com.example.medappointmentscheduler.utils.Validation.ValidExperience;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateDoctorModel {
    @NotBlank(message = "{signup.doctor.form.firstName.notEmpty}")
    private String firstName;
    @NotBlank(message = "{signup.doctor.form.lastName.notEmpty}")
    private String lastName;
    @ValidEmailAddAppointment
    private String email;
    @NotBlank(message = "{signup.doctor.form.phone.notEmpty}")
    @Pattern(regexp="(^$|[0-9]{10})", message = "{signup.form.phone.invalid}")
    private String phone;
    @NotBlank(message = "{signup.doctor.form.speciality.notEmpty}")
    private String speciality;
    @ValidExperience
    private String experience;
    private String education;
    @ValidDoctorId
    private String doctorId;
    private String userRole;
}
