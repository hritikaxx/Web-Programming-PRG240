package com.example.service;

import com.example.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    public Employee registerEmployee(String name, String email, String contactNumber, String position, int age, String address) {
        return new Employee(name, email, contactNumber, position, age, address);
    }
}
