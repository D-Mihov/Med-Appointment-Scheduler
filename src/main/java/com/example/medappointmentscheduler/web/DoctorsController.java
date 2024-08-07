package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.enums.UserRoleEnum;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.UpdateDoctorModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.service.DoctorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DoctorsController {
    private final DoctorService doctorService;
    private final ModelMapper modelMapper;

    public DoctorsController(DoctorService doctorService, ModelMapper modelMapper) {
        this.doctorService = doctorService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/doctors")
    public String showDoctors(Model model) {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            model.addAttribute("doctors", doctors);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while fetching doctors.");
        }

        return "doctors";
    }

    @GetMapping("/doctors/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            SignupDoctorModel signupDoctorModel = modelMapper.map(doctor, SignupDoctorModel.class);
            model.addAttribute("signupDoctorModel", signupDoctorModel);
            return "update-doctor-form";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while preparing the update form.");
        }
    }

    @PostMapping("/doctors/{id}/edit")
    public String updateDoctor(@PathVariable("id") Long id, @ModelAttribute("signupDoctorModel") @Valid UpdateDoctorModel updateDoctorModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update-doctor-form";
        }

        try {
            SignupDoctorModel signupDoctorModel = new SignupDoctorModel();
            modelMapper.map(updateDoctorModel, signupDoctorModel);
            signupDoctorModel.setUserRole(UserRoleEnum.DOCTOR.name());
            doctorService.updateDoctor(id, signupDoctorModel);
            return "redirect:/doctors";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while updating the doctor.");
        }
    }

    @GetMapping("/doctors/delete")
    public String deleteDoctor(@RequestParam("id") Long id) {
        try {
            doctorService.deleteDoctor(id);
            return "redirect:/doctors";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while deleting the doctor.");
        }
    }
}
