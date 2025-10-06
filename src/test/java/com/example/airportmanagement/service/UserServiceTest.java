package com.example.airportmanagement.service;

import com.example.airportmanagement.model.User;
import com.example.airportmanagement.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserService
 */
public class UserServiceTest {
    
    @Test
    public void testPasswordHashing() {
        String password = "TestPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(PasswordUtil.verifyPassword(password, hashed));
        assertFalse(PasswordUtil.verifyPassword("wrongpassword", hashed));
    }
    
    @Test
    public void testPasswordValidation() {
        assertTrue(PasswordUtil.isValidPassword("ValidPass123"));
        assertTrue(PasswordUtil.isValidPassword("AnotherValid1"));
        assertFalse(PasswordUtil.isValidPassword("short1A"));
        assertFalse(PasswordUtil.isValidPassword("nouppercase123"));
        assertFalse(PasswordUtil.isValidPassword("NOLOWERCASE123"));
        assertFalse(PasswordUtil.isValidPassword("NoDigitsHere"));
    }
}
