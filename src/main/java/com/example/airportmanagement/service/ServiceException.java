package com.example.airportmanagement.service;

/**
 * Custom exception for service layer
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
