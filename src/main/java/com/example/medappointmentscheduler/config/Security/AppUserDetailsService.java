package com.example.medappointmentscheduler.config.Security;

import com.example.medappointmentscheduler.domain.entity.User;
import com.example.medappointmentscheduler.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Looking for user: " + username);

        Optional<User> user = this.userRepository
                .findByEmail(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }

        User userEntity = user.get();

        return new CustomUserDetails(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().toString())));
    }
}
