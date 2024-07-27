package com.example.medappointmentscheduler.config.Security;

import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.security.CustomAuthenticationFailureHandler;
import com.example.medappointmentscheduler.security.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Autowired
//    private CustomAuthenticationSuccessHandler successHandler;

    private final CustomAuthenticationFailureHandler failureHandler;

    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/signupDoctor", "/patients/**", "/doctors/**").hasRole("ADMIN")
                .requestMatchers("/add-appointment", "/getAvailableHours/**", "/appointments/feedback/**").hasRole("PATIENT")
                .requestMatchers("/doctors").hasRole("DOCTOR")
                .requestMatchers("/changePassword", "/appointments/**").authenticated()
                .requestMatchers("/login", "/signup").anonymous()
                .requestMatchers("/logout", "/", "/css/**", "/js/**", "/img/**", "**/favicon.ico").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .failureHandler(failureHandler)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new AppUserDetailsService(userRepository);
    }
}
