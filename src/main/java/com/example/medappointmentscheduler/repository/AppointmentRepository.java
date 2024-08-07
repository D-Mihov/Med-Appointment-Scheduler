package com.example.medappointmentscheduler.repository;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.enums.AppointmentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, Date appointmentDate);

    List<Appointment> findByAppointmentDateAndStatus(Date appointmentDate, AppointmentStatusEnum status);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatusEnum scheduled);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatusEnum scheduled);

    List<Appointment> findByStatus(AppointmentStatusEnum status);

    List<Appointment> findByAppointmentDateAndAppointmentHour(Date appointmentDate, Time appointmentTime);

    int countByDoctor(Doctor doctor);

    int countByDoctorAndStatus(Doctor doctor, AppointmentStatusEnum scheduled);

    int countByPatient(Patient patient);

    int countByPatientAndStatus(Patient patient, AppointmentStatusEnum scheduled);
}
