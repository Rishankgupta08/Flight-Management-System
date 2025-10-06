package com.example.airportmanagement.controller;

import com.example.airportmanagement.model.User;
import com.example.airportmanagement.service.ServiceException;
import com.example.airportmanagement.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Servlet - handles login, register, logout
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*"})
public class AuthServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private final UserService userService;
    
    public AuthServlet() {
        this.userService = new UserService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            return;
        }
        
        switch (pathInfo) {
            case "/register":
                handleRegister(request, response);
                break;
            case "/login":
                handleLogin(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/current".equals(pathInfo)) {
            handleGetCurrentUser(request, response);
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    /**
     * Handle user registration
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String roleStr = request.getParameter("role");
            
            User.UserRole role = User.UserRole.PASSENGER; // Default role
            if (roleStr != null && !roleStr.isEmpty()) {
                try {
                    role = User.UserRole.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    role = User.UserRole.PASSENGER;
                }
            }
            
            User user = userService.register(username, password, email, role);
            
            // Don't send password hash in response
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            
            sendSuccess(response, userData, "User registered successfully");
            logger.info("User registered: {}", username);
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error during registration", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Handle user login
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            User user = userService.login(username, password);
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(3600); // 1 hour
            
            // Prepare user data (without password hash)
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            
            sendSuccess(response, userData, "Login successful");
            logger.info("User logged in: {}", username);
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            logger.error("Error during login", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Login failed: " + e.getMessage());
        }
    }
    
    /**
     * Handle user logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String username = "";
                User user = getCurrentUser(session);
                if (user != null) {
                    username = user.getUsername();
                }
                
                session.invalidate();
                sendSuccess(response, null, "Logged out successfully");
                logger.info("User logged out: {}", username);
            } else {
                sendSuccess(response, null, "Already logged out");
            }
        } catch (Exception e) {
            logger.error("Error during logout", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Logout failed: " + e.getMessage());
        }
    }
    
    /**
     * Get current logged-in user
     */
    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            
            if (!isAuthenticated(session)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated");
                return;
            }
            
            User user = getCurrentUser(session);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            
            sendSuccess(response, userData, "Current user retrieved");
            
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Failed to get current user: " + e.getMessage());
        }
    }
}
