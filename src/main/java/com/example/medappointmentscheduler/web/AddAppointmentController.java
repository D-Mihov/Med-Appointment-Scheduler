package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AddAppointmentController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public AddAppointmentController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/add-appointment")
    public String prepareAddAppointment(Model model) {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            model.addAttribute("doctors", doctors);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while fetching doctors.");
        }
        model.addAttribute("addAppointmentModel", new AddAppointmentModel());
        return "add-appointment";
    }

    @PostMapping("/add-appointment")
    public String addAppointment(Model model, @ModelAttribute("addAppointmentModel") @Valid AddAppointmentModel addAppointmentModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            attachAttributesToModel(model, addAppointmentModel);
            return "add-appointment";
        }

        addAppointmentModel.setStatus("Scheduled");

        try {
            String validAppointmentMessage = appointmentService.isValidAppointment(addAppointmentModel);

            if (!validAppointmentMessage.isEmpty()) {
                bindingResult.rejectValue("appointmentDate", null, validAppointmentMessage);
                attachAttributesToModel(model, addAppointmentModel);
                return "add-appointment";
            }

            appointmentService.createAppointment(addAppointmentModel);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while creating the appointment.");
        }

        return "redirect:/";
    }

    private void attachAttributesToModel(Model model, @ModelAttribute("addAppointmentModel") @Valid AddAppointmentModel addAppointmentModel) {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            model.addAttribute("doctors", doctors);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while preparing the model.");
        }
        addAppointmentModel.setDoctorId(null);
        addAppointmentModel.setAppointmentDate(null);
        model.addAttribute("addAppointmentModel", addAppointmentModel);
    }
}
