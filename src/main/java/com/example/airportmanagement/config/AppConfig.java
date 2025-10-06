package com.example.airportmanagement.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application Configuration Manager
 */
public class AppConfig {
    private static final Properties dbProperties = new Properties();
    private static final Properties messageProperties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream dbStream = AppConfig.class.getClassLoader()
                .getResourceAsStream("db.properties");
             InputStream msgStream = AppConfig.class.getClassLoader()
                .getResourceAsStream("messages.properties")) {
            
            if (dbStream != null) {
                dbProperties.load(dbStream);
            }
            if (msgStream != null) {
                messageProperties.load(msgStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration files", e);
        }
    }
    
    public static String getDbProperty(String key) {
        return dbProperties.getProperty(key);
    }
    
    public static String getMessage(String key) {
        return messageProperties.getProperty(key);
    }
    
    public static String getDbUrl() {
        return getDbProperty("db.url");
    }
    
    public static String getDbUsername() {
        return getDbProperty("db.username");
    }
    
    public static String getDbPassword() {
        return getDbProperty("db.password");
    }
    
    public static String getDbDriver() {
        return getDbProperty("db.driver");
    }
}
