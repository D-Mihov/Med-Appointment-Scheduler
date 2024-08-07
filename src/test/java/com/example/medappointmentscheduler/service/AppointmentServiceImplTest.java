package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.enums.AppointmentStatusEnum;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.AppointmentRepository;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.FeedbackRepository;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private MessageSource messageSource;
    @Mock
    private EmailService emailService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private AddAppointmentModel appointmentModel;
    private Appointment appointment;
    private Patient patient;
    private Doctor doctor;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentServiceImpl(appointmentRepository, patientRepository, doctorRepository, feedbackRepository, messageSource, emailService, modelMapper);

        appointmentModel = new AddAppointmentModel();
        appointmentModel.setDoctorId(1L);
        appointmentModel.setAppointmentDate(LocalDate.parse("2000-02-02"));
        appointmentModel.setAppointmentHour(LocalTime.parse("09:00:00"));
        appointmentModel.setPatientEmail("patient@example.com");

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        appointment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        patient = new Patient();

        doctor = new Doctor();

        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createAppointment_success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("patient@example.com");

        when(patientRepository.findByEmail(appointmentModel.getPatientEmail())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment createdAppointment = appointmentService.createAppointment(appointmentModel);

        assertNotNull(createdAppointment);
        verify(emailService).sendAppointmentConfirmationEmail("patient@example.com", createdAppointment);
    }

    @Test
    void getAppointmentById_found() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Appointment foundAppointment = appointmentService.getAppointmentById(1L);

        assertNotNull(foundAppointment);
        assertEquals(1L, foundAppointment.getId());
    }

    @Test
    void getAppointmentById_notFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> appointmentService.getAppointmentById(1L));
    }

    @Test
    void completeAppointment_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.completeAppointment(1L);

        assertEquals(AppointmentStatusEnum.Completed, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancelAppointment_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatusEnum.Canceled, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void deleteAppointment_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void isValidAppointment_doctorNotFound() {
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                appointmentService.isValidAppointment(appointmentModel));

        assertEquals("Doctor with ID 1 not found!", exception.getMessage());
    }

    @Test
    void isValidAppointment_patientNotFound() {
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findByEmail(appointmentModel.getPatientEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                appointmentService.isValidAppointment(appointmentModel));

        assertEquals("Patient with email patient@example.com not found!", exception.getMessage());
    }

    @Test
    void isValidAppointment_appointmentExistsWithSameDoctorAndDate() {
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findByEmail(appointmentModel.getPatientEmail())).thenReturn(Optional.of(patient));

        appointment.setPatient(patient);
        when(appointmentRepository.findByDoctorAndAppointmentDate(doctor, Date.valueOf("2000-02-02"))).thenReturn(List.of(appointment));

        when(messageSource.getMessage("appointment.error.one", null, LocaleContextHolder.getLocale())).thenReturn("You already have an appointment with this doctor on this date.");

        String result = appointmentService.isValidAppointment(appointmentModel);

        assertEquals("You already have an appointment with this doctor on this date.", result);
    }

    @Test
    void isValidAppointment_appointmentExistsWithSameDateAndTime() {
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findByEmail(appointmentModel.getPatientEmail())).thenReturn(Optional.of(patient));

        appointment.setPatient(patient);
        appointment.setAppointmentHour(Time.valueOf("09:00:00"));
        when(appointmentRepository.findByAppointmentDateAndAppointmentHour(Date.valueOf("2000-02-02"), Time.valueOf("09:00:00"))).thenReturn(List.of(appointment));

        when(messageSource.getMessage("appointment.error.two", null, LocaleContextHolder.getLocale())).thenReturn("You already have an appointment at this time.");

        String result = appointmentService.isValidAppointment(appointmentModel);

        assertEquals("You already have an appointment at this time.", result);
    }

    @Test
    void isValidAppointment_noConflicts() {
        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findByEmail(appointmentModel.getPatientEmail())).thenReturn(Optional.of(patient));

        when(appointmentRepository.findByDoctorAndAppointmentDate(doctor, Date.valueOf("2000-02-02"))).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByAppointmentDateAndAppointmentHour(Date.valueOf("2000-02-02"), Time.valueOf("09:00:00"))).thenReturn(Collections.emptyList());

        String result = appointmentService.isValidAppointment(appointmentModel);

        assertEquals("", result);
    }

    @Test
    void getAvailableHoursForDoctor_doctorNotFound() {
        Long doctorId = 1L;
        String appointmentDate = "2000-02-02";

        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                appointmentService.getAvailableHoursForDoctor(doctorId, appointmentDate));

        assertEquals("Doctor with ID 1 not found!", exception.getMessage());
    }

    @Test
    void getAvailableHoursForDoctor_noExistingAppointments() {
        Long doctorId = 1L;
        String appointmentDate = "2000-02-02";

        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndAppointmentDate(doctor, Date.valueOf(appointmentDate))).thenReturn(Collections.emptyList());

        List<String> availableHours = appointmentService.getAvailableHoursForDoctor(doctorId, appointmentDate);

        List<String> expectedHours = Arrays.asList(
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"
        );

        assertEquals(expectedHours, availableHours);
    }

    @Test
    void getAvailableHoursForDoctor_someHoursBooked() {
        Long doctorId = 1L;
        String appointmentDate = "2000-02-02";

        when(doctorRepository.findById(appointmentModel.getDoctorId())).thenReturn(Optional.of(doctor));

        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentHour(Time.valueOf("10:00:00"));

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentHour(Time.valueOf("14:00:00"));

        when(appointmentRepository.findByDoctorAndAppointmentDate(doctor, Date.valueOf(appointmentDate))).thenReturn(Arrays.asList(appointment1, appointment2));

        List<String> availableHours = appointmentService.getAvailableHoursForDoctor(doctorId, appointmentDate);

        List<String> expectedHours = Arrays.asList(
                "09:00", "11:00", "12:00", "13:00", "15:00", "16:00", "17:00"
        );

        assertEquals(expectedHours, availableHours);
    }

    @Test
    void testGetAppointmentCountForDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countByDoctor(doctor)).thenReturn(5);

        int count = appointmentService.getAppointmentCountForDoctor("doctor@example.com");

        assertEquals(5, count);
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(appointmentRepository, times(1)).countByDoctor(doctor);
    }

    @Test
    void testGetScheduledAppointmentCountForDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countByDoctorAndStatus(doctor, AppointmentStatusEnum.Scheduled)).thenReturn(3);

        int count = appointmentService.getScheduledAppointmentCountForDoctor("doctor@example.com");

        assertEquals(3, count);
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(appointmentRepository, times(1)).countByDoctorAndStatus(doctor, AppointmentStatusEnum.Scheduled);
    }

    @Test
    void testGetAppointmentCountForPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(appointmentRepository.countByPatient(patient)).thenReturn(4);

        int count = appointmentService.getAppointmentCountForPatient("patient@example.com");

        assertEquals(4, count);
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(appointmentRepository, times(1)).countByPatient(patient);
    }

    @Test
    void testGetScheduledAppointmentCountForPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(appointmentRepository.countByPatientAndStatus(patient, AppointmentStatusEnum.Scheduled)).thenReturn(2);

        int count = appointmentService.getScheduledAppointmentCountForPatient("patient@example.com");

        assertEquals(2, count);
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(appointmentRepository, times(1)).countByPatientAndStatus(patient, AppointmentStatusEnum.Scheduled);
    }

    @Test
    void testGetScheduledAppointmentCount() {
        when(appointmentRepository.findByStatus(AppointmentStatusEnum.Scheduled)).thenReturn(Collections.emptyList());

        int count = appointmentService.getScheduledAppointmentCount();

        assertEquals(0, count);
        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatusEnum.Scheduled);
    }

    @Test
    void testGetCompletedAppointmentCount() {
        when(appointmentRepository.findByStatus(AppointmentStatusEnum.Completed)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCompletedAppointmentCount();

        assertEquals(0, count);
        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatusEnum.Completed);
    }

    @Test
    void testGetCanceledAppointmentCount() {
        when(appointmentRepository.findByStatus(AppointmentStatusEnum.Canceled)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCanceledAppointmentCount();

        assertEquals(0, count);
        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatusEnum.Canceled);
    }

    @Test
    void testGetAppointmentsByDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(Collections.emptyList());

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor("doctor@example.com");

        assertEquals(0, appointments.size());
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(appointmentRepository, times(1)).findByDoctor(doctor);
    }

    @Test
    void testGetAppointmentsByPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(Collections.emptyList());

        List<Appointment> appointments = appointmentService.getAppointmentsByPatient("patient@example.com");

        assertEquals(0, appointments.size());
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(appointmentRepository, times(1)).findByPatient(patient);
    }

    @Test
    void testGetCompletedAppointmentCountForDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Completed)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCompletedAppointmentCountForDoctor("doctor@example.com");

        assertEquals(0, count);
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(appointmentRepository, times(1)).findByDoctorAndStatus(doctor, AppointmentStatusEnum.Completed);
    }

    @Test
    void testGetCanceledAppointmentCountForDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatusEnum.Canceled)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCanceledAppointmentCountForDoctor("doctor@example.com");

        assertEquals(0, count);
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(appointmentRepository, times(1)).findByDoctorAndStatus(doctor, AppointmentStatusEnum.Canceled);
    }

    @Test
    void testGetCompletedAppointmentCountForPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Completed)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCompletedAppointmentCountForPatient("patient@example.com");

        assertEquals(0, count);
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(appointmentRepository, times(1)).findByPatientAndStatus(patient, AppointmentStatusEnum.Completed);
    }

    @Test
    void testGetCanceledAppointmentCountForPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientAndStatus(patient, AppointmentStatusEnum.Canceled)).thenReturn(Collections.emptyList());

        int count = appointmentService.getCanceledAppointmentCountForPatient("patient@example.com");

        assertEquals(0, count);
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(appointmentRepository, times(1)).findByPatientAndStatus(patient, AppointmentStatusEnum.Canceled);
    }

    @Test
    void testGetFeedbacksCount() {
        when(feedbackRepository.findAll()).thenReturn(Collections.emptyList());

        int count = appointmentService.getFeedbacksCount();

        assertEquals(0, count);
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void testGetFeedbacksCountForPatient() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(feedbackRepository.findByPatient(patient)).thenReturn(Collections.emptyList());

        int count = appointmentService.getFeedbacksCountForPatient("patient@example.com");

        assertEquals(0, count);
        verify(patientRepository, times(1)).findByEmail("patient@example.com");
        verify(feedbackRepository, times(1)).findByPatient(patient);
    }

    @Test
    void testGetFeedbacksCountForDoctor() {
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(feedbackRepository.findByDoctor(doctor)).thenReturn(Collections.emptyList());

        int count = appointmentService.getFeedbacksCountForDoctor("doctor@example.com");

        assertEquals(0, count);
        verify(doctorRepository, times(1)).findByEmail("doctor@example.com");
        verify(feedbackRepository, times(1)).findByDoctor(doctor);
    }

    @Test
    void testGetAppointmentCountForDoctor_throwsObjectNotFoundException() {
        when(doctorRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> appointmentService.getAppointmentCountForDoctor("nonexistent@example.com"));
    }

    @Test
    void testGetAppointmentCountForPatient_throwsUsernameNotFoundException() {
        when(patientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> appointmentService.getAppointmentCountForPatient("nonexistent@example.com"));
    }

    @Test
    void testGetAllAppointments() {
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findAll()).thenReturn(expectedAppointments);

        List<Appointment> actualAppointments = appointmentService.getAllAppointments();

        assertEquals(expectedAppointments, actualAppointments);
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void testGetAppointmentsForDate() {
        LocalDate localDate = LocalDate.parse("2000-02-02");
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findByAppointmentDateAndStatus(Date.valueOf(localDate), AppointmentStatusEnum.Scheduled)).thenReturn(expectedAppointments);

        List<Appointment> actualAppointments = appointmentService.getAppointmentsForDate(localDate, AppointmentStatusEnum.Scheduled.name());

        assertEquals(expectedAppointments, actualAppointments);
        verify(appointmentRepository, times(1)).findByAppointmentDateAndStatus(Date.valueOf(localDate), AppointmentStatusEnum.Scheduled);
    }
}
