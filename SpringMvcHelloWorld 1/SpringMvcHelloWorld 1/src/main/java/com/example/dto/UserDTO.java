package com.example.dto;

public class UserDTO {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String password;

    public UserDTO() {
    }

    public UserDTO(String fullName, String email, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public UserDTO(Long id, String fullName, String email, String username, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "UserDTO{id=" + id + ", fullName='" + fullName + "', email='" + email +
                "', username='" + username + "'}";
    }
}