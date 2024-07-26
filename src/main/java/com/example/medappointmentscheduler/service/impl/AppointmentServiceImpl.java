package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.enums.AppointmentStatusEnum;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.repository.AppointmentRepository;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.FeedbackRepository;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final FeedbackRepository feedbackRepository;
    private final MessageSource messageSource;
    private final EmailService emailService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, FeedbackRepository feedbackRepository, MessageSource messageSource, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.feedbackRepository = feedbackRepository;
        this.messageSource = messageSource;
        this.emailService = emailService;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Appointment with ID: " + id + " not found!"));
    }

    @Override
    public Appointment createAppointment(AddAppointmentModel appointmentModel) {
        Appointment appointment = new Appointment();
        setAppointmentDetails(appointmentModel, appointment);
        appointment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        appointment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String patientEmail = authentication.getName();

        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Patient " + patientEmail + " not found!"));
        appointment.setPatient(patient);

        Doctor doctor = doctorRepository.findById(appointmentModel.getDoctorId())
                .orElseThrow(() -> new UsernameNotFoundException("Doctor with ID: " + appointmentModel.getDoctorId() + " not found!"));
        appointment.setDoctor(doctor);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        emailService.sendAppointmentConfirmationEmail(appointment.getPatientEmail(), savedAppointment);

        return savedAppointment;
    }

