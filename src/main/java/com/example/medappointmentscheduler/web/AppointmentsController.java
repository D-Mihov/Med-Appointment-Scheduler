package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.utils.Utils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class AppointmentsController {
    private final AppointmentService appointmentService;

    private final Utils utils;

    public AppointmentsController(AppointmentService appointmentService, Utils utils) {
        this.appointmentService = appointmentService;
        this.utils = utils;
    }

    @GetMapping("/appointments")
    public String showAppointments(Model model, Principal principal) {
        String loggedInEmail = principal.getName();

        List<Appointment> appointments;

        try {
            if (utils.hasRole("ROLE_ADMIN")) {
                appointments = appointmentService.getAllAppointments();
            } else if (utils.hasRole("ROLE_DOCTOR")) {
                appointments = appointmentService.getAppointmentsByDoctor(loggedInEmail);
            } else if (utils.hasRole("ROLE_PATIENT")) {
                appointments = appointmentService.getAppointmentsByPatient(loggedInEmail);
            } else {
                appointments = Collections.emptyList();
            }
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while retrieving appointments.");
        }

        model.addAttribute("appointments", appointments);

        return "appointments";
    }

    @GetMapping("/appointments/complete")
    public String completeAppointment(@RequestParam("id") Long id) {
        try {
            appointmentService.completeAppointment(id);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while completing the appointment.");
        }

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/cancel")
    public String cancelAppointment(@RequestParam("id") Long id) {
        try {
            appointmentService.cancelAppointment(id);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while canceling the appointment.");
        }

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/delete")
    public String deleteAppointment(@RequestParam("id") Long id) {
        try {
            appointmentService.deleteAppointment(id);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while deleting the appointment.");
        }

        return "redirect:/appointments";
    }
}
