package com.example.service;

import com.example.model.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmployeeService {

    private final Map<Long, Employee> employeeStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Employee registerEmployee(String name, String email, String contactNumber, String position) {
        Employee employee = new Employee(name, email, contactNumber, position);
        Long id = idCounter.getAndIncrement();
        employee.setId(id);
        employeeStore.put(id, employee);
        return employee;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeStore.values());
    }

    public Employee getEmployeeById(Long id) {
        return employeeStore.get(id);
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = employeeStore.get(id);
        if (existing == null) {
            return null;
        }
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setContactNumber(updated.getContactNumber());
        existing.setPosition(updated.getPosition());
        return existing;
    }

    public boolean deleteEmployee(Long id) {
        return employeeStore.remove(id) != null;
    }
}
