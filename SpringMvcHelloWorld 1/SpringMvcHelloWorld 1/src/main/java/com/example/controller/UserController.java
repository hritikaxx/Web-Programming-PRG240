package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // ==================== JSP VIEW ENDPOINTS ====================

    @GetMapping("/userregister")
    public String showRegistrationPage() {
        return "userregistration";
    }

    @GetMapping("/user/login")
    public String showLoginPage() {
    return "login";
    }

    @PostMapping("/registerUser")
    public String registerUser(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        try {
            User user = userService.registerUser(fullName, email, username, password);
            logger.info("User registered via form POST: id={}, username={}", user.getId(), username);
            model.addAttribute("user", user);
            return "usersummary";
        } catch (Exception e) {
            logger.error("Error registering user via form: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "userregistration";
        }
    }

    @GetMapping("/userregister/summary")
    public String showSummary(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            Model model) {
        User user = new User(fullName, email, username);
        model.addAttribute("user", user);
        return "usersummary";
    }

    // ==================== REST API ENDPOINTS ====================

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.error("Validation error while creating user: {}", bindingResult.getAllErrors());
            return buildErrorResponse(bindingResult);
        }

        try {
            User registeredUser = userService.registerUser(
                    user.getFullName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword()
            );
            logger.info("User created via API: id={}, username={}", registeredUser.getId(), registeredUser.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while creating user: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to register user. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", users.size());
            response.put("users", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while fetching users: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to fetch users. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);

            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "User not found with id: " + id);
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while fetching user id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to fetch user. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.error("Validation error while updating user id {}: {}", id, bindingResult.getAllErrors());
            return buildErrorResponse(bindingResult);
        }

        try {
            User updatedUser = userService.updateUser(id, user);

            if (updatedUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "User not found with id: " + id);
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while updating user id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update user. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            boolean deleted = userService.deleteUser(id);

            if (!deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "User not found with id: " + id);
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database error while deleting user id {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to delete user. Database error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/api/users/register")
    @ResponseBody
    public ResponseEntity<?> registerUserApi(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {
        return createUser(user, bindingResult);
    }

    // ==================== ERROR HANDLING ====================

    private ResponseEntity<?> buildErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    
}