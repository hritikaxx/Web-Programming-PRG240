package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public String loginUser(String username, String password) {
        return username;
    }
}
