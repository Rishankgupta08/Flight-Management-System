package com.example.airportmanagement.service;

import com.example.airportmanagement.dao.UserDAO;
import com.example.airportmanagement.model.User;
import com.example.airportmanagement.util.PasswordUtil;
import com.example.airportmanagement.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for User business logic
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Register a new user
     */
    public User register(String username, String password, String email, User.UserRole role) 
            throws ServiceException {
        try {
            // Validate inputs
            if (!ValidationUtil.isValidUsername(username)) {
                throw new ServiceException("Invalid username. Must be 3-20 alphanumeric characters.");
            }
            
            if (!PasswordUtil.isValidPassword(password)) {
                throw new ServiceException("Password must be at least 8 characters with uppercase, lowercase, and digit.");
            }
            
            if (!ValidationUtil.isValidEmail(email)) {
                throw new ServiceException("Invalid email format.");
            }
            
            // Check if user already exists
            if (userDAO.findByUsername(username).isPresent()) {
                throw new ServiceException("Username already exists.");
            }
            
            if (userDAO.findByEmail(email).isPresent()) {
                throw new ServiceException("Email already registered.");
            }
            
            // Hash password
            String passwordHash = PasswordUtil.hashPassword(password);
            
            // Create user
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(passwordHash);
            user.setEmail(email);
            user.setRole(role != null ? role : User.UserRole.PASSENGER);
            
            User createdUser = userDAO.create(user);
            logger.info("User registered successfully: {}", username);
            
            return createdUser;
            
        } catch (SQLException e) {
            logger.error("Error registering user", e);
            throw new ServiceException("Failed to register user: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user login
     */
    public User login(String username, String password) throws ServiceException {
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                throw new ServiceException("Invalid username or password.");
            }
            
            User user = userOpt.get();
            
            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                throw new ServiceException("Invalid username or password.");
            }
            
            logger.info("User logged in successfully: {}", username);
            return user;
            
        } catch (SQLException e) {
            logger.error("Error during login", e);
            throw new ServiceException("Login failed: " + e.getMessage());
        }
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long id) throws ServiceException {
        try {
            return userDAO.findById(id)
                .orElseThrow(() -> new ServiceException("User not found"));
        } catch (SQLException e) {
            logger.error("Error fetching user", e);
            throw new ServiceException("Failed to fetch user: " + e.getMessage());
        }
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() throws ServiceException {
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching users", e);
            throw new ServiceException("Failed to fetch users: " + e.getMessage());
        }
    }
    
    /**
     * Update user
     */
    public boolean updateUser(User user) throws ServiceException {
        try {
            if (!ValidationUtil.isValidUsername(user.getUsername())) {
                throw new ServiceException("Invalid username.");
            }
            
            if (!ValidationUtil.isValidEmail(user.getEmail())) {
                throw new ServiceException("Invalid email format.");
            }
            
            return userDAO.update(user);
            
        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new ServiceException("Failed to update user: " + e.getMessage());
        }
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(Long id) throws ServiceException {
        try {
            return userDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
            throw new ServiceException("Failed to delete user: " + e.getMessage());
        }
    }
}
