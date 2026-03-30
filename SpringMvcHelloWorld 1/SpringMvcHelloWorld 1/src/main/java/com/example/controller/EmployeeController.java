package com.example.controller;

import com.example.model.Employee;
import com.example.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * The Controller handles all incoming HTTP requests from the browser or API clients.
 *
 * It has two types of endpoints:
 * 1. JSP View Endpoints — return web pages (for the browser UI)
 * 2. REST API Endpoints — return JSON data (for tools like Postman or JavaScript fetch)
 *
 * How it works:
 * - User sends a request (e.g., clicks "Submit" on the form)
 * - Spring routes the request to the matching method based on the URL and HTTP method
 * - The method calls the Service to do the actual work
 * - The method returns either a page name (JSP) or JSON data
 */
@Controller  // Tells Spring: "This class handles web requests"
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired  // Tells Spring: "Please give me an instance of EmployeeService"
    private EmployeeService employeeService;

    // ==================== JSP VIEW ENDPOINTS ====================
    // These endpoints return web pages (JSP files) for the browser

    /**
     * Shows the registration form page.
     * URL: GET /register
     * Returns the "employeeForm" JSP page.
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "employeeForm";  // Spring looks for /WEB-INF/views/employeeForm.jsp
    }

    /**
     * Handles traditional form submission (non-AJAX).
     * URL: POST /register
     *
     * @RequestParam reads the form field values from the submitted form.
     * On success: shows the summary page with the employee data.
     * On error: shows the form again with an error message.
     */
    @PostMapping("/register")
    public String registerEmployee(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("position") String position,
            Model model) {

        try {
            // Call the service to save the employee
            Employee employee = employeeService.registerEmployee(name, email, contactNumber, position);
            logger.info("Employee registered via form POST: id={}, name={}", employee.getId(), name);

            // Add the employee to the model so the JSP page can display it
            model.addAttribute("employee", employee);
            return "employeeSummary";  // Show the summary page
        } catch (Exception e) {
            logger.error("Error registering employee via form: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "employeeForm";  // Show the form again with error
        }
    }

    /**
     * Shows the summary page after a successful AJAX registration.
     * URL: GET /register/summary?name=...&email=...&contactNumber=...&position=...
     *
     * After the JavaScript AJAX call succeeds, it redirects to this URL
     * with the employee data as query parameters.
     */
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
    // These endpoints return JSON data (not web pages)
    // @ResponseBody tells Spring: "Convert the return value to JSON, don't look for a JSP page"

    /**
     * Create a new employee via JSON API.
     * URL: POST /api/employees
     *
     * @Valid tells Spring to check all the validation rules on the Employee object
     *        (@NotBlank, @Email, @Pattern). If any fail, the errors go into BindingResult.
     * @RequestBody tells Spring to read the JSON from the request and convert it to an Employee.
     */
    @PostMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {

        // Step 1: Check if validation failed
        if (bindingResult.hasErrors()) {
            logger.error("Validation error while creating employee: {}", bindingResult.getAllErrors());
            return buildErrorResponse(bindingResult);  // Return 400 Bad Request with error details
        }

        // Step 2: Try to save the employee
        try {
            Employee registered = employeeService.registerEmployee(
                    employee.getName(),
                    employee.getEmail(),
                    employee.getContactNumber(),
                    employee.getPosition()
            );

            logger.info("Employee created via API: id={}, name={}", registered.getId(), registered.getName());

            // Build a success response with the saved employee data
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Employee registered successfully");
            response.put("employee", registered);
            return ResponseEntity.ok(response);  // Return 200 OK
        } catch (Exception e) {
            logger.error("Database error while creating employee: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to register employee. Database error.");
            return ResponseEntity.status(500).body(response);  // Return 500 Internal Server Error
        }
    }

    /**
     * Get all employees.
     * URL: GET /api/employees
     */
    @GetMapping("/api/employees")
    @ResponseBody
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", employees.size());
            response.put("employees", employees);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while fetching employees: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to fetch employees. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get one employee by their ID.
     * URL: GET /api/employees/1  (where 1 is the employee ID)
     *
     * @PathVariable reads the {id} from the URL path.
     */
    @GetMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);

            // If no employee found with that ID, return 404
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
        } catch (Exception e) {
            logger.error("Database error while fetching employee id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to fetch employee. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Update an existing employee.
     * URL: PUT /api/employees/1
     */
    @PutMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEmployee(
            @PathVariable("id") Long id,
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.error("Validation error while updating employee id {}: {}", id, bindingResult.getAllErrors());
            return buildErrorResponse(bindingResult);
        }

        try {
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
        } catch (Exception e) {
            logger.error("Database error while updating employee id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update employee. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Delete an employee.
     * URL: DELETE /api/employees/1
     */
    @DeleteMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        try {
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
        } catch (Exception e) {
            logger.error("Database error while deleting employee id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to delete employee. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Alternative endpoint for the JSP form's AJAX submission.
     * URL: POST /api/register
     * Just calls createEmployee() — kept so the form JavaScript doesn't break.
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> registerEmployeeApi(
            @Valid @RequestBody Employee employee,
            BindingResult bindingResult) {
        return createEmployee(employee, bindingResult);
    }

    /**
     * Helper method: collects all validation errors and builds a JSON error response.
     *
     * Example output:
     * {
     *   "status": "error",
     *   "errors": {
     *     "name": "Name is required",
     *     "email": "Email must be a valid email address"
     *   }
     * }
     */
    private ResponseEntity<?> buildErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);  // Return 400 Bad Request
    }
}
