package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentRestController {
    private final AppointmentService appointmentService;

    public AppointmentRestController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/getAvailableHours")
    public ResponseEntity<List<String>> getAvailableHours(@RequestParam Long doctorId, @RequestParam String appointmentDate) {
        List<String> availableHours = appointmentService.getAvailableHoursForDoctor(doctorId, appointmentDate);

        return ResponseEntity.ok(availableHours);
    }
}
