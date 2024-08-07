package com.example.medappointmentscheduler.service.impl;

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
import com.example.medappointmentscheduler.service.FeedbackService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    private final AppointmentRepository appointmentRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository, ModelMapper modelMapper, AppointmentRepository appointmentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ObjectNotFoundException("Feedback with ID " + feedbackId + " not found!"));
    }

    @Override
    public Feedback createFeedback(FeedbackModel feedbackModel) {
        Feedback feedback = new Feedback();
        feedback.setComment(feedbackModel.getComment());
        feedback.setRating(Integer.parseInt(feedbackModel.getRating()));
        feedback.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return setFeedbackDetails(feedbackModel, feedback);
    }

    private Feedback setFeedbackDetails(FeedbackModel feedbackModel, Feedback feedback) {
        Patient patient = patientRepository.findById(feedbackModel.getPatientId())
                .orElseThrow(() -> new ObjectNotFoundException("Patient with ID: " + feedbackModel.getPatientId() + " not found!"));
        feedback.setPatient(patient);

        Doctor doctor = doctorRepository.findById(feedbackModel.getDoctorId())
                .orElseThrow(() -> new ObjectNotFoundException("Doctor with ID: " + feedbackModel.getDoctorId() + " not found!"));
        feedback.setDoctor(doctor);

        Appointment appointment = appointmentRepository.findById(feedbackModel.getAppointmentId())
                .orElseThrow(() -> new ObjectNotFoundException("Appointment with ID: " + feedbackModel.getAppointmentId() + " not found!"));
        feedback.setAppointment(appointment);

        return feedbackRepository.save(feedback);
    }

//    @Override
//    public Feedback updateFeedback(Long feedbackId, FeedbackModel feedbackModel) {
//        Feedback feedback = getFeedbackById(feedbackId);
//
//        modelMapper.map(feedbackModel, Feedback.class);
//        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//
//        return setFeedbackDetails(feedbackModel, feedback);
//    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = getFeedbackById(feedbackId);
        feedbackRepository.delete(feedback);
    }

    @Override
    public Feedback getFeedbackByPatientAndAppointment(Patient patient, Appointment appointment) {
        return feedbackRepository.findByPatientAndAppointment(patient, appointment);
    }
}
