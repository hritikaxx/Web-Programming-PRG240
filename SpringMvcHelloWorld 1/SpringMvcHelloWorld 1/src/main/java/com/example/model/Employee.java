package com.example.model;

public class Employee {

    private String name;
    private String email;
    private String contactNumber;
    private String position;

    public Employee() {
    }

    public Employee(String name, String email, String contactNumber, String position) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.position = position;
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
}
