package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.enums.UserRoleEnum;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.UpdateDoctorModel;
import com.example.medappointmentscheduler.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    @WithUserDetails("admin@gmail.com")
    void testShowDoctors() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors"))
                .andExpect(model().attributeExists("doctors"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void testShowUpdateForm() throws Exception {
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        SignupDoctorModel signupDoctorModel = new SignupDoctorModel();

        when(doctorService.getDoctorById(doctorId)).thenReturn(doctor);
        when(modelMapper.map(doctor, SignupDoctorModel.class)).thenReturn(signupDoctorModel);

        mockMvc.perform(get("/doctors/{id}/edit", doctorId))
                .andExpect(status().isOk())
                .andExpect(view().name("update-doctor-form"))
                .andExpect(model().attributeExists("signupDoctorModel"))
                .andExpect(model().attribute("signupDoctorModel", signupDoctorModel));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void testUpdateDoctor_success() throws Exception {
        Long doctorId = 1L;
        SignupDoctorModel signupDoctorModel = new SignupDoctorModel();
        signupDoctorModel.setUserRole(UserRoleEnum.DOCTOR.name());
        UpdateDoctorModel updateDoctorModel = new UpdateDoctorModel();

        when(modelMapper.map(updateDoctorModel, SignupDoctorModel.class)).thenReturn(signupDoctorModel);

        mockMvc.perform(post("/doctors/{id}/edit", doctorId)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "test@email.com")
                        .param("phone", "0123456789")
                        .param("speciality", "Doe")
                        .param("experience", "12")
                        .param("doctorId", "AB1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void testUpdateDoctor_bindingErrors() throws Exception {
        Long doctorId = 1L;

        mockMvc.perform(post("/doctors/{id}/edit", doctorId)
                        .param("firstName", "")
                        .param("lastName", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("update-doctor-form"))
                .andExpect(model().attributeHasFieldErrors("signupDoctorModel", "firstName", "lastName"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void testDeleteDoctor_success() throws Exception {
        Long doctorId = 1L;

        mockMvc.perform(get("/doctors/delete").param("id", doctorId.toString()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));

        verify(doctorService).deleteDoctor(doctorId);
    }
}
