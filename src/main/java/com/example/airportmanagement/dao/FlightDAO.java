package com.example.airportmanagement.dao;

import com.example.airportmanagement.db.DatabaseConnection;
import com.example.airportmanagement.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Flight entity
 */
public class FlightDAO {
    private static final Logger logger = LoggerFactory.getLogger(FlightDAO.class);
    
    private static final String INSERT_FLIGHT = 
        "INSERT INTO flights (flight_number, source_airport_id, destination_airport_id, " +
        "departure_time, arrival_time, seats_available, price, status, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    
    private static final String SELECT_FLIGHT_BY_ID = 
        "SELECT f.*, " +
        "sa.code as source_code, sa.name as source_name, " +
        "da.code as dest_code, da.name as dest_name " +
        "FROM flights f " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "WHERE f.id = ?";
    
    private static final String SELECT_ALL_FLIGHTS = 
        "SELECT f.*, " +
        "sa.code as source_code, sa.name as source_name, " +
        "da.code as dest_code, da.name as dest_name " +
        "FROM flights f " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "ORDER BY f.departure_time";
    
    private static final String UPDATE_FLIGHT = 
        "UPDATE flights SET flight_number = ?, source_airport_id = ?, " +
        "destination_airport_id = ?, departure_time = ?, arrival_time = ?, " +
        "seats_available = ?, price = ?, status = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String DELETE_FLIGHT = 
        "DELETE FROM flights WHERE id = ?";
    
    private static final String SEARCH_FLIGHTS = 
        "SELECT f.*, " +
        "sa.code as source_code, sa.name as source_name, " +
        "da.code as dest_code, da.name as dest_name " +
        "FROM flights f " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "WHERE (sa.code = ? OR ? IS NULL) " +
        "AND (da.code = ? OR ? IS NULL) " +
        "AND (DATE(f.departure_time) = DATE(?) OR ? IS NULL) " +
        "AND f.status != 'CANCELLED' " +
        "ORDER BY f.departure_time";
    
    private static final String UPDATE_SEATS = 
        "UPDATE flights SET seats_available = seats_available - ?, updated_at = NOW() WHERE id = ?";
    
    /**
     * Create a new flight
     */
    public Flight create(Flight flight) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_FLIGHT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, flight.getFlightNumber());
            stmt.setLong(2, flight.getSourceAirportId());
            stmt.setLong(3, flight.getDestinationAirportId());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(6, flight.getSeatsAvailable());
            stmt.setDouble(7, flight.getPrice());
            stmt.setString(8, flight.getStatus() != null ? flight.getStatus().name() : "SCHEDULED");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating flight failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                flight.setId(rs.getLong(1));
            }
            
            logger.info("Flight created: {}", flight.getFlightNumber());
            return flight;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Find flight by ID
     */
    public Optional<Flight> findById(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_FLIGHT_BY_ID);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToFlight(rs));
            }
            
            return Optional.empty();
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Get all flights
     */
    public List<Flight> findAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Flight> flights = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_FLIGHTS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
            
            return flights;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Search flights
     */
    public List<Flight> search(String sourceCode, String destCode, String date) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Flight> flights = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SEARCH_FLIGHTS);
            
            stmt.setString(1, sourceCode);
            stmt.setString(2, sourceCode);
            stmt.setString(3, destCode);
            stmt.setString(4, destCode);
            stmt.setString(5, date);
            stmt.setString(6, date);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
            
            return flights;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Update flight
     */
    public boolean update(Flight flight) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_FLIGHT);
            
            stmt.setString(1, flight.getFlightNumber());
            stmt.setLong(2, flight.getSourceAirportId());
            stmt.setLong(3, flight.getDestinationAirportId());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(6, flight.getSeatsAvailable());
            stmt.setDouble(7, flight.getPrice());
            stmt.setString(8, flight.getStatus().name());
            stmt.setLong(9, flight.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Flight updated: {}", flight.getFlightNumber());
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Update available seats (for booking)
     */
    public boolean updateSeats(Long flightId, int seatsToBook) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_SEATS);
            
            stmt.setInt(1, seatsToBook);
            stmt.setLong(2, flightId);
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Flight seats updated: flightId={}, seats={}", flightId, seatsToBook);
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete flight
     */
    public boolean delete(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_FLIGHT);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Flight deleted: {}", id);
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Map ResultSet to Flight object
     */
    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getLong("id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setSourceAirportId(rs.getLong("source_airport_id"));
        flight.setDestinationAirportId(rs.getLong("destination_airport_id"));
        
        Timestamp departure = rs.getTimestamp("departure_time");
        if (departure != null) {
            flight.setDepartureTime(departure.toLocalDateTime());
        }
        
        Timestamp arrival = rs.getTimestamp("arrival_time");
        if (arrival != null) {
            flight.setArrivalTime(arrival.toLocalDateTime());
        }
        
        flight.setSeatsAvailable(rs.getInt("seats_available"));
        flight.setPrice(rs.getDouble("price"));
        flight.setStatus(Flight.FlightStatus.valueOf(rs.getString("status")));
        
        // Set airport codes and names from joined tables
        flight.setSourceAirportCode(rs.getString("source_code"));
        flight.setSourceAirportName(rs.getString("source_name"));
        flight.setDestinationAirportCode(rs.getString("dest_code"));
        flight.setDestinationAirportName(rs.getString("dest_name"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            flight.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            flight.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return flight;
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
