Feature: User CRUD Operations
  As an administrator of the Employee Management System
  I want to manage users through the API
  So that I can create, read, update, and delete users in the system

  # ===================== CREATE =====================

  Scenario: Successfully create a new user with valid data
    Given the user management API is available
    When I send a create user request with the following data:
      | fullName  | email                | username  | password  |
      | John Doe  | johndoe@example.com  | johndoe   | pass1234  |
    Then the user should be created successfully
    And the create response should contain the username "johndoe"

  Scenario: Fail to create a user with missing fullName
    Given the user management API is available
    When I send a create user request with the following data:
      | fullName | email               | username | password |
      |          | johndoe@example.com | johndoe  | pass1234 |
    Then the user creation should fail with an error

  Scenario: Fail to create a user with invalid email
    Given the user management API is available
    When I send a create user request with the following data:
      | fullName | email     | username | password |
      | John Doe | not-email | johndoe  | pass1234 |
    Then the user creation should fail with an error

  # ===================== READ =====================

  Scenario: Successfully retrieve all users
    Given the user management API is available
    When I request all users
    Then I should receive a list of users

  Scenario: Successfully retrieve a single user by ID
    Given the user management API is available
    When I request the user with ID 3
    Then I should receive the user details
    And the user details should contain the username "hritika"

  Scenario: Fail to retrieve a user with non-existent ID
    Given the user management API is available
    When I request the user with ID 99999
    Then the user retrieval should fail with not found error

  # ===================== UPDATE =====================

  Scenario: Successfully update an existing user
    Given the user management API is available
    When I update the user with ID 3 with the following data:
      | fullName        | email                   | username | password |
      | Hritika Updated | hritika.new@example.com | hritika  | 11111111 |
    Then the user should be updated successfully
    And the update response should contain the fullName "Hritika Updated"

  Scenario: Fail to update a user with non-existent ID
    Given the user management API is available
    When I update the user with ID 99999 with the following data:
      | fullName | email            | username | password |
      | John Doe | john@example.com | johndoe  | pass1234 |
    Then the user update should fail with an error

  # ===================== DELETE =====================

  Scenario: Successfully delete an existing user
    Given the user management API is available
    When I send a create user request with the following data:
      | fullName  | email                | username | password |
      | Delete Me | deleteme@example.com | deleteme | pass1234 |
    Then the user should be created successfully
    When I delete the user that was just created
    Then the user should be deleted successfully

  Scenario: Fail to delete a user with non-existent ID
    Given the user management API is available
    When I delete the user with ID 99999
    Then the user deletion should fail with an error