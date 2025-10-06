package com.example.airportmanagement.db;

import com.example.airportmanagement.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Connection Pool Implementation
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final List<Connection> connectionPool = new ArrayList<>();
    private static final List<Connection> usedConnections = new ArrayList<>();
    
    static {
        try {
            Class.forName(AppConfig.getDbDriver());
            initializePool();
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
            throw new RuntimeException("Failed to load database driver", e);
        }
    }
    
    private static void initializePool() {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connectionPool.add(createConnection());
        }
        logger.info("Database connection pool initialized with {} connections", INITIAL_POOL_SIZE);
    }
    
    private static Connection createConnection() {
        try {
            return DriverManager.getConnection(
                AppConfig.getDbUrl(),
                AppConfig.getDbUsername(),
                AppConfig.getDbPassword()
            );
        } catch (SQLException e) {
            logger.error("Failed to create database connection", e);
            throw new RuntimeException("Failed to create database connection", e);
        }
    }
    
    public static synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection());
            } else {
                throw new SQLException("Maximum pool size reached, no available connections");
            }
        }
        
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        
        // Check if connection is still valid
        if (!connection.isValid(1)) {
            connection = createConnection();
        }
        
        usedConnections.add(connection);
        logger.debug("Connection retrieved from pool. Available: {}, Used: {}", 
            connectionPool.size(), usedConnections.size());
        
        return connection;
    }
    
    public static synchronized boolean releaseConnection(Connection connection) {
        if (connection == null) {
            return false;
        }
        
        connectionPool.add(connection);
        boolean removed = usedConnections.remove(connection);
        logger.debug("Connection returned to pool. Available: {}, Used: {}", 
            connectionPool.size(), usedConnections.size());
        
        return removed;
    }
    
    public static void shutdown() {
        usedConnections.forEach(DatabaseConnection::closeConnection);
        connectionPool.forEach(DatabaseConnection::closeConnection);
        usedConnections.clear();
        connectionPool.clear();
        logger.info("Database connection pool shut down");
    }
    
    private static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close connection", e);
        }
    }
}
