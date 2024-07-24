package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id);
    Doctor createDoctor(SignupDoctorModel doctorDTO);
    Doctor updateDoctor(Long id, SignupDoctorModel doctorDTO);
    void deleteDoctor(Long id);
    Doctor getDoctorByEmail(String email);
}
