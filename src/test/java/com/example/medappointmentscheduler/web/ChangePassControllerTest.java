package com.example.medappointmentscheduler.web;

import com.example.medappointmentscheduler.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChangePassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testChangePassPage() throws Exception {
        this.mockMvc.perform(get("/changePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeExists("changePasswordModel"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testChangePassword_success() throws Exception {
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/changePassword")
                        .param("oldPassword", "oldPassword123")
                        .param("newPassword", "newPassword123")
                        .param("confirmNewPassword", "newPassword123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testChangePassword_passwordMismatch() throws Exception {
        mockMvc.perform(post("/changePassword")
                        .param("oldPassword", "oldPassword123")
                        .param("newPassword", "newPassword123")
                        .param("confirmNewPassword", "differentPassword123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeHasFieldErrors("changePasswordModel", "newPassword", "confirmNewPassword"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testChangePassword_oldPasswordIncorrect() throws Exception {
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/changePassword")
                        .param("oldPassword", "wrongOldPassword")
                        .param("newPassword", "newPassword123")
                        .param("confirmNewPassword", "newPassword123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeHasFieldErrors("changePasswordModel", "oldPassword"));
    }

    @Test
    @WithUserDetails("daniel.mihovv@gmail.com")
    void testChangePassword_bindingErrors() throws Exception {
        mockMvc.perform(post("/changePassword")
                        .param("oldPassword", "")
                        .param("newPassword", "newPassword123")
                        .param("confirmNewPassword", "newPassword123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeHasFieldErrors("changePasswordModel", "oldPassword"));
    }
}
