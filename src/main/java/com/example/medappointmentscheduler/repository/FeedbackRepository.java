package com.example.medappointmentscheduler.repository;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback findByPatientAndAppointment(Patient patient, Appointment appointment);

    List<Feedback> findByPatient(Patient patient);

    List<Feedback> findByDoctor(Doctor doctor);
}
