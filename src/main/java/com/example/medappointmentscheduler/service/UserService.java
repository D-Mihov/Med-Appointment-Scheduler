package com.example.medappointmentscheduler.service;

import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    User createPatientUser(SignupModel signupModel);

    User createDoctorUser(SignupDoctorModel signupDoctorModel);

    User getUserByEmail(String email);

    void deleteUser(String username);

    void updateUser(SignupModel signupModel);

    void initAdmin();

    boolean changePassword(String email, String oldPassword, String newPassword);

    void updateUserEmail(String oldEmail, String email);
}
