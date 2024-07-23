package com.example.medappointmentscheduler.service.impl;

import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.domain.entity.enums.UserRoleEnum;
import com.example.medappointmentscheduler.domain.model.SignupDoctorModel;
import com.example.medappointmentscheduler.domain.model.SignupModel;
import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPass;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${admin.email}") String adminEmail,
                           @Value("${admin.password}") String adminPass) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPass = adminPass;
    }

    public void initAdmin() {
        if (userRepository.count() == 0) {
            createUser(adminEmail, adminPass, UserRoleEnum.ADMIN.toString());
        }
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + email + " not found!");
        }

        User userEntity = user.get();

        if (!passwordEncoder.matches(oldPassword, userEntity.getPassword())) {
            return false;
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);
        userEntity.setPassword(newPasswordHash);

        userRepository.save(userEntity);

        return true;
    }

    @Override
    public User createPatientUser(SignupModel signupModel) {
        return createUser(signupModel.getEmail(), signupModel.getPassword(), signupModel.getUserRole());
    }

    @Override
    public User createDoctorUser(SignupDoctorModel signupDoctorModel) {
        return createUser(signupDoctorModel.getEmail(), signupDoctorModel.getPassword(), signupDoctorModel.getUserRole());
    }

    private User createUser(String email, String password, String userRole) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRoleEnum.valueOf(userRole));
        user.setLastLogin(new Timestamp(System.currentTimeMillis()));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        log.debug("Looking for user: " + email);

        Optional<User> user = this.userRepository
                .findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + email + " not found!");
        }

        return user.get();
    }

    @Override
    public void deleteUser(String email) {
        Optional<User> user = this.userRepository
                .findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + email + " not found!");
        }

        User userEntity = user.get();

        userRepository.delete(userEntity);
    }

    @Override
    public void updateUser(SignupModel signupModel) {
        Optional<User> user = this.userRepository
                .findByEmail(signupModel.getEmail());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + signupModel.getEmail() + " not found!");
        }

        User userEntity = user.get();
        userEntity.setPassword(passwordEncoder.encode(signupModel.getPassword()));
        userEntity.setEmail(signupModel.getEmail());
        userEntity.setRole(UserRoleEnum.valueOf(signupModel.getUserRole()));
        userEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(userEntity);
    }
}
