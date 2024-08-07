package com.example.medappointmentscheduler.web.REST;

import com.example.medappointmentscheduler.domain.REST.Prescription;
import com.example.medappointmentscheduler.service.REST.PrescriptionClient;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments/prescription")
public class PrescriptionController {

    private final PrescriptionClient prescriptionClient;

    public PrescriptionController(PrescriptionClient prescriptionClient) {
        this.prescriptionClient = prescriptionClient;
    }

    @GetMapping
    public String getPrescriptionByPatientId(@RequestParam Long appointmentId, @RequestParam Long patientId, Model model) {
        Prescription prescription = prescriptionClient.getPrescriptionByPatientId(appointmentId, patientId);
        model.addAttribute("prescription", prescription);
        return "prescription";
    }

    @PostMapping
    public String addPrescription(@ModelAttribute("prescription") @Valid Prescription prescription, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "prescription";
        }

        prescriptionClient.addPrescription(prescription);
        return "redirect:/appointments";
    }

    @GetMapping("/delete")
    public String deletePrescription(@RequestParam Long appointmentId, @RequestParam Long patientId) {
        prescriptionClient.deletePrescription(appointmentId, patientId);
        return "redirect:/appointments";
    }
}
