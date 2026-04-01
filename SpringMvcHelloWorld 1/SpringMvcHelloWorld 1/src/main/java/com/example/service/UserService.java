package com.example.service;

import com.example.dao.UserDAO;
import com.example.dto.UserDTO;
import com.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerUser(String fullName, String email, String username, String password) {
        logger.info("Registering new user: fullName={}, email={}, username={}", fullName, email, username);

        UserDTO dto = new UserDTO(fullName, email, username, password);
        UserDTO saved = userDAO.save(dto);

        logger.info("User registered successfully with id: {}", saved.getId());
        return toUser(saved);
    }

    public List<User> getAllUsers() {
        List<UserDTO> dtos = userDAO.findAll();
        logger.info("Retrieved {} users from database", dtos.size());

        List<User> users = new ArrayList<>();
        for (UserDTO dto : dtos) {
            users.add(toUser(dto));
        }
        return users;
    }

    public User getUserById(Long id) {
        UserDTO dto = userDAO.findById(id);

        if (dto == null) {
            logger.info("User not found with id: {}", id);
            return null;
        }

        return toUser(dto);
    }

    public User updateUser(Long id, User updated) {
        UserDTO dto = new UserDTO(
                updated.getFullName(),
                updated.getEmail(),
                updated.getUsername(),
                updated.getPassword()
        );

        UserDTO result = userDAO.update(id, dto);

        if (result == null) {
            logger.info("Cannot update - user not found with id: {}", id);
            return null;
        }

        return toUser(result);
    }

    public boolean deleteUser(Long id) {
        boolean deleted = userDAO.delete(id);
        if (!deleted) {
            logger.info("Cannot delete - user not found with id: {}", id);
        }
        return deleted;
    }

    private User toUser(UserDTO dto) {
        User user = new User(dto.getFullName(), dto.getEmail(), dto.getUsername(), dto.getPassword());
        user.setId(dto.getId());
        return user;
    }
}