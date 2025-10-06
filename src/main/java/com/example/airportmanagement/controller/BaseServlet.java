package com.example.airportmanagement.controller;

import com.example.airportmanagement.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base servlet with common JSON utilities
 */
public abstract class BaseServlet extends HttpServlet {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    /**
     * Send JSON response
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }
    
    /**
     * Send success response
     */
    protected void sendSuccess(HttpServletResponse response, Object data, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        sendJsonResponse(response, result);
    }
    
    /**
     * Send error response
     */
    protected void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        sendJsonResponse(response, error);
    }
    
    /**
     * Get current user from session
     */
    protected User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
    
    /**
     * Check if user is logged in
     */
    protected boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
    
    /**
     * Check if user has required role
     */
    protected boolean hasRole(HttpSession session, User.UserRole... roles) {
        User user = getCurrentUser(session);
        if (user == null) {
            return false;
        }
        
        for (User.UserRole role : roles) {
            if (user.getRole() == role) {
                return true;
            }
        }
        return false;
    }
}
