package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.domain.model.UpdatePatientModel;
import com.example.medappointmentscheduler.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@Controller
public class PatientsController {
    private final PatientService patientService;

    public PatientsController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public String showPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        SignupModel signupModel = getPatientDTO(patient);
        model.addAttribute("signupModel", signupModel);

        return "update-patient-form";
    }

    @PostMapping("/patients/{id}/edit")
    public String updatePatient(@PathVariable("id") Long id, @ModelAttribute("signupModel") @Valid UpdatePatientModel updatePatientModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update-patient-form";
        }

        SignupModel signupModel = setUpdateModelToSignupModel(updatePatientModel);
        patientService.updatePatient(id, signupModel);

        return "redirect:/patients";
    }

    @GetMapping("/patients/delete")
    public String deletePatient(@RequestParam("id") Long id) {
        patientService.deletePatient(id);

        return "redirect:/patients";
    }

    private SignupModel setUpdateModelToSignupModel(UpdatePatientModel updatePatientModel) {
        SignupModel signupModel = new SignupModel();

        signupModel.setFirstName(updatePatientModel.getFirstName());
        signupModel.setLastName(updatePatientModel.getLastName());
        signupModel.setEmail(updatePatientModel.getEmail());
        signupModel.setPhone(updatePatientModel.getPhone());
        signupModel.setDateOfBirth(updatePatientModel.getDateOfBirth());
        signupModel.setGender(updatePatientModel.getGender());
        signupModel.setMedicalHistory(updatePatientModel.getMedicalHistory());

        return signupModel;
    }

    private SignupModel getPatientDTO(Patient patient) {
        SignupModel signupModel = new SignupModel();

        signupModel.setFirstName(patient.getFirstName());
        signupModel.setLastName(patient.getLastName());
        signupModel.setEmail(patient.getEmail());
        signupModel.setPhone(patient.getPhone());
        signupModel.setDateOfBirth(patient.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        signupModel.setGender(patient.getGender().toString());
        signupModel.setMedicalHistory(patient.getMedicalHistory());

        return signupModel;
    }
}
