package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.error.exceptions.CustomServerException;
import com.example.medappointmentscheduler.service.PatientService;
import com.example.medappointmentscheduler.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PatientService patientService;

    @Test
    void testPrepareSignup() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("signupModel"));
    }

    @Test
    void testSignup_ValidData() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("firstName", "test")
                        .param("lastName", "testov")
                        .param("email", "test@mail.com")
                        .param("password", "test123")
                        .param("confirmPassword", "test123")
                        .param("phone", "0888888888")
                        .param("dateOfBirth", "2000-02-02")
                        .param("gender", "Male")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testSignup_PasswordMismatch() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("firstName", "test")
                        .param("lastName", "testov")
                        .param("email", "test@mail.com")
                        .param("password", "test123")
                        .param("confirmPassword", "test124124")
                        .param("phone", "0888888888")
                        .param("dateOfBirth", "2000-02-02")
                        .param("gender", "Male")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("signupModel", "password", "confirmPassword"))
                .andExpect(model().attributeHasFieldErrorCode("signupModel", "password", "form.password.nomatch"))
                .andExpect(model().attributeHasFieldErrorCode("signupModel", "confirmPassword", "form.password.nomatch"));
    }

    @Test
    void testSignup_WithValidationErrors() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("firstName", "")
                        .param("lastName", "testov")
                        .param("email", "invalid-email")
                        .param("password", "test123")
                        .param("confirmPassword", "test123")
                        .param("phone", "0888888888")
                        .param("dateOfBirth", "2000-02-02")
                        .param("gender", "Male")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("signupModel", "firstName", "email"));
    }

    @Test
    void testSignup_ExceptionHandling() throws Exception {
        doThrow(new CustomServerException("Error during registration process."))
                .when(userService).createPatientUser(any(SignupModel.class));

        mockMvc.perform(post("/signup")
                        .param("firstName", "test")
                        .param("lastName", "testov")
                        .param("email", "test@mail.com")
                        .param("password", "test123")
                        .param("confirmPassword", "test123")
                        .param("phone", "0888888888")
                        .param("dateOfBirth", "2000-02-02")
                        .param("gender", "Male")
                        .with(csrf()))
                .andExpect(status().is5xxServerError());
    }
}
