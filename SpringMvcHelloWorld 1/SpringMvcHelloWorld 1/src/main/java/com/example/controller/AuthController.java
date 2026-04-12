package com.example.controller;

import com.example.security.JwtUtil;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API login endpoint.
 *
 * POST /api/auth/login  { "username": "...", "password": "..." }
 *   -> 200 { "token": "<jwt>" } on success
 *   -> 401 { "status": "error", "message": "Invalid credentials" } on failure
 *
 * Authenticates against the users table in the database.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (userService.authenticate(username, password)) {
            String token = jwtUtil.generateToken(username);
            logger.info("API login success for user: {}", username);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        logger.warn("API login failed for user: {}", username);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }
}
