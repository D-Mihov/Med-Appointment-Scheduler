package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Patient;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.entity.enums.PatientGenderEnum;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.repository.PatientRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.PatientService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientServiceImpl(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new UsernameNotFoundException("Patient with ID: " + patientId + " not found!"));
    }
    @Override
    public Patient createPatient(SignupModel patientDTO) {
        Patient patient = new Patient();
        SetPatientDetails(patientDTO, patient);
        patient.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        patient.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Optional<User> user = userRepository.findByEmail(patientDTO.getEmail());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Patient " + patientDTO.getEmail() + " not found!");
        }

        User userEntity = user.get();

        patient.setUser(userEntity);

        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(Long patientId, SignupModel patientDTO) {
        Patient patient = getPatientById(patientId);
        SetPatientDetails(patientDTO, patient);
        patient.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return patientRepository.save(patient);
    }


    @Override
    public void deletePatient(Long patientId) {
        Patient patient = getPatientById(patientId);
        patientRepository.delete(patient);
    }

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email).orElse(null);
    }

    private void SetPatientDetails(SignupModel patientDTO, Patient patient) {
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setDateOfBirth(java.sql.Date.valueOf(patientDTO.getDateOfBirth()));
        patient.setGender(PatientGenderEnum.valueOf(patientDTO.getGender()));
        patient.setMedicalHistory(patientDTO.getMedicalHistory());
    }
}
