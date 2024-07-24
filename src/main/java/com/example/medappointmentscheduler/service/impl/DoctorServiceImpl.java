package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.DoctorService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor with ID: " + id + " not found!"));
    }

    @Override
    public Doctor createDoctor(SignupDoctorModel doctorDTO) {
        Doctor doctor = new Doctor();
        SetDoctorDetails(doctorDTO, doctor);
        doctor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        doctor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Optional<User> user = userRepository.findByEmail(doctorDTO.getEmail());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Doctor " + doctorDTO.getEmail() + " not found!");
        }

        User userEntity = user.get();

        doctor.setUser(userEntity);

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Long id, SignupDoctorModel doctorDTO) {
        Doctor doctor = getDoctorById(id);
        SetDoctorDetails(doctorDTO, doctor);
        doctor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return doctorRepository.save(doctor);
    }


    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }

    @Override
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email).orElse(null);
    }

    private void SetDoctorDetails(SignupDoctorModel doctorDTO, Doctor doctor) {
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setPhone(doctorDTO.getPhone());
        doctor.setSpeciality(doctorDTO.getSpeciality());
        doctor.setExperience(Integer.parseInt(doctorDTO.getExperience()));
        doctor.setEducation(doctorDTO.getEducation());
        doctor.setDoctorId(doctorDTO.getDoctorId());
    }
}
