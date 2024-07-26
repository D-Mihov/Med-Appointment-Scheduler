package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    Appointment createAppointment(AddAppointmentModel appointmentModel);
//    Appointment updateAppointment(Long id, AddAppointmentModel appointmentModel);
    void completeAppointment(Long id);
    void cancelAppointment(Long id);
    void deleteAppointment(Long id);

    List<String> getAvailableHoursForDoctor(Long doctorId, String appointmentDate);

    List<Appointment> getAppointmentsForDate(LocalDate date, String status);
    int getAppointmentCountForDoctor(String doctorEmail);

    int getScheduledAppointmentCountForDoctor(String doctorEmail);

    int getAppointmentCountForPatient(String patientEmail);

    int getScheduledAppointmentCountForPatient(String patientEmail);

    int getScheduledAppointmentCount();

    int getCompletedAppointmentCount();

    int getCanceledAppointmentCount();

    String isValidAppointment(AddAppointmentModel newAppointment);

    List<Appointment> getAppointmentsByDoctor(String loggedInEmail);

    List<Appointment> getAppointmentsByPatient(String loggedInEmail);

    int getCompletedAppointmentCountForDoctor(String loggedInEmail);

    int getCanceledAppointmentCountForDoctor(String loggedInEmail);

    int getCompletedAppointmentCountForPatient(String loggedInEmail);

    int getCanceledAppointmentCountForPatient(String loggedInEmail);

    int getFeedbacksCount();

    int getFeedbacksCountForPatient(String loggedInEmail);

    int getFeedbacksCountForDoctor(String loggedInEmail);
}
