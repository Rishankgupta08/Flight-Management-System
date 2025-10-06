package com.example.airportmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final DateTimeFormatter ISO_FORMATTER = 
        DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Format LocalDateTime to string
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    /**
     * Format LocalDateTime to ISO string
     */
    public static String formatISO(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER);
    }
    
    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    /**
     * Get current date-time
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Calculate duration in hours between two date-times
     */
    public static long getDurationInHours(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.Duration.between(start, end).toHours();
    }
}
