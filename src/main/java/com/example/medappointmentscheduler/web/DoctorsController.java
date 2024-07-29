package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.UpdateDoctorModel;
import com.example.medappointmentscheduler.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DoctorsController {
    private final DoctorService doctorService;

    public DoctorsController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doctors")
    public String showDoctors(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "doctors";
    }

    @GetMapping("/doctors/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        SignupDoctorModel signupDoctorModel = getDoctorDTO(doctor);
        model.addAttribute("signupDoctorModel", signupDoctorModel);

        return "update-doctor-form";
    }

    @PostMapping("/doctors/{id}/edit")
    public String updateDoctor(@PathVariable("id") Long id, @ModelAttribute("signupDoctorModel") @Valid UpdateDoctorModel updateDoctorModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update-doctor-form";
        }

        SignupDoctorModel signupDoctorModel = setUpdateModelToSignupModel(updateDoctorModel);
        doctorService.updateDoctor(id, signupDoctorModel);

        return "redirect:/doctors";
    }

    @GetMapping("/doctors/delete")
    public String deleteDoctor(@RequestParam("id") Long id) {
        doctorService.deleteDoctor(id);

        return "redirect:/doctors";
    }

    private SignupDoctorModel setUpdateModelToSignupModel(UpdateDoctorModel updateDoctorModel) {
        SignupDoctorModel signupDoctorModel = new SignupDoctorModel();
        signupDoctorModel.setFirstName(updateDoctorModel.getFirstName());
        signupDoctorModel.setLastName(updateDoctorModel.getLastName());
        signupDoctorModel.setEmail(updateDoctorModel.getEmail());
        signupDoctorModel.setPhone(updateDoctorModel.getPhone());
        signupDoctorModel.setExperience(updateDoctorModel.getExperience());
        signupDoctorModel.setEducation(updateDoctorModel.getEducation());
        signupDoctorModel.setSpeciality(updateDoctorModel.getSpeciality());
        signupDoctorModel.setDoctorId(updateDoctorModel.getDoctorId());

        return signupDoctorModel;
    }

    private SignupDoctorModel getDoctorDTO(Doctor doctor) {
        SignupDoctorModel signupDoctorModel = new SignupDoctorModel();

        signupDoctorModel.setFirstName(doctor.getFirstName());
        signupDoctorModel.setLastName(doctor.getLastName());
        signupDoctorModel.setEmail(doctor.getEmail());
        signupDoctorModel.setPhone(doctor.getPhone());
        signupDoctorModel.setExperience(String.valueOf(doctor.getExperience()));
        signupDoctorModel.setEducation(doctor.getEducation());
        signupDoctorModel.setSpeciality(doctor.getSpeciality());
        signupDoctorModel.setDoctorId(doctor.getDoctorId());

        return signupDoctorModel;
    }
}
