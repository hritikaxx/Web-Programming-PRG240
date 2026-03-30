package com.example.service;

import com.example.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private List<User> userList = new ArrayList<>();
    private Long idCounter = 1L;

    // CREATE
    public User registerUser(String fullName, String email, String username, String password) {
        User user = new User(idCounter++, fullName, email, username, password);
        userList.add(user);
        return user;
    }

    // GET ALL
    public List<User> getAllUsers() {
        return userList;
    }

    // GET BY ID
    public User getUserById(Long id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // UPDATE
    public User updateUser(Long id, User updatedUser) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                user.setFullName(updatedUser.getFullName());
                user.setEmail(updatedUser.getEmail());
                user.setUsername(updatedUser.getUsername());
                user.setPassword(updatedUser.getPassword());
                return user;
            }
        }
        return null;
    }

    // DELETE
    public boolean deleteUser(Long id) {
        return userList.removeIf(user -> user.getId().equals(id));
    }
}