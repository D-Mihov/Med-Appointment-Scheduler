package com.example.medappointmentscheduler.domain.model;

import com.example.medappointmentscheduler.utils.Validation.ValidEmail;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddAppointmentModel {
    @NotBlank(message = "{addappointment.patientFullName.notEmpty}")
    private String patientFullName;
    @ValidEmail
    private String patientEmail;
    @NotNull(message = "{addappointment.doctorId.notEmpty}")
    private Long doctorId;
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{addappointment.appointmentDate.notEmpty}")
    private LocalDate appointmentDate;
    @NotNull(message = "{addappointment.appointmentHour.notEmpty}")
    private LocalTime appointmentHour;
    @NotBlank(message = "{addappointment.diseases.notEmpty}")
    private String diseases;
    private String notes;
    private String status;
}
