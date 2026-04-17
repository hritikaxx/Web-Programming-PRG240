package com.example.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for validating Employee form data.
 *
 * These tests use the Jakarta Bean Validation API (Hibernate Validator)
 * to verify that the @NotBlank, @Email, and @Pattern annotations on the
 * Employee model work correctly — the same validations that Spring triggers
 * when @Valid is used in the controller.
 */
class EmployeeValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        // Create a Validator instance that reads the annotations on Employee fields
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== VALID DATA TESTS ====================

    @Test
    @DisplayName("Valid employee should have no validation errors")
    void validEmployee_shouldHaveNoViolations() {
        Employee employee = new Employee("Abiral Khanal", "abiral@example.com", "1234567890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertTrue(violations.isEmpty(), "A valid employee should produce no violations");
    }

    // ==================== NAME FIELD TESTS ====================

    @Test
    @DisplayName("Blank name should produce validation error")
    void blankName_shouldFailValidation() {
        Employee employee = new Employee("", "abiral@example.com", "1234567890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty(), "Blank name should produce violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Should have a violation on the 'name' field");
    }

    @Test
    @DisplayName("Null name should produce validation error")
    void nullName_shouldFailValidation() {
        Employee employee = new Employee(null, "abiral@example.com", "1234567890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    // ==================== EMAIL FIELD TESTS ====================

    @Test
    @DisplayName("Blank email should produce validation error")
    void blankEmail_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "", "1234567890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Invalid email format should produce validation error")
    void invalidEmail_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "not-an-email", "1234567890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                "An invalid email should produce a violation on the 'email' field");
    }

    // ==================== CONTACT NUMBER FIELD TESTS ====================

    @Test
    @DisplayName("Blank contact number should produce validation error")
    void blankContactNumber_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "abiral@example.com", "", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactNumber")));
    }

    @Test
    @DisplayName("Contact number with less than 10 digits should fail")
    void shortContactNumber_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "abiral@example.com", "12345", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactNumber")),
                "A 5-digit contact number should fail the 10-digit pattern check");
    }

    @Test
    @DisplayName("Contact number with letters should fail")
    void lettersInContactNumber_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "abiral@example.com", "123abc7890", "Developer");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactNumber")),
                "Letters in contact number should fail the digits-only pattern check");
    }

    // ==================== POSITION FIELD TESTS ====================

    @Test
    @DisplayName("Blank position should produce validation error")
    void blankPosition_shouldFailValidation() {
        Employee employee = new Employee("Abiral Khanal", "abiral@example.com", "1234567890", "");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("position")));
    }

    // ==================== ALL FIELDS BLANK TEST ====================

    @Test
    @DisplayName("All blank fields should produce multiple validation errors")
    void allFieldsBlank_shouldProduceMultipleViolations() {
        Employee employee = new Employee("", "", "", "");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        // Should have at least 4 violations — one for each required field
        assertTrue(violations.size() >= 4,
                "All blank fields should produce at least 4 violations, got: " + violations.size());
    }
}
