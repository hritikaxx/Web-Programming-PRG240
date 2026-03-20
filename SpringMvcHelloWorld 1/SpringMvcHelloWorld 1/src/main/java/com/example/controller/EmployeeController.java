package com.example.controller;

import com.example.model.Employee;
import com.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "employeeForm";
    }

    @PostMapping("/register")
    public String registerEmployee(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("position") String position,
            Model model) {

        Employee employee = employeeService.registerEmployee(name, email, contactNumber, position);
        model.addAttribute("employee", employee);
        return "employeeSummary";
    }
}
