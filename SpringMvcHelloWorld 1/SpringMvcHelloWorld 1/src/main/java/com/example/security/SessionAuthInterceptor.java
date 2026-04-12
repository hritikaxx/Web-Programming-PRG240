package com.example.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts requests to protected UI pages and redirects to /login
 * if the user is not logged in (no "loggedInUser" in session).
 * See AUTHENTICATION_DOCUMENTATION.md for full details.
 */
@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loggedInUser") != null) {
            return true;
        }

        logger.info("Unauthenticated access to {} — redirecting to login", request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
