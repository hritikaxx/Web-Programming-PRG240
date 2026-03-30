package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
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

    @Autowired
    private UserService userService;

    // ==================== JSP VIEW ====================

    @GetMapping("/userregister")
    public String showRegistrationPage() {
        return "userregistration";
    }

    @PostMapping("/registerUser")
    public String registerUser(User user, Model model) {
        User registeredUser = userService.registerUser(
                user.getFullName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword()
        );
        model.addAttribute("user", registeredUser);
        return "usersummary";
    }

    // ==================== REST API ====================

    // CREATE USER
    // POST /api/users
    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }

        User registeredUser = userService.registerUser(
                user.getFullName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User registered successfully");
        response.put("user", registeredUser);

        return ResponseEntity.ok(response);
    }

    // GET ALL USERS
    // GET /api/users
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> getAllUsers() {

        List<User> users = userService.getAllUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", users.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }

    // GET USER BY ID
    // GET /api/users/{id}
    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {

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
    }

    // UPDATE USER
    // PUT /api/users/{id}
    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }

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
    }

    // DELETE USER
    // DELETE /api/users/{id}
    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {

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