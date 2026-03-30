package com.example.dao;

import com.example.dto.EmployeeDTO;

import java.util.List;

/**
 * DAO (Data Access Object) Interface for Employee database operations.
 *
 * This interface defines WHAT database operations are available (save, find, update, delete)
 * but NOT HOW they are done. The actual implementation is in EmployeeDAOImpl.
 *
 * Why use an interface?
 * - The Service layer depends on this interface, not the implementation.
 *   If we ever switch from JDBC to JPA or another technology, we just write
 *   a new implementation — the Service code stays the same.
 * - Makes testing easier — we can create a fake (mock) implementation for tests.
 */
public interface EmployeeDAO {

    // INSERT a new employee into the database and return it with the generated ID
    EmployeeDTO save(EmployeeDTO employee);

    // SELECT all employees from the database
    List<EmployeeDTO> findAll();

    // SELECT one employee by their ID (returns null if not found)
    EmployeeDTO findById(Long id);

    // UPDATE an employee's information (returns null if not found)
    EmployeeDTO update(Long id, EmployeeDTO employee);

    // DELETE an employee by their ID (returns true if deleted, false if not found)
    boolean delete(Long id);
}
