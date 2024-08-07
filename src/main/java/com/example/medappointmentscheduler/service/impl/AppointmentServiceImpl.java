package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.enums.AppointmentStatusEnum;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.AppointmentRepository;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.FeedbackRepository;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.EmailService;
import org.modelmapper.ModelMapper;
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

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final FeedbackRepository feedbackRepository;
    private final MessageSource messageSource;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, FeedbackRepository feedbackRepository, MessageSource messageSource, EmailService emailService, ModelMapper modelMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.feedbackRepository = feedbackRepository;
        this.messageSource = messageSource;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Appointment with ID " + id + " not found!"));
    }

    @Override
    public Appointment createAppointment(AddAppointmentModel appointmentModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientEmail = authentication.getName();

        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + patientEmail + " not found!"));

        Doctor doctor = doctorRepository.findById(appointmentModel.getDoctorId())
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with ID: " + appointmentModel.getDoctorId() + " not found!"));

        Appointment appointment = new Appointment();
        modelMapper.map(appointmentModel, appointment);
        appointment.setId(null);
        appointment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        appointment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        appointment.setAppointmentDate(Date.valueOf(appointmentModel.getAppointmentDate()));
        appointment.setAppointmentHour(Time.valueOf(appointmentModel.getAppointmentHour()));
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        emailService.sendAppointmentConfirmationEmail(patientEmail, savedAppointment);

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

    @Override
    public List<String> getAvailableHoursForDoctor(Long doctorId, String appointmentDate) {
        LocalDate date = LocalDate.parse(appointmentDate);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with ID " + doctorId + " not found!"));

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, Date.valueOf(date));

        List<String> availableHours = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            LocalTime time = LocalTime.of(hour, 0);

            if (existingAppointments.stream().noneMatch(appointment -> appointment.getAppointmentHour().equals(Time.valueOf(time)))) {
                availableHours.add(time.toString());
            }
        }

        return availableHours;
    }

    public List<Appointment> getAppointmentsForDate(LocalDate date, String status) {
        return appointmentRepository.findByAppointmentDateAndStatus(Date.valueOf(date), AppointmentStatusEnum.valueOf(status));
    }

    public int getAppointmentCountForDoctor(String doctorEmail) {
        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + doctorEmail + " not found!"));
        return appointmentRepository.countByDoctor(doctor);
    }

    @Override
    public int getScheduledAppointmentCountForDoctor(String doctorEmail) {
        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + doctorEmail + " not found!"));
        return appointmentRepository.countByDoctorAndStatus(doctor, AppointmentStatusEnum.Scheduled);
    }

    @Override
    public int getAppointmentCountForPatient(String patientEmail) {
        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Patient with email " + patientEmail + " not found!"));
        return appointmentRepository.countByPatient(patient);
    }

    @Override
    public int getScheduledAppointmentCountForPatient(String patientEmail) {
        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Patient with email " + patientEmail + " not found!"));
        return appointmentRepository.countByPatientAndStatus(patient, AppointmentStatusEnum.Scheduled);
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

    @Override
    public String isValidAppointment(AddAppointmentModel newAppointment) {
        Date appointmentDate = Date.valueOf(newAppointment.getAppointmentDate());
        Time appointmentTime = Time.valueOf(newAppointment.getAppointmentHour());
        Doctor doctor = doctorRepository.findById(newAppointment.getDoctorId())
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with ID " + newAppointment.getDoctorId() + " not found!"));
        Patient patient = patientRepository.findByEmail(newAppointment.getPatientEmail())
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + newAppointment.getPatientEmail() + " not found!"));

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
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + loggedInEmail + " not found!"));
        return appointmentRepository.findByDoctor(doctor);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + loggedInEmail + " not found!"));
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public int getCompletedAppointmentCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + loggedInEmail + " not found!"));
        List<Appointment> scheduledAppointments = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Completed);

        return scheduledAppointments.size();
    }

    @Override
    public int getCanceledAppointmentCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + loggedInEmail + " not found!"));
        List<Appointment> scheduledAppointments = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Canceled);

        return scheduledAppointments.size();
    }

    @Override
    public int getCompletedAppointmentCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + loggedInEmail + " not found!"));
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Completed);

        return scheduledAppointments.size();
    }

    @Override
    public int getCanceledAppointmentCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + loggedInEmail + " not found!"));
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Canceled);

        return scheduledAppointments.size();
    }

    @Override
    public int getFeedbacksCount() {
        return feedbackRepository.findAll().size();
    }

    @Override
    public int getFeedbacksCountForPatient(String loggedInEmail) {
        Patient patient = patientRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + loggedInEmail + " not found!"));
        List<Feedback> feedbacks = feedbackRepository.findByPatient(patient);

        return feedbacks.size();
    }

    @Override
    public int getFeedbacksCountForDoctor(String loggedInEmail) {
        Doctor doctor = doctorRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + loggedInEmail + " not found!"));
        List<Feedback> feedbacks = feedbackRepository.findByDoctor(doctor);

        return feedbacks.size();
    }
}
