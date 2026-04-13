package com.example.service;

import com.example.dao.UserDAO;
import com.example.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final UserDAO userDAO;

    public LoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String loginUser(String username, String password) {
        logger.info("Login attempt for username: {}", username);

        UserDTO user = userDAO.findByUsername(username);

        if (user == null) {
            logger.warn("Username not found: {}", username);
            return "USERNAME_NOT_FOUND";
        }

        if (!user.getPassword().equals(password)) {
            logger.warn("Incorrect password for username: {}", username);
            return "WRONG_PASSWORD";
        }

        logger.info("Login successful for username: {}", username);
        return "SUCCESS";
    }
}