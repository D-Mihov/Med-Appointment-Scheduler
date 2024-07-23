package com.example.medappointmentscheduler.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback extends BaseEntity{
    @Column(nullable = false)
    private String comment;

    private int rating;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Appointment appointment;

    public Feedback() {
    }

    public String getComment() {
        return comment;
    }

    public Feedback setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public Feedback setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public Feedback setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Feedback setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Feedback setAppointment(Appointment appointment) {
        this.appointment = appointment;
        return this;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "comment='" + comment + '\'' +
                ", rating=" + rating +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", appointment=" + appointment +
                '}';
    }
}
