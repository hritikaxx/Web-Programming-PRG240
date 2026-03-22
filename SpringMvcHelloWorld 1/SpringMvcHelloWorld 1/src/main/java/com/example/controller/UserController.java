package com.paws.controller;

import com.paws.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    // Open the registration page
    @GetMapping("/userregister")
    public String showRegistrationPage() {
        return "userregistration";   // loads userregistration.jsp
    }

    // Handle form submission
    @PostMapping("/registerUser")
    public String registerUser(User user, Model model) {

        // Here you could save the user to the database later
        // For now we just send the user data to the summary page

        model.addAttribute("user", user);

        return "usersummary";   // loads usersummary.jsp
    }

}