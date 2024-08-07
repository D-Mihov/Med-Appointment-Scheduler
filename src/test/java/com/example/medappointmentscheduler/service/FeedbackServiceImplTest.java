package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.model.FeedbackModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.AppointmentRepository;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.FeedbackRepository;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {
    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceImpl(feedbackRepository, patientRepository, doctorRepository, modelMapper, appointmentRepository);
    }

    @Test
    void getAllFeedbacks_success() {
        List<Feedback> feedbacks = List.of(new Feedback(), new Feedback());
        when(feedbackRepository.findAll()).thenReturn(feedbacks);

        List<Feedback> result = feedbackService.getAllFeedbacks();

        assertEquals(feedbacks.size(), result.size());
        verify(feedbackRepository).findAll();
    }

    @Test
    void getFeedbackById_success() {
        Long feedbackId = 1L;
        Feedback feedback = new Feedback();
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        Feedback result = feedbackService.getFeedbackById(feedbackId);

        assertNotNull(result);
        assertEquals(feedback, result);
        verify(feedbackRepository).findById(feedbackId);
    }

    @Test
    void getFeedbackById_notFound() {
        Long feedbackId = 1L;
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> feedbackService.getFeedbackById(feedbackId));
        verify(feedbackRepository).findById(feedbackId);
    }

    @Test
    void createFeedback_success() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.setPatientId(1L);
        feedbackModel.setDoctorId(2L);
        feedbackModel.setAppointmentId(3L);
        feedbackModel.setRating("3");

        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        Appointment appointment = new Appointment();
        Feedback feedback = new Feedback();

        when(patientRepository.findById(feedbackModel.getPatientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(feedbackModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(feedbackModel.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        Feedback result = feedbackService.createFeedback(feedbackModel);

        assertNotNull(result);
        assertEquals(feedback, result);
        verify(patientRepository).findById(feedbackModel.getPatientId());
        verify(doctorRepository).findById(feedbackModel.getDoctorId());
        verify(appointmentRepository).findById(feedbackModel.getAppointmentId());
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void createFeedback_patientNotFound() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.setPatientId(1L);
        feedbackModel.setDoctorId(2L);
        feedbackModel.setAppointmentId(3L);
        feedbackModel.setRating("3");

        when(patientRepository.findById(feedbackModel.getPatientId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> feedbackService.createFeedback(feedbackModel));
        verify(patientRepository).findById(feedbackModel.getPatientId());
    }

    @Test
    void createFeedback_doctorNotFound() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.setPatientId(1L);
        feedbackModel.setDoctorId(2L);
        feedbackModel.setAppointmentId(3L);
        feedbackModel.setRating("3");

        Patient patient = new Patient();
        when(patientRepository.findById(feedbackModel.getPatientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(feedbackModel.getDoctorId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> feedbackService.createFeedback(feedbackModel));
        verify(doctorRepository).findById(feedbackModel.getDoctorId());
    }

    @Test
    void createFeedback_appointmentNotFound() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.setPatientId(1L);
        feedbackModel.setDoctorId(2L);
        feedbackModel.setAppointmentId(3L);
        feedbackModel.setRating("3");

        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        when(patientRepository.findById(feedbackModel.getPatientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(feedbackModel.getDoctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(feedbackModel.getAppointmentId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> feedbackService.createFeedback(feedbackModel));
        verify(appointmentRepository).findById(feedbackModel.getAppointmentId());
    }

    @Test
    void deleteFeedback_success() {
        Long feedbackId = 1L;
        Feedback feedback = new Feedback();

        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        feedbackService.deleteFeedback(feedbackId);

        verify(feedbackRepository).delete(feedback);
    }

    @Test
    void deleteFeedback_notFound() {
        Long feedbackId = 1L;

        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> feedbackService.deleteFeedback(feedbackId));
        verify(feedbackRepository).findById(feedbackId);
    }

    @Test
    void getFeedbackByPatientAndAppointment_success() {
        Patient patient = new Patient();
        Appointment appointment = new Appointment();
        Feedback feedback = new Feedback();

        when(feedbackRepository.findByPatientAndAppointment(patient, appointment)).thenReturn(feedback);

        Feedback result = feedbackService.getFeedbackByPatientAndAppointment(patient, appointment);

        assertNotNull(result);
        assertEquals(feedback, result);
        verify(feedbackRepository).findByPatientAndAppointment(patient, appointment);
    }
}
