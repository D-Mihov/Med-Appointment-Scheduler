package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.PatientService;
import com.example.medappointmentscheduler.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public PatientServiceImpl(PatientRepository patientRepository, UserRepository userRepository, ModelMapper modelMapper, UserService userService) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }


    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with ID " + patientId + " not found!"));
    }
    @Override
    public Patient createPatient(SignupModel patientDTO) {
        Optional<User> user = userRepository.findByEmail(patientDTO.getEmail());

        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with email " + patientDTO.getEmail() + " not found!");
        }

        Patient patient = modelMapper.map(patientDTO, Patient.class);
        patient.setDateOfBirth(Date.valueOf(patientDTO.getDateOfBirth()));
        patient.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        patient.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        patient.setUser(user.get());

        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(Long patientId, SignupModel patientDTO) {
        Patient existingPatient = getPatientById(patientId);

        if (!(userService.getUserByEmail(existingPatient.getEmail()).getEmail().equals(patientDTO.getEmail()))) {
            userService.updateUserEmail(existingPatient.getEmail(), patientDTO.getEmail());
        }

        modelMapper.map(patientDTO, existingPatient);
        existingPatient.setDateOfBirth(Date.valueOf(patientDTO.getDateOfBirth()));
        existingPatient.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return patientRepository.save(existingPatient);
    }


    @Override
    public void deletePatient(Long patientId) {
        Patient patient = getPatientById(patientId);
        patientRepository.delete(patient);
    }

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("Patient with email " + email + " not found!"));
    }
}
