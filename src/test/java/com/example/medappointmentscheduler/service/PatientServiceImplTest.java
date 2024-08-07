package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(patientRepository, userRepository, modelMapper, userService);
    }

    @Test
    void getAllPatients_success() {
        List<Patient> patients = List.of(new Patient(), new Patient());
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertEquals(patients.size(), result.size());
        verify(patientRepository).findAll();
    }

    @Test
    void getPatientById_success() {
        Long patientId = 1L;
        Patient patient = new Patient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(patientId);

        assertNotNull(result);
        assertEquals(patient, result);
        verify(patientRepository).findById(patientId);
    }

    @Test
    void getPatientById_notFound() {
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> patientService.getPatientById(patientId));
        verify(patientRepository).findById(patientId);
    }

    @Test
    void createPatient_success() {
        SignupModel patientDTO = new SignupModel();
        patientDTO.setEmail("patient@gmail.com");
        patientDTO.setDateOfBirth(LocalDate.parse("2000-01-01"));

        User user = new User();
        Patient patient = new Patient();

        when(userRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.of(user));
        when(modelMapper.map(patientDTO, Patient.class)).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        assertEquals(patient, result);
        verify(userRepository).findByEmail(patientDTO.getEmail());
        verify(modelMapper).map(patientDTO, Patient.class);
        verify(patientRepository).save(patient);
    }

    @Test
    void createPatient_userNotFound() {
        SignupModel patientDTO = new SignupModel();
        patientDTO.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> patientService.createPatient(patientDTO));
        verify(userRepository).findByEmail(patientDTO.getEmail());
    }

    @Test
    void updatePatient_success() {
        Long patientId = 1L;
        String oldEmail = "old-patient@example.com";
        String newEmail = "patient@example.com";

        SignupModel patientDTO = new SignupModel();
        patientDTO.setEmail(newEmail);
        patientDTO.setDateOfBirth(LocalDate.parse("2000-02-02"));

        Patient existingPatient = new Patient();
        existingPatient.setEmail(oldEmail);

        User existingUser = new User();
        existingUser.setEmail(oldEmail);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(userService.getUserByEmail(oldEmail)).thenReturn(existingUser);
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);

        Patient result = patientService.updatePatient(patientId, patientDTO);

        assertNotNull(result);
        verify(userService).getUserByEmail(oldEmail);
        verify(userService).updateUserEmail(oldEmail, newEmail);
        verify(modelMapper).map(patientDTO, existingPatient);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    void updatePatient_notFound() {
        Long patientId = 1L;
        SignupModel patientDTO = new SignupModel();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> patientService.updatePatient(patientId, patientDTO));
        verify(patientRepository).findById(patientId);
    }

    @Test
    void deletePatient_success() {
        Long patientId = 1L;
        Patient patient = new Patient();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        patientService.deletePatient(patientId);

        verify(patientRepository).delete(patient);
    }

    @Test
    void deletePatient_notFound() {
        Long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> patientService.deletePatient(patientId));
        verify(patientRepository).findById(patientId);
    }

    @Test
    void getPatientByEmail_success() {
        String email = "patient@example.com";
        Patient patient = new Patient();

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByEmail(email);

        assertNotNull(result);
        assertEquals(patient, result);
        verify(patientRepository).findByEmail(email);
    }

    @Test
    void getPatientByEmail_notFound() {
        String email = "nonexistent@example.com";

        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> patientService.getPatientByEmail(email));
        verify(patientRepository).findByEmail(email);
    }
}
