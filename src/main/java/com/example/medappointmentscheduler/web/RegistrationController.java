package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.service.PatientService;
import com.example.medappointmentscheduler.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final PatientService patientService;

    public RegistrationController(UserService userService, PatientService patientService) {
        this.userService = userService;
        this.patientService = patientService;
    }

    @GetMapping("/signup")
    public String prepareSignup(Model model) {
        SignupModel signupModel = new SignupModel();

        model.addAttribute("signupModel", signupModel);

        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupModel") @Valid SignupModel signupModel, BindingResult bindingResult, Model model) {
        signupModel.setUserRole("PATIENT");

        if (!signupModel.getPassword().equals(signupModel.getConfirmPassword())) {
            bindingResult.rejectValue("password", "form.password.nomatch");
            bindingResult.rejectValue("confirmPassword", "form.password.nomatch");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        this.userService.createPatientUser(signupModel);
        this.patientService.createPatient(signupModel);

        return "redirect:/";
    }
}
