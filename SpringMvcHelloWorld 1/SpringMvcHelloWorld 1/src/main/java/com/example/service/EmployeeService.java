package com.example.service;

import com.example.dao.EmployeeDAO;
import com.example.dto.EmployeeDTO;
import com.example.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The Service layer sits between the Controller and the DAO.
 *
 * Its job is to:
 * 1. Contain business logic (any rules or processing before saving/reading data)
 * 2. Convert between Employee (used by Controller) and EmployeeDTO (used by DAO)
 * 3. Log important events
 *
 * Why not just call the DAO directly from the Controller?
 * Separation of concerns — the Controller handles HTTP requests,
 * the Service handles business logic, and the DAO handles database access.
 */
@Service  // Tells Spring: "This is a service class — manage it as a bean"
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    // The DAO that handles database operations
    private final EmployeeDAO employeeDAO;

    /**
     * Constructor — Spring automatically provides the EmployeeDAOImpl instance.
     * This is called "dependency injection" — instead of us creating objects with "new",
     * Spring creates them and passes them where they're needed.
     */
    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    /**
     * Register a new employee.
     * 1. Create a DTO from the given details
     * 2. Save it to the database via the DAO
     * 3. Convert the saved DTO (which now has a generated ID) back to an Employee
     * 4. Return the Employee to the Controller
     */
    public Employee registerEmployee(String name, String email, String contactNumber, String position) {
        logger.info("Registering new employee: name={}, email={}, position={}", name, email, position);

        // Create a DTO to send to the database layer
        EmployeeDTO dto = new EmployeeDTO(name, email, contactNumber, position);

        // Save to database — the DAO returns the DTO with the auto-generated ID
        EmployeeDTO saved = employeeDAO.save(dto);

        logger.info("Employee registered successfully with id: {}", saved.getId());

        // Convert DTO back to Employee model and return it
        return toEmployee(saved);
    }

    /**
     * Get all employees from the database.
     * The DAO returns DTOs, so we convert each one to an Employee model.
     */
    public List<Employee> getAllEmployees() {
        // Get all DTOs from the database
        List<EmployeeDTO> dtos = employeeDAO.findAll();
        logger.info("Retrieved {} employees from database", dtos.size());

        // Convert each DTO to an Employee model
        List<Employee> employees = new ArrayList<>();
        for (EmployeeDTO dto : dtos) {
            employees.add(toEmployee(dto));
        }
        return employees;
    }

    /**
     * Get one employee by their ID.
     * Returns null if no employee has that ID.
     */
    public Employee getEmployeeById(Long id) {
        EmployeeDTO dto = employeeDAO.findById(id);

        if (dto == null) {
            logger.info("Employee not found with id: {}", id);
            return null;
        }

        return toEmployee(dto);
    }

    /**
     * Update an existing employee's information.
     * Returns null if no employee has that ID.
     */
    public Employee updateEmployee(Long id, Employee updated) {
        // Convert the Employee model to a DTO for the database layer
        EmployeeDTO dto = new EmployeeDTO(
                updated.getName(),
                updated.getEmail(),
                updated.getContactNumber(),
                updated.getPosition()
        );

        EmployeeDTO result = employeeDAO.update(id, dto);

        if (result == null) {
            logger.info("Cannot update - employee not found with id: {}", id);
            return null;
        }

        return toEmployee(result);
    }

    /**
     * Delete an employee by their ID.
     * Returns true if deleted, false if not found.
     */
    public boolean deleteEmployee(Long id) {
        boolean deleted = employeeDAO.delete(id);
        if (!deleted) {
            logger.info("Cannot delete - employee not found with id: {}", id);
        }
        return deleted;
    }

    /**
     * Helper method: Convert a DTO (database object) to an Employee (model object).
     * This is needed because the Controller works with Employee objects,
     * but the DAO works with EmployeeDTO objects.
     */
    private Employee toEmployee(EmployeeDTO dto) {
        Employee emp = new Employee(dto.getName(), dto.getEmail(), dto.getContactNumber(), dto.getPosition());
        emp.setId(dto.getId());
        return emp;
    }
}
