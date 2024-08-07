package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.Doctor;
import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.error.exceptions.ObjectNotFoundException;
import com.example.medappointmentscheduler.repository.DoctorRepository;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.DoctorService;
import com.example.medappointmentscheduler.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, ModelMapper modelMapper, UserService userService) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Doctor with ID " + id + " not found!"));
    }

    @Override
    public Doctor createDoctor(SignupDoctorModel doctorDTO) {
        Optional<User> user = userRepository.findByEmail(doctorDTO.getEmail());

        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with email " + doctorDTO.getEmail() + " not found!");
        }

        Doctor doctor = new Doctor();
        SetDoctorDetails(doctorDTO, doctor);
        doctor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        doctor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        doctor.setUser(user.get());

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Long id, SignupDoctorModel doctorDTO) {
        Doctor existingDoctor = getDoctorById(id);

        if (!(userService.getUserByEmail(existingDoctor.getEmail()).getEmail().equals(doctorDTO.getEmail()))) {
            userService.updateUserEmail(existingDoctor.getEmail(), doctorDTO.getEmail());
        }

        SetDoctorDetails(doctorDTO, existingDoctor);
        existingDoctor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return doctorRepository.save(existingDoctor);
    }


    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }

    @Override
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                        .orElseThrow(() -> new ObjectNotFoundException("Doctor with email " + email + " not found!"));
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
