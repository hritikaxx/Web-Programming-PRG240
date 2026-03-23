package com.example.controller;

import com.example.service.UserService;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userregister")
    public String showRegistrationPage() {
        return "userregistration";
    }

    @PostMapping("/registerUser")
    public String registerUser(User user, Model model) {
        User registeredUser = userService.registerUser(user.getFullName(), user.getEmail(), user.getUsername(), user.getPassword());
        model.addAttribute("user", registeredUser);
        return "usersummary";
    }

}