package com.example.medappointmentscheduler.domain.REST;

import jakarta.validation.constraints.NotBlank;

public class Prescription {
    private Long id;
    private Long patientId;
    private Long appointmentId;
    @NotBlank(message = "{prescription.medication.notEmpty}")
    private String medication;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }
}