//    @Override
//    public Appointment updateAppointment(Long id, AddAppointmentModel appointmentModel) {
//        Appointment appointment = getAppointmentById(id);
//        setAppointmentDetails(appointmentModel, appointment);
//        appointment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        String patientEmail = authentication.getName();
//
//        Patient patient = patientRepository.findByEmail(patientEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("Patient " + patientEmail + " not found!"));
//        appointment.setPatient(patient);
//
//        Doctor doctor = doctorRepository.findById(appointmentModel.getDoctorId())
//                .orElseThrow(() -> new UsernameNotFoundException("Doctor with ID: " + id + " not found!"));
//        appointment.setDoctor(doctor);
//
//        return appointmentRepository.save(appointment);
//    }

    @Override
    public void completeAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatusEnum.Completed);

        appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatusEnum.Canceled);

        appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    private void setAppointmentDetails(AddAppointmentModel appointmentModel, Appointment appointment) {
        appointment.setPatientFullName(appointmentModel.getPatientFullName());
        appointment.setPatientEmail(appointmentModel.getPatientEmail());
        appointment.setAppointmentDate(java.sql.Date.valueOf(appointmentModel.getAppointmentDate()));
        appointment.setAppointmentHour(java.sql.Time.valueOf(appointmentModel.getAppointmentHour()));
        appointment.setDiseases(appointmentModel.getDiseases());
        appointment.setStatus(AppointmentStatusEnum.valueOf(appointmentModel.getStatus()));
        appointment.setNotes(appointmentModel.getNotes());
    }

    @Override
    public List<String> getAvailableHoursForDoctor(Long doctorId, String appointmentDate) {
        LocalDate date = LocalDate.parse(appointmentDate);
        Optional<Doctor> optDoctor = doctorRepository.findById(doctorId);
        Doctor doctor = optDoctor.orElse(null);

        List<Appointment> existingAppointments = new ArrayList<>();
        if (doctor != null) {
            existingAppointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, java.sql.Date.valueOf(date));
        }

        List<String> availableHours = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            LocalTime time = LocalTime.of(hour, 0);

            if (!isDoctorBooked(existingAppointments, time)) {
                availableHours.add(time.toString());
            }
        }

        return availableHours;
    }

    private boolean isDoctorBooked(List<Appointment> existingAppointments, LocalTime time) {
        for (Appointment appointment :
                existingAppointments) {
            if (appointment.getAppointmentHour().equals(java.sql.Time.valueOf(time))) {
                return true;
            }
        }
        return false;
    }

    public List<Appointment> getAppointmentsForDate(LocalDate date, String status) {
        return appointmentRepository.findByAppointmentDateAndStatus(java.sql.Date.valueOf(date), AppointmentStatusEnum.valueOf(status));
    }

    public int getAppointmentCountForDoctor(String doctorEmail) {
        Doctor doctor = doctorRepository.findByEmail(doctorEmail).orElse(null);
        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);

        return appointments.size();
    }

    @Override
    public int getScheduledAppointmentCountForDoctor(String doctorEmail) {
        Doctor doctor = doctorRepository.findByEmail(doctorEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Scheduled);

        return scheduledAppointments.size();
    }

    @Override
    public int getAppointmentCountForPatient(String patientEmail) {
        Patient patient = patientRepository.findByEmail(patientEmail).orElse(null);
        List<Appointment> appointments = appointmentRepository.findByPatient(patient);

        return appointments.size();
    }

    @Override
    public int getScheduledAppointmentCountForPatient(String patientEmail) {
        Patient patient = patientRepository.findByEmail(patientEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Scheduled);

        return scheduledAppointments.size();
    }

    @Override
    public int getScheduledAppointmentCount() {
        return appointmentRepository.findByStatus(AppointmentStatusEnum.Scheduled).size();
    }

    @Override
    public int getCompletedAppointmentCount() {
        return appointmentRepository.findByStatus(AppointmentStatusEnum.Completed).size();
    }

    @Override
    public int getCanceledAppointmentCount() {
        return appointmentRepository.findByStatus(AppointmentStatusEnum.Canceled).size();
    }

    public String isValidAppointment(AddAppointmentModel newAppointment) {
        Date appointmentDate = Date.valueOf(newAppointment.getAppointmentDate());
        Time appointmentTime = Time.valueOf(newAppointment.getAppointmentHour());
        Doctor doctor = doctorRepository.findById(newAppointment.getDoctorId()).orElse(null);
        Patient patient = patientRepository.findByEmail(newAppointment.getPatientEmail()).orElse(null);

        List<Appointment> appointmentsByDateAndDoctor = appointmentRepository.findByDoctorAndAppointmentDate(doctor, appointmentDate);

        for (Appointment appointmentByDateAndDoctor :
                appointmentsByDateAndDoctor) {
            if (appointmentByDateAndDoctor.getPatient().equals(patient)) {
                return messageSource.getMessage("appointment.error.one", null, LocaleContextHolder.getLocale());
            }
        }

        List<Appointment> appointmentsByDateAndTime = appointmentRepository.findByAppointmentDateAndAppointmentHour(appointmentDate, appointmentTime);

        for (Appointment appointmentByDateAndTime :
                appointmentsByDateAndTime) {
            if (appointmentByDateAndTime.getPatient().equals(patient)) {
                return messageSource.getMessage("appointment.error.two", null, LocaleContextHolder.getLocale());
            }
        }

        return "";
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail).orElse(null);
        return appointmentRepository.findByDoctor(doctor);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail).orElse(null);
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public int getCompletedAppointmentCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Completed);

        return scheduledAppointments.size();
    }

    @Override
    public int getCanceledAppointmentCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Canceled);

        return scheduledAppointments.size();
    }

    @Override
    public int getCompletedAppointmentCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Completed);

        return scheduledAppointments.size();
    }

    @Override
    public int getCanceledAppointmentCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail).orElse(null);
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Canceled);

        return scheduledAppointments.size();
    }

    @Override
    public int getFeedbacksCount() {
        return feedbackRepository.findAll().size();
    }

    @Override
    public int getFeedbacksCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail).orElse(null);
        List<Feedback> feedbacks = feedbackRepository.findByPatient(patient);

        return feedbacks.size();
    }

    @Override
    public int getFeedbacksCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail).orElse(null);
        List<Feedback> feedbacks = feedbackRepository.findByDoctor(doctor);

        return feedbacks.size();
    }
}
