package com.example.airportmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    private static final Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{3,4}$");
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate airport code (IATA format: 3 uppercase letters)
     */
    public static boolean isValidAirportCode(String code) {
        return code != null && AIRPORT_CODE_PATTERN.matcher(code).matches();
    }
    
    /**
     * Validate flight number format (e.g., AA123, BA1234)
     */
    public static boolean isValidFlightNumber(String flightNumber) {
        return flightNumber != null && FLIGHT_NUMBER_PATTERN.matcher(flightNumber).matches();
    }
    
    /**
     * Validate username (alphanumeric, 3-20 characters)
     */
    public static boolean isValidUsername(String username) {
        return username != null && 
               username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Check if string is not null or empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(Integer number) {
        return number != null && number > 0;
    }
    
    /**
     * Validate positive double
     */
    public static boolean isPositiveDouble(Double number) {
        return number != null && number > 0;
    }
    
    /**
     * Parse date-time string
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Validate that departure time is before arrival time
     */
    public static boolean isValidFlightTimes(LocalDateTime departure, LocalDateTime arrival) {
        return departure != null && arrival != null && departure.isBefore(arrival);
    }
    
    /**
     * Validate that departure time is in the future
     */
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
}
