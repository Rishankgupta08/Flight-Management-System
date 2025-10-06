package com.example.airportmanagement.dao;

import com.example.airportmanagement.db.DatabaseConnection;
import com.example.airportmanagement.model.Airport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Airport entity
 */
public class AirportDAO {
    private static final Logger logger = LoggerFactory.getLogger(AirportDAO.class);
    
    private static final String INSERT_AIRPORT = 
        "INSERT INTO airports (name, code, city, country, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, NOW(), NOW())";
    
    private static final String SELECT_AIRPORT_BY_ID = 
        "SELECT * FROM airports WHERE id = ?";
    
    private static final String SELECT_AIRPORT_BY_CODE = 
        "SELECT * FROM airports WHERE code = ?";
    
    private static final String SELECT_ALL_AIRPORTS = 
        "SELECT * FROM airports ORDER BY name";
    
    private static final String UPDATE_AIRPORT = 
        "UPDATE airports SET name = ?, code = ?, city = ?, country = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String DELETE_AIRPORT = 
        "DELETE FROM airports WHERE id = ?";
    
    private static final String SEARCH_AIRPORTS = 
        "SELECT * FROM airports WHERE name LIKE ? OR code LIKE ? OR city LIKE ? OR country LIKE ?";
    
    /**
     * Create a new airport
     */
    public Airport create(Airport airport) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_AIRPORT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, airport.getName());
            stmt.setString(2, airport.getCode());
            stmt.setString(3, airport.getCity());
            stmt.setString(4, airport.getCountry());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating airport failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                airport.setId(rs.getLong(1));
            }
            
            logger.info("Airport created: {}", airport.getCode());
            return airport;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Find airport by ID
     */
    public Optional<Airport> findById(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_AIRPORT_BY_ID);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAirport(rs));
            }
            
            return Optional.empty();
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Find airport by code
     */
    public Optional<Airport> findByCode(String code) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_AIRPORT_BY_CODE);
            stmt.setString(1, code);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAirport(rs));
            }
            
            return Optional.empty();
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Get all airports
     */
    public List<Airport> findAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_AIRPORTS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                airports.add(mapResultSetToAirport(rs));
            }
            
            return airports;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Search airports by keyword
     */
    public List<Airport> search(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SEARCH_AIRPORTS);
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                airports.add(mapResultSetToAirport(rs));
            }
            
            return airports;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Update airport
     */
    public boolean update(Airport airport) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_AIRPORT);
            
            stmt.setString(1, airport.getName());
            stmt.setString(2, airport.getCode());
            stmt.setString(3, airport.getCity());
            stmt.setString(4, airport.getCountry());
            stmt.setLong(5, airport.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Airport updated: {}", airport.getCode());
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete airport
     */
    public boolean delete(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_AIRPORT);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Airport deleted: {}", id);
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Map ResultSet to Airport object
     */
    private Airport mapResultSetToAirport(ResultSet rs) throws SQLException {
        Airport airport = new Airport();
        airport.setId(rs.getLong("id"));
        airport.setName(rs.getString("name"));
        airport.setCode(rs.getString("code"));
        airport.setCity(rs.getString("city"));
        airport.setCountry(rs.getString("country"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            airport.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            airport.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return airport;
    }
    
    /**
     * Close database resources
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseConnection.releaseConnection(conn);
        } catch (SQLException e) {
            logger.error("Error closing resources", e);
        }
    }
}
