package com.example.medappointmentscheduler.domain.entity;

import com.example.medappointmentscheduler.domain.entity.enums.AppointmentStatusEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity{
    @Column(nullable = false, name = "patient_full_name")
    private String patientFullName;

    @Column(nullable = false, name = "patient_email")
    private String patientEmail;

    @Column(nullable = false, name = "appointment_date")
    private Date appointmentDate;

    @Column(nullable = false, name = "appointment_hour")
    private Time appointmentHour;

    @Column(nullable = false)
    private String diseases;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatusEnum status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    public Appointment() {
    }

    public String getPatientFullName() {
        return patientFullName;
    }

    public Appointment setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
        return this;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public Appointment setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
        return this;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public Appointment setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
        return this;
    }

    public Time getAppointmentHour() {
        return appointmentHour;
    }

    public Appointment setAppointmentHour(Time appointmentHour) {
        this.appointmentHour = appointmentHour;
        return this;
    }

    public String getDiseases() {
        return diseases;
    }

    public Appointment setDiseases(String diseases) {
        this.diseases = diseases;
        return this;
    }

    public AppointmentStatusEnum getStatus() {
        return status;
    }

    public Appointment setStatus(AppointmentStatusEnum status) {
        this.status = status;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public Appointment setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public Appointment setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Appointment setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "patientFullName='" + patientFullName + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentHour=" + appointmentHour +
                ", diseases='" + diseases + '\'' +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", patient=" + patient +
                ", doctor=" + doctor +
                '}';
    }
}
