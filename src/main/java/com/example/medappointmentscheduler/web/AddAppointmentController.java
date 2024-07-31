package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
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
        AddAppointmentModel addAppointmentModel = new AddAppointmentModel();
        List<Doctor> doctors = doctorService.getAllDoctors();

        model.addAttribute("doctors", doctors);
        model.addAttribute("addAppointmentModel", addAppointmentModel);

        return "add-appointment";
    }

    @PostMapping("/add-appointment")
    public String addAppointment(Model model, @ModelAttribute("addAppointmentModel") @Valid AddAppointmentModel addAppointmentModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            attachAttributesToModel(model, addAppointmentModel);
            return "add-appointment";
        }

        addAppointmentModel.setStatus("Scheduled");

        String validAppointment = appointmentService.isValidAppointment(addAppointmentModel);

        if (!validAppointment.equals("")) {
            bindingResult.rejectValue("appointmentDate", null, validAppointment);
        }

        if (bindingResult.hasErrors()) {
            attachAttributesToModel(model, addAppointmentModel);
            return "add-appointment";
        }

        appointmentService.createAppointment(addAppointmentModel);

        return "redirect:/";
    }

    private void attachAttributesToModel(Model model, @ModelAttribute("addAppointmentModel") @Valid AddAppointmentModel addAppointmentModel) {
        List<Doctor> doctors = doctorService.getAllDoctors();

        model.addAttribute("doctors", doctors);
        addAppointmentModel.setDoctorId(null);
        addAppointmentModel.setAppointmentDate(null);
        model.addAttribute("addAppointmentModel", addAppointmentModel);
    }
}
