package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.DoctorService;
import com.example.medappointmentscheduler.service.PatientService;
import com.example.medappointmentscheduler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    private final Utils utils;

    public HomeController(AppointmentService appointmentService, DoctorService doctorService, PatientService patientService, Utils utils) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.utils = utils;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {

        if (utils.isLoggedIn()) {
            String loggedInEmail = principal.getName();

            if (utils.hasRole("ROLE_DOCTOR")) {
                addDoctorAttributes(model, loggedInEmail);
            } else if (utils.hasRole("ROLE_PATIENT")) {
                addPatientAttributes(model, loggedInEmail);
            } else if (utils.hasRole("ROLE_ADMIN")) {
                addAdminAttributes(model);
            }
            return "homeAuth";
        }
        return "home";
    }

    private void addCommonAttributes(Model model, int appointmentCount, int scheduledAppointmentCount, int completedAppointmentCount, int canceledAppointmentCount, int feedbacksCount) {
        model.addAttribute("appointmentCount", appointmentCount);
        model.addAttribute("scheduledAppointmentCount", scheduledAppointmentCount);
        model.addAttribute("completedAppointmentCount", completedAppointmentCount);
        model.addAttribute("canceledAppointmentCount", canceledAppointmentCount);
        model.addAttribute("feedbacksCount", feedbacksCount);
    }

    private void addDoctorAttributes(Model model, String loggedInEmail) {
        int appointmentCount = appointmentService.getAppointmentCountForDoctor(loggedInEmail);
        int scheduledAppointmentCount = appointmentService.getScheduledAppointmentCountForDoctor(loggedInEmail);
        int completedAppointmentCount = appointmentService.getCompletedAppointmentCountForDoctor(loggedInEmail);
        int canceledAppointmentCount = appointmentService.getCanceledAppointmentCountForDoctor(loggedInEmail);
        int feedbacksCount = appointmentService.getFeedbacksCountForDoctor(loggedInEmail);
        String doctorFullName = doctorService.getDoctorByEmail(loggedInEmail).getFullName();

        model.addAttribute("doctorFullName", doctorFullName);
        addCommonAttributes(model, appointmentCount, scheduledAppointmentCount, completedAppointmentCount, canceledAppointmentCount, feedbacksCount);
    }

    private void addPatientAttributes(Model model, String loggedInEmail) {
        Patient patient = patientService.getPatientByEmail(loggedInEmail);
        String patientFullName = patient.getFullName();
        int appointmentCount = appointmentService.getAppointmentCountForPatient(loggedInEmail);
        int scheduledAppointmentCount = appointmentService.getScheduledAppointmentCountForPatient(loggedInEmail);
        int completedAppointmentCount = appointmentService.getCompletedAppointmentCountForPatient(loggedInEmail);
        int canceledAppointmentCount = appointmentService.getCanceledAppointmentCountForPatient(loggedInEmail);
        int feedbacksCount = appointmentService.getFeedbacksCountForPatient(loggedInEmail);

        model.addAttribute("patientFullName", patientFullName);
        addCommonAttributes(model, appointmentCount, scheduledAppointmentCount, completedAppointmentCount, canceledAppointmentCount, feedbacksCount);
    }

    private void addAdminAttributes(Model model) {
        int patientsCount = patientService.getAllPatients().size();
        int doctorsCount = doctorService.getAllDoctors().size();
        int appointmentCount = appointmentService.getAllAppointments().size();
        int scheduledAppointmentCount = appointmentService.getScheduledAppointmentCount();
        int completedAppointmentCount = appointmentService.getCompletedAppointmentCount();
        int canceledAppointmentCount = appointmentService.getCanceledAppointmentCount();
        int feedbacksCount = appointmentService.getFeedbacksCount();

        model.addAttribute("patientsCount", patientsCount);
        model.addAttribute("doctorsCount", doctorsCount);
        addCommonAttributes(model, appointmentCount, scheduledAppointmentCount, completedAppointmentCount, canceledAppointmentCount, feedbacksCount);
    }
}
