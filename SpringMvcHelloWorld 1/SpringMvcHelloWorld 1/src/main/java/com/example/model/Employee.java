package com.example.model;

public class Employee {

    private String name;
    private String email;
    private String contactNumber;
    private String position;
    private int age;
    private String address;

    public Employee() {
    }

    public Employee(String name, String email, String contactNumber, String position, int age, String address) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.position = position;
        this.age = age;
        this.address = address;
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

    public int getAge() {
    return age;
    }

    public void setAge(int age) {
    this.age = age;
    }

    public String getAddress() {
    return address;
    }

    public void setAddress(String address) {
    this.address = address;
    }
} 
