package com.example.airportmanagement.dao;

import com.example.airportmanagement.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Unit tests for UserDAO
 */
public class UserDAOTest {
    
    @Test
    public void testUserModel() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(User.UserRole.PASSENGER);
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.UserRole.PASSENGER, user.getRole());
    }
    
    @Test
    public void testUserRoleEnum() {
        assertEquals("ADMIN", User.UserRole.ADMIN.name());
        assertEquals("STAFF", User.UserRole.STAFF.name());
        assertEquals("PASSENGER", User.UserRole.PASSENGER.name());
    }
}
