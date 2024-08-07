package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.model.AddAppointmentModel;
import com.example.medappointmentscheduler.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testPrepareAddAppointment() throws Exception {
        this.mockMvc.perform(get("/add-appointment"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-appointment"))
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("addAppointmentModel"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testAddAppointment_success() throws Exception {

        when(appointmentService.isValidAppointment(any(AddAppointmentModel.class))).thenReturn("");

        mockMvc.perform(post("/add-appointment")
                        .param("patientFullName", "Daniel Mihov")
                        .param("patientEmail", "daniel.mihovv@gmail.com")
                        .param("doctorId", "1")
                        .param("appointmentDate", "2024-08-15")
                        .param("appointmentHour", "10:00")
                        .param("diseases", "cold")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testAddAppointment_validationErrors() throws Exception {
        mockMvc.perform(post("/add-appointment")
                        .param("patientFullName", "")
                        .param("patientEmail", "daniel@gmail.com")
                        .param("doctorId", "1")
                        .param("appointmentDate", "2024-08-15")
                        .param("appointmentHour", "10:00")
                        .param("diseases", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-appointment"))
                .andExpect(model().attributeHasFieldErrors("addAppointmentModel", "patientFullName", "diseases"));

    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testAddAppointment_invalidAppointment() throws Exception {

        when(appointmentService.isValidAppointment(any(AddAppointmentModel.class))).thenReturn("Invalid appointment details");

        mockMvc.perform(post("/add-appointment")
                        .param("patientFullName", "Daniel Mihov")
                        .param("patientEmail", "daniel.mihovv@gmail.com")
                        .param("doctorId", "1")
                        .param("appointmentDate", "2024-08-15")
                        .param("appointmentHour", "10:00")
                        .param("diseases", "cold")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-appointment"))
                .andExpect(model().attributeHasFieldErrors("addAppointmentModel", "appointmentDate"));

    }
}
