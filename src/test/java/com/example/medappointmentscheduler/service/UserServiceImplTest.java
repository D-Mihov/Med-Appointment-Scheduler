package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.entity.enums.UserRoleEnum;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = NoOpPasswordEncoder.getInstance();
        userService = new UserServiceImpl(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    void initAdmin_usersExist_doesNotCreateAdminUser() {
        when(userRepository.count()).thenReturn(1L);

        userService.initAdmin();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_userNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.changePassword("test@example.com", "oldPassword", "newPassword"));

        assertEquals("User with email test@example.com not found!", exception.getMessage());
    }

    @Test
    void changePassword_oldPasswordDoesNotMatch() {
        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean result = userService.changePassword("test@example.com", "wrongOldPassword", "newPassword");

        assertFalse(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void changePassword_success() {
        User user = new User();
        user.setPassword("oldPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean result = userService.changePassword("test@example.com", "oldPassword", "newPassword");

        assertTrue(result);
        verify(userRepository).save(user);

        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void createPatientUser_success() {
        SignupModel signupModel = new SignupModel();
        signupModel.setEmail("patient@example.com");
        signupModel.setPassword("password");

        User user = new User();
        user.setEmail("patient@example.com");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createPatientUser(signupModel);

        assertNotNull(createdUser);
        assertEquals("password", createdUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createDoctorUser_success() {
        SignupDoctorModel signupDoctorModel = new SignupDoctorModel();
        signupDoctorModel.setEmail("doctor@example.com");
        signupDoctorModel.setPassword("password");

        User user = new User();
        user.setEmail("doctor@example.com");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createDoctorUser(signupDoctorModel);

        assertNotNull(createdUser);
        assertEquals("password", createdUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserByEmail_success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_notFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ObjectNotFoundException thrown = assertThrows(
                ObjectNotFoundException.class,
                () -> userService.getUserByEmail(email),
                "Expected getUserByEmail() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User with email " + email + " not found!"));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void deleteUser_success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.deleteUser(email);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ObjectNotFoundException thrown = assertThrows(
                ObjectNotFoundException.class,
                () -> userService.deleteUser(email),
                "Expected deleteUser() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User with email " + email + " not found!"));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void updateUser_success() {
        String email = "test@example.com";
        SignupModel signupModel = new SignupModel();
        signupModel.setEmail(email);
        signupModel.setPassword("newPassword");

        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        userService.updateUser(signupModel);

        verify(userRepository).save(existingUser);
        assertNotNull(existingUser.getUpdatedAt());
    }

    @Test
    void updateUser_notFound() {
        SignupModel signupModel = new SignupModel();
        signupModel.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(signupModel.getEmail())).thenReturn(Optional.empty());

        ObjectNotFoundException thrown = assertThrows(
                ObjectNotFoundException.class,
                () -> userService.updateUser(signupModel),
                "Expected updateUser() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User with email " + signupModel.getEmail() + " not found!"));
        verify(userRepository, never()).save(any(User.class));
    }
}
