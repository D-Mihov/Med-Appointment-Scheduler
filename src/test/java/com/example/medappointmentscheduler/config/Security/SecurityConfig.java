package com.example.medappointmentscheduler.config.Security;

import com.example.medappointmentscheduler.repository.UserRepository;
import com.example.medappointmentscheduler.security.CustomAuthenticationFailureHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;


@Configuration
public class SecurityConfig {

//    @Autowired
//    private CustomAuthenticationSuccessHandler successHandler;

    private final CustomAuthenticationFailureHandler failureHandler;

    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .requestMatchers("/signupDoctor", "/patients/**", "/doctors/**").hasRole("ADMIN")
                .requestMatchers("/add-appointment", "/api/getAvailableHours/**").hasRole("PATIENT")
                .requestMatchers("/doctors").hasRole("DOCTOR")
                .requestMatchers("/changePassword", "/appointments/**", "/appointments/feedback/**", "/logout").authenticated()
                .requestMatchers("/login", "/signup").anonymous()
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "**/favicon.ico", "/error").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureHandler(failureHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new AppUserDetailsService(userRepository);
    }
}
