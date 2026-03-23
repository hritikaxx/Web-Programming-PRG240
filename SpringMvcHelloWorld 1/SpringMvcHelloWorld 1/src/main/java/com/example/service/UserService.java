package com.example.service;

import com.paws.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User registerUser(String fullName, String email, String username, String password) {
        return new User(fullName, email, username, password);
    }
}