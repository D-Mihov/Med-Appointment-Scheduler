package com.example.medappointmentscheduler.config;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import com.example.medappointmentscheduler.service.AppointmentService;
import com.example.medappointmentscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Autowired
    AppointmentService appointmentService;

    @Autowired
    EmailService emailService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 15 * * *")
    public void sendAppointmentNotifications() {
        System.out.println("sendAppointmentNotifications invoked. The time is now " + dateFormat.format(new Date()));
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentService.getAppointmentsForDate(tomorrow, "Scheduled");

        for (Appointment appointment : appointments) {
            System.out.println(appointment.toString());
            String patientEmail = appointment.getPatient().getEmail();
            try {
                emailService.sendAppointmentNotificationEmail(patientEmail, appointment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
