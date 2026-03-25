package com.example.controller;

import com.example.model.Employee;
import com.example.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ==================== JSP VIEW ENDPOINTS ====================

    // Show the registration form page
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "employeeForm";
    }

    // Original form POST endpoint (kept for backward compatibility)
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

    // Show summary page after successful API registration
    @GetMapping("/register/summary")
    public String showSummary(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("position") String position,
            Model model) {
        Employee employee = new Employee(name, email, contactNumber, position);
        model.addAttribute("employee", employee);
        return "employeeSummary";
    }

    // ==================== REST API ENDPOINTS ====================

    // POST /api/employees - Create a new employee (JSON)
    @PostMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }

        Employee registered = employeeService.registerEmployee(
                employee.getName(),
                employee.getEmail(),
                employee.getContactNumber(),
                employee.getPosition()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Employee registered successfully");
        response.put("employee", registered);
        return ResponseEntity.ok(response);
    }

    // GET /api/employees - Get all employees
    @GetMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", employees.size());
        response.put("employees", employees);
        return ResponseEntity.ok(response);
    }

    // GET /api/employees/{id} - Get a single employee by ID
    @GetMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Employee not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("employee", employee);
        return ResponseEntity.ok(response);
    }

    // PUT /api/employees/{id} - Update an existing employee
    @PutMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEmployee(
            @PathVariable("id") Long id,
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }

        Employee updated = employeeService.updateEmployee(id, employee);

        if (updated == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Employee not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Employee updated successfully");
        response.put("employee", updated);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/employees/{id} - Delete an employee
    @DeleteMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        boolean deleted = employeeService.deleteEmployee(id);

        if (!deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Employee not found with id: " + id);
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Employee deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Keep the old /api/register endpoint for backward compatibility
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> registerEmployeeApi(
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {
        return createEmployee(employee, bindingResult);
    }

    // Helper method to build validation error response
    private ResponseEntity<?> buildErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }
}
