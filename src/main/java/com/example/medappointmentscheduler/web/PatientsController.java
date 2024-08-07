package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.enums.UserRoleEnum;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.domain.model.UpdatePatientModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.service.PatientService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@Controller
public class PatientsController {
    private final PatientService patientService;
    private final ModelMapper modelMapper;

    public PatientsController(PatientService patientService, ModelMapper modelMapper) {
        this.patientService = patientService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/patients")
    public String showPatients(Model model) {
        try {
            List<Patient> patients = patientService.getAllPatients();
            model.addAttribute("patients", patients);
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while fetching patients.");
        }

        return "patients";
    }

    @GetMapping("/patients/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        try {
            Patient patient = patientService.getPatientById(id);
            SignupModel signupModel = modelMapper.map(patient, SignupModel.class);
            signupModel.setDateOfBirth(patient.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            model.addAttribute("signupModel", signupModel);
            return "update-patient-form";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while preparing the update form.");
        }
    }

    @PostMapping("/patients/{id}/edit")
    public String updatePatient(@PathVariable("id") Long id, @ModelAttribute("signupModel") @Valid UpdatePatientModel updatePatientModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update-patient-form";
        }

        try{
            SignupModel signupModel = modelMapper.map(updatePatientModel, SignupModel.class);
            signupModel.setUserRole(UserRoleEnum.PATIENT.name());
            patientService.updatePatient(id, signupModel);
            return "redirect:/patients";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while updating the patient.");
        }
    }

    @GetMapping("/patients/delete")
    public String deletePatient(@RequestParam("id") Long id) {
        try {
            patientService.deletePatient(id);
            return "redirect:/patients";
        } catch (Exception e) {
            throw new CustomServerException("An error occurred while deleting the doctor.");
        }
    }
}
