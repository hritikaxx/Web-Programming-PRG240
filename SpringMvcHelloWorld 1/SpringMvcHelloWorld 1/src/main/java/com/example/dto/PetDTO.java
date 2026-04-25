package com.example.dto;

/**
 * DTO (Data Transfer Object) — a simple container for passing pet data
 * between the Service layer and the DAO (database) layer.
 */
public class PetDTO {

    private Long id;
    private String name;
    private String type;
    private int age;
    private String personality;
    private byte[] image;
    private Long userId;

    // No-argument constructor
    public PetDTO() {
    }

    // Constructor without id (used when creating a NEW pet)
    public PetDTO(String name, String type, int age, String personality, byte[] image, Long userId) {
        this.name = name;
        this.type = type;
        this.age = age;
        this.personality = personality;
        this.image = image;
        this.userId = userId;
    }

    // Constructor with id (used when reading an EXISTING pet from the database)
    public PetDTO(Long id, String name, String type, int age, String personality, byte[] image, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.age = age;
        this.personality = personality;
        this.image = image;
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}