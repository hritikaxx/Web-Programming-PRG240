package com.example.service;

import com.example.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    public Employee registerEmployee(String name, String email, String contactNumber, String position) {
        return new Employee(name, email, contactNumber, position);
    }
}
