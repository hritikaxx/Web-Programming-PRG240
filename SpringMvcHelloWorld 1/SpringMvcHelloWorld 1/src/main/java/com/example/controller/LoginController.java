package com.example.controller;

import com.example.model.Login;
import com.example.service.LoginService;
import jakarta.servlet.http.HttpSession;
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
    public String showLoginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model, HttpSession session) {
        if (redirect != null && !redirect.isEmpty()) {
            model.addAttribute("redirectAfterLogin", redirect);
            session.setAttribute("redirectAfterLogin", redirect);
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/logout-home")
    public String logoutToHome(HttpSession session) {
    session.invalidate();
    return "redirect:/";
}

    @PostMapping("/loginUser")
    public String loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirectAfterLogin", required = false) String redirectAfterLogin,
            Model model,
            HttpSession session) {

        try {
            String result = loginService.loginUser(username, password);

            if (result.equals("USERNAME_NOT_FOUND")) {
                model.addAttribute("errorMessage", "Invalid username. Please register first.");
                return "login";
            }

            if (result.equals("WRONG_PASSWORD")) {
                model.addAttribute("errorMessage", "Wrong password. Please try again.");
                return "login";
            }

            session.setAttribute("loggedInUser", username);
            if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                return "redirect:" + redirectAfterLogin;
            }
            String redirectTarget = (String) session.getAttribute("redirectAfterLogin");
            if (redirectTarget != null && !redirectTarget.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectTarget;
            }
            return "redirect:/";

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
            String result = loginService.loginUser(login.getUsername(), login.getPassword());

            if (result.equals("USERNAME_NOT_FOUND")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid username.");
                return ResponseEntity.status(401).body(response);
            }

            if (result.equals("WRONG_PASSWORD")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Wrong password.");
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