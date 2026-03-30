package com.example.dao;

import com.example.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * This class handles all database operations for employees.
 * It uses Spring's JdbcTemplate to run SQL queries against the H2 database.
 *
 * Think of JdbcTemplate as a helper that lets you run SQL without worrying
 * about opening/closing database connections — Spring handles that for you.
 */
@Repository  // Tells Spring: "This class talks to the database — manage it as a bean"
public class EmployeeDAOImpl implements EmployeeDAO {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDAOImpl.class);

    // JdbcTemplate is the tool we use to run SQL queries
    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper: tells Spring how to convert a database row into a Java object.
     *
     * When we run a SELECT query, the database returns rows and columns (like a spreadsheet).
     * This mapper reads each column by name and creates an EmployeeDTO object from it.
     *
     * Example: a row like | 1 | John | john@email.com | 1234567890 | Developer |
     * becomes: new EmployeeDTO(1, "John", "john@email.com", "1234567890", "Developer")
     */
    private final RowMapper<EmployeeDTO> rowMapper = (rs, rowNum) -> new EmployeeDTO(
            rs.getLong("id"),              // Read the "id" column as a number
            rs.getString("name"),          // Read the "name" column as text
            rs.getString("email"),         // Read the "email" column as text
            rs.getString("contact_number"), // Read the "contact_number" column as text
            rs.getString("position")       // Read the "position" column as text
    );

    /**
     * Constructor — called once when the application starts.
     * Spring automatically passes in the JdbcTemplate (configured in dispatcher-servlet.xml).
     * After saving it, we create the database table if it doesn't already exist.
     */
    public EmployeeDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTable();
    }

    /**
     * Creates the "employees" table in the database if it doesn't exist yet.
     * - First time running the app: table gets created.
     * - Every time after: IF NOT EXISTS means this does nothing, existing data is safe.
     */
    private void initTable() {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS employees (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL, " +
                            "contact_number VARCHAR(10) NOT NULL, " +
                            "position VARCHAR(255) NOT NULL" +
                            ")"
            );
            logger.info("Employee table initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize employee table: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * INSERT a new employee into the database.
     *
     * The ? marks are placeholders — Spring fills them in safely with the actual values.
     * This prevents SQL injection (a security attack where someone puts harmful SQL in the input).
     *
     * After inserting, we use CALL IDENTITY() to get the auto-generated ID that the
     * database assigned to this new employee.
     */
    @Override
    public EmployeeDTO save(EmployeeDTO employee) {
        String sql = "INSERT INTO employees (name, email, contact_number, position) VALUES (?, ?, ?, ?)";
        try {
            // KeyHolder captures the auto-generated ID from the INSERT statement
            KeyHolder keyHolder = new GeneratedKeyHolder();

            // Run the INSERT query using a PreparedStatement so we can retrieve the generated key
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, employee.getName());
                ps.setString(2, employee.getEmail());
                ps.setString(3, employee.getContactNumber());
                ps.setString(4, employee.getPosition());
                return ps;
            }, keyHolder);

            // Retrieve the auto-generated ID from the KeyHolder
            Long generatedId = keyHolder.getKey().longValue();

            employee.setId(generatedId);
            logger.info("Employee inserted successfully with id: {}", generatedId);
            return employee;
        } catch (Exception e) {
            logger.error("Database error while saving employee: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * SELECT all employees from the database.
     * The rowMapper converts each database row into an EmployeeDTO object.
     * Returns a list of all employees.
     */
    @Override
    public List<EmployeeDTO> findAll() {
        try {
            return jdbcTemplate.query("SELECT * FROM employees", rowMapper);
        } catch (Exception e) {
            logger.error("Database error while fetching all employees: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * SELECT one employee by their ID.
     * The ? placeholder gets replaced with the id value we pass in.
     * Returns the employee if found, or null if no employee has that ID.
     */
    @Override
    public EmployeeDTO findById(Long id) {
        try {
            List<EmployeeDTO> results = jdbcTemplate.query(
                    "SELECT * FROM employees WHERE id = ?", rowMapper, id);

            if (results.isEmpty()) {
                return null;  // No employee found with this ID
            }
            return results.get(0);  // Return the first (and only) result
        } catch (Exception e) {
            logger.error("Database error while fetching employee with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * UPDATE an existing employee's information.
     *
     * jdbcTemplate.update() returns how many rows were changed:
     *   - 0 means no employee with that ID exists
     *   - 1 means the update was successful
     */
    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employee) {
        try {
            int rowsChanged = jdbcTemplate.update(
                    "UPDATE employees SET name = ?, email = ?, contact_number = ?, position = ? WHERE id = ?",
                    employee.getName(),
                    employee.getEmail(),
                    employee.getContactNumber(),
                    employee.getPosition(),
                    id
            );

            if (rowsChanged == 0) {
                return null;  // No employee found with this ID
            }

            employee.setId(id);
            logger.info("Employee updated successfully with id: {}", id);
            return employee;
        } catch (Exception e) {
            logger.error("Database error while updating employee with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * DELETE an employee by their ID.
     * Returns true if the employee was deleted, false if no employee had that ID.
     */
    @Override
    public boolean delete(Long id) {
        try {
            int rowsDeleted = jdbcTemplate.update("DELETE FROM employees WHERE id = ?", id);

            if (rowsDeleted > 0) {
                logger.info("Employee deleted successfully with id: {}", id);
                return true;
            }
            return false;  // No employee found with this ID
        } catch (Exception e) {
            logger.error("Database error while deleting employee with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
