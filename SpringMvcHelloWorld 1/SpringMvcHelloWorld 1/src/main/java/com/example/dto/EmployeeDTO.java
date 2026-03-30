package com.example.dto;

/**
 * DTO (Data Transfer Object) — a simple container for passing employee data
 * between the Service layer and the DAO (database) layer.
 *
 * Why not just use the Employee model everywhere?
 * The Employee model has validation annotations (@NotBlank, @Email, etc.)
 * that are only needed in the Controller layer. The database layer doesn't
 * care about validation — it just needs the raw data. The DTO keeps these
 * concerns separate.
 *
 * Think of it like this:
 * - Employee = what the user sees (with input validation)
 * - EmployeeDTO = what the database sees (just plain data)
 */
public class EmployeeDTO {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String position;

    // No-argument constructor
    public EmployeeDTO() {
    }

    // Constructor without id (used when creating a NEW employee — id doesn't exist yet)
    public EmployeeDTO(String name, String email, String contactNumber, String position) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.position = position;
    }

    // Constructor with id (used when reading an EXISTING employee from the database)
    public EmployeeDTO(Long id, String name, String email, String contactNumber, String position) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.position = position;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{id=" + id + ", name='" + name + "', email='" + email +
                "', contactNumber='" + contactNumber + "', position='" + position + "'}";
    }
}
