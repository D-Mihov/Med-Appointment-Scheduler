package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.model.FeedbackModel;

import java.util.List;

public interface FeedbackService {
    List<Feedback> getAllFeedbacks();
    Feedback getFeedbackById(Long feedbackId);
    Feedback createFeedback(FeedbackModel feedbackModel);
//    Feedback updateFeedback(Long feedbackId, FeedbackModel feedbackModel);
    void deleteFeedback(Long feedbackId);

    Feedback getFeedbackByPatientAndAppointment(Patient patient, Appointment appointment);
}
