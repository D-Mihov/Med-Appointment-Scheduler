package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorServiceImpl(doctorRepository, userRepository, modelMapper, userService);
    }

    @Test
    void getAllDoctors_success() {
        List<Doctor> doctors = List.of(new Doctor(), new Doctor());
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorService.getAllDoctors();

        assertEquals(doctors.size(), result.size());
        verify(doctorRepository).findAll();
    }

    @Test
    void getDoctorById_success() {
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorById(doctorId);

        assertNotNull(result);
        assertEquals(doctor, result);
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void getDoctorById_notFound() {
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> doctorService.getDoctorById(doctorId));
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void createDoctor_success() {
        SignupDoctorModel doctorDTO = new SignupDoctorModel();
        doctorDTO.setEmail("doctor@example.com");
        doctorDTO.setExperience("12");

        User user = new User();
        Doctor doctor = new Doctor();

        when(userRepository.findByEmail(doctorDTO.getEmail())).thenReturn(Optional.of(user));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.createDoctor(doctorDTO);

        assertNotNull(result);
        assertEquals(doctor, result);
        verify(userRepository).findByEmail(doctorDTO.getEmail());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void createDoctor_userNotFound() {
        SignupDoctorModel doctorDTO = new SignupDoctorModel();
        doctorDTO.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(doctorDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> doctorService.createDoctor(doctorDTO));
        verify(userRepository).findByEmail(doctorDTO.getEmail());
    }

    @Test
    void updateDoctor_success() {
        Long doctorId = 1L;
        String oldEmail = "old-doctor@example.com";
        String newEmail = "doctor@example.com";
        SignupDoctorModel doctorDTO = new SignupDoctorModel();
        doctorDTO.setEmail(newEmail);
        doctorDTO.setExperience("12");

        Doctor existingDoctor = new Doctor();
        existingDoctor.setEmail(oldEmail);

        User existingUser = new User();
        existingUser.setEmail(oldEmail);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(userService.getUserByEmail(oldEmail)).thenReturn(existingUser);
        when(doctorRepository.save(existingDoctor)).thenReturn(existingDoctor);

        Doctor result = doctorService.updateDoctor(doctorId, doctorDTO);

        assertNotNull(result);
        verify(userService).getUserByEmail(oldEmail);
        verify(userService).updateUserEmail(oldEmail, newEmail);
        verify(doctorRepository).save(existingDoctor);
    }

    @Test
    void updateDoctor_notFound() {
        Long doctorId = 1L;
        SignupDoctorModel doctorDTO = new SignupDoctorModel();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> doctorService.updateDoctor(doctorId, doctorDTO));
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void deleteDoctor_success() {
        Long doctorId = 1L;
        Doctor doctor = new Doctor();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        doctorService.deleteDoctor(doctorId);

        verify(doctorRepository).delete(doctor);
    }

    @Test
    void deleteDoctor_notFound() {
        Long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> doctorService.deleteDoctor(doctorId));
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void getDoctorByEmail_success() {
        String email = "doctor@example.com";
        Doctor doctor = new Doctor();

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorByEmail(email);

        assertNotNull(result);
        assertEquals(doctor, result);
        verify(doctorRepository).findByEmail(email);
    }

    @Test
    void getDoctorByEmail_notFound() {
        String email = "nonexistent@example.com";

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> doctorService.getDoctorByEmail(email));
        verify(doctorRepository).findByEmail(email);
    }
}