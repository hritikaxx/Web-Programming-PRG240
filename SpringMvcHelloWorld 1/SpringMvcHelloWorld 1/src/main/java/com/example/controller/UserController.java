package com.paws.controller;

import com.paws.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @GetMapping("/userregister")
    public String showRegistrationPage() {
        return "userregistration";  
    }

    @PostMapping("/registerUser")
    public String registerUser(User user, Model model) {

        model.addAttribute("user", user);

        return "usersummary";   
    }

}