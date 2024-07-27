package com.example.medappointmentscheduler.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String login(){
        return "login";
    }

//    @PostMapping("/login-error")
//    public String onFailedLogin(@ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String email, RedirectAttributes redirectAttributes) {
//
//        redirectAttributes.addFlashAttribute("email", email);
//        redirectAttributes.addFlashAttribute("bad_credentials",true);
//        return "redirect:/login";
//    }
}
