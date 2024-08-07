package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Feedback;
import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.model.FeedbackModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.FeedbackService;
import com.example.medappointmentscheduler.service.PatientService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final ModelMapper modelMapper;

    public FeedbackController(FeedbackService feedbackService, AppointmentService appointmentService, PatientService patientService, ModelMapper modelMapper) {
        this.feedbackService = feedbackService;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/appointments/feedback")
    public String showFeedbackForm(@RequestParam Long appointmentId,
                                   @RequestParam Long patientId,
                                   Model model) {

        Patient patient = patientService.getPatientById(patientId);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        Feedback feedback = feedbackService.getFeedbackByPatientAndAppointment(patient, appointment);

        FeedbackModel feedbackModel = feedback != null ? modelMapper.map(feedback, FeedbackModel.class) : new FeedbackModel();

        model.addAttribute("feedbackModel", feedbackModel);

        return "feedback";
    }

    @PostMapping("/appointments/feedback")
    public String postFeedback(@ModelAttribute("feedbackModel") @Valid FeedbackModel feedbackModel, BindingResult bindingResult,
                               @RequestParam Long doctorId,
                               @RequestParam Long patientId,
                               @RequestParam Long appointmentId,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("feedbackModel", feedbackModel);
            return "feedback";
        }

        feedbackModel.setDoctorId(doctorId);
        feedbackModel.setPatientId(patientId);
        feedbackModel.setAppointmentId(appointmentId);

        try {
            feedbackService.createFeedback(feedbackModel);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while submitting feedback.");
        }

        return "redirect:/appointments";
    }
}
