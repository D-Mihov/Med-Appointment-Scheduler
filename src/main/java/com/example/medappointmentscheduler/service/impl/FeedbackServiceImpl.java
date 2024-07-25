package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.model.FeedbackModel;
import com.example.medappointmentscheduler.repository.AppointmentRepository;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.FeedbackRepository;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.service.FeedbackService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final AppointmentRepository appointmentRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new UsernameNotFoundException("Feedback with ID: " + feedbackId + " not found!"));
    }

    @Override
    public Feedback createFeedback(FeedbackModel feedbackModel) {
        Feedback feedback = new Feedback();
        setFeedbackDetails(feedback, feedbackModel);
        feedback.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Patient patient = patientRepository.findById(feedbackModel.getPatientId())
                .orElseThrow(() -> new UsernameNotFoundException("Patient with ID: " + feedbackModel.getPatientId() + " not found!"));
        feedback.setPatient(patient);

        Doctor doctor = doctorRepository.findById(feedbackModel.getDoctorId())
                .orElseThrow(() -> new UsernameNotFoundException("Doctor with ID: " + feedbackModel.getDoctorId() + " not found!"));
        feedback.setDoctor(doctor);

        Appointment appointment = appointmentRepository.findById(feedbackModel.getAppointmentId())
                .orElseThrow(() -> new UsernameNotFoundException("Appointment with ID: " + feedbackModel.getAppointmentId() + " not found!"));
        feedback.setAppointment(appointment);

        return feedbackRepository.save(feedback);
    }

//    @Override
//    public Feedback updateFeedback(Long feedbackId, FeedbackModel feedbackModel) {
//        Feedback feedback = getFeedbackById(feedbackId);
//        setFeedbackDetails(feedback, feedbackModel);
//        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//
//        Patient patient = patientRepository.findById(feedbackModel.getPatientId())
//                .orElseThrow(() -> new UsernameNotFoundException("Patient with ID: " + feedbackModel.getPatientId() + " not found!"));
//        feedback.setPatient(patient);
//
//        Doctor doctor = doctorRepository.findById(feedbackModel.getDoctorId())
//                .orElseThrow(() -> new UsernameNotFoundException("Doctor with ID: " + feedbackModel.getDoctorId() + " not found!"));
//        feedback.setDoctor(doctor);
//
//        Appointment appointment = appointmentRepository.findById(feedbackModel.getAppointmentId())
//                .orElseThrow(() -> new UsernameNotFoundException("Appointment with ID: " + feedbackModel.getAppointmentId() + " not found!"));
//        feedback.setAppointment(appointment);
//
//        return feedbackRepository.save(feedback);
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

    private void setFeedbackDetails(Feedback feedback, FeedbackModel feedbackModel) {
        feedback.setComment(feedbackModel.getComment());
        feedback.setRating(Integer.parseInt(feedbackModel.getRating()));
    }
}
