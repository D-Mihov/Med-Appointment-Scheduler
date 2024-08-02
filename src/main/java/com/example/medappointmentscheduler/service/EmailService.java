package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("classpath:templates/email-templates/confirmation-email-template.html")
    private Resource confirmationEmailTemplateResource;

    @Value("classpath:templates/email-templates/notification-email-template.html")
    private Resource notificationEmailTemplateResource;

    @Value("classpath:templates/email-templates/confirmation-email-template-bg.html")
    private Resource confirmationEmailTemplateResourceBG;

    @Value("classpath:templates/email-templates/notification-email-template-bg.html")
    private Resource notificationEmailTemplateResourceBG;


    public void sendAppointmentConfirmationEmail(String recipientEmail, Appointment appointment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String locale = LocaleContextHolder.getLocale().getLanguage();

            helper.setTo(recipientEmail);

            helper.setSubject("Appointment Confirmation");

            SetEmailDetailsAndSend(appointment, message, helper, locale, confirmationEmailTemplateResourceBG, confirmationEmailTemplateResource);
        } catch (IOException | MessagingException e) {
            System.out.println("Error in sendAppointmentConfirmationEmail");
            e.printStackTrace();
        }
    }

    public void sendAppointmentNotificationEmail(String recipientEmail, Appointment appointment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String locale = LocaleContextHolder.getLocale().getLanguage();

            helper.setTo(recipientEmail);

            helper.setSubject("Appointment Notification");

            SetEmailDetailsAndSend(appointment, message, helper, locale, notificationEmailTemplateResourceBG, notificationEmailTemplateResource);
        } catch (IOException | MessagingException e) {
            System.out.println("Error in sendAppointmentNotificationEmail");
            e.printStackTrace();
        }
    }

    private void SetEmailDetailsAndSend(Appointment appointment, MimeMessage message, MimeMessageHelper helper, String locale, Resource emailTemplateResourceBG, Resource emailTemplateResource) throws IOException, MessagingException {
        String emailContent = "";

        if ("bg".equals(locale)) {
            emailContent = getEmailContent(appointment, emailTemplateResourceBG);
        } else if ("en".equals(locale)) {
            emailContent = getEmailContent(appointment, emailTemplateResource);
        }

        helper.setText(emailContent, true);

        javaMailSender.send(message);
    }

    private String getEmailContent(Appointment appointment, Resource resource) throws IOException {
        String emailTemplate = Files.readString(resource.getFile().toPath());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        return String.format(
                emailTemplate,
                appointment.getPatientFullName(),
                dateFormat.format(appointment.getAppointmentDate()),
                appointment.getDoctor().getFullName(),
                dateFormat.format(appointment.getAppointmentDate()),
                timeFormat.format(appointment.getAppointmentHour())
        );
    }
}
