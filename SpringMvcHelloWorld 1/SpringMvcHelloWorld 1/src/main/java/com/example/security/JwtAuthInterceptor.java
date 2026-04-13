package com.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthInterceptor.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI();
        if (path.contains("/api/register") || path.contains("/api/employees")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeError(response, "Incorrect JWT token");
            return false;
        }

        String token = header.substring("Bearer ".length()).trim();
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token");
            writeError(response, "Expired JWT token");
            return false;
        } catch (JwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            writeError(response, "Incorrect JWT token");
            return false;
        }
    }

    private void writeError(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("status", "error");
        body.put("message", message);
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }
}