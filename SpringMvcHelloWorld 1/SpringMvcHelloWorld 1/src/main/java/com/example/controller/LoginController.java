package com.example.controller;

import com.example.model.Login;
import com.example.service.LoginService;
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
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    // ==================== JSP VIEW ENDPOINTS ====================

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/loginUser")
    public String loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        try {
            boolean success = loginService.loginUser(username, password);

            if (!success) {
                logger.warn("Login failed for username: {}", username);
                model.addAttribute("errorMessage", "Invalid username or password.");
                return "login";
            }

            logger.info("User logged in via form: username={}", username);
            model.addAttribute("username", username);
            return "loginsummary";
        } catch (Exception e) {
            logger.error("Error during login for username {}: {}", username, e.getMessage(), e);
            model.addAttribute("errorMessage", "Login failed. Please try again.");
            return "login";
        }
    }

    // ==================== REST API ENDPOINTS ====================

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(
            @Valid @RequestBody Login login,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.error("Validation error during login: {}", bindingResult.getAllErrors());
            return buildErrorResponse(bindingResult);
        }

        try {
            boolean success = loginService.loginUser(login.getUsername(), login.getPassword());

            if (!success) {
                logger.warn("Login failed via API for username: {}", login.getUsername());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid username or password.");
                return ResponseEntity.status(401).body(response);
            }

            logger.info("User logged in via API: username={}", login.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("username", login.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during API login for username {}: {}", login.getUsername(), e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Login failed. Server error.");
            return ResponseEntity.status(500).body(response);
        }
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