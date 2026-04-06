package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public boolean loginUser(String username, String password) {
        logger.info("Login attempt for username: {}", username);

        // TODO: replace with real database lookup
        if (username != null && !username.isBlank() && password != null && !password.isBlank()) {
            logger.info("Login successful for username: {}", username);
            return true;
        }

        logger.warn("Login failed for username: {}", username);
        return false;
    }
}