Feature: Employee Registration Form
  As a user of the Employee management system
  I want to fill in the Employee registration form
  So that I can register new employees in the system

  Scenario: Successfully register a new employee with valid data
    Given the employee registration API is available
    When I fill in the employee form with the following data:
      | name           | email                | contactNumber | position  |
      | Abiral Khanal  | abiral@example.com   | 1234567890    | Developer |
    Then the employee should be registered successfully
    And the response should contain the employee name "Abiral Khanal"

  Scenario: Fail to register employee with missing name
    Given the employee registration API is available
    When I fill in the employee form with the following data:
      | name | email              | contactNumber | position  |
      |      | abiral@example.com | 1234567890    | Developer |
    Then the registration should fail with validation errors
    And the error response should contain a message for field "name"

  Scenario: Fail to register employee with invalid email
    Given the employee registration API is available
    When I fill in the employee form with the following data:
      | name          | email        | contactNumber | position  |
      | Abiral Khanal | not-an-email | 1234567890    | Developer |
    Then the registration should fail with validation errors
    And the error response should contain a message for field "email"

  Scenario: Fail to register employee with invalid contact number
    Given the employee registration API is available
    When I fill in the employee form with the following data:
      | name          | email              | contactNumber | position  |
      | Abiral Khanal | abiral@example.com | 12345         | Developer |
    Then the registration should fail with validation errors
    And the error response should contain a message for field "contactNumber"

  Scenario: Register multiple employees sequentially
    Given the employee registration API is available
    When I fill in the employee form with the following data:
      | name          | email              | contactNumber | position  |
      | Abiral Khanal | abiral@example.com | 1234567890    | Developer |
    Then the employee should be registered successfully
    When I fill in the employee form with the following data:
      | name       | email             | contactNumber | position |
      | Jane Smith | jane@example.com  | 9876543210    | Manager  |
    Then the employee should be registered successfully
