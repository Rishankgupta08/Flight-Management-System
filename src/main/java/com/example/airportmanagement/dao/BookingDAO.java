package com.example.airportmanagement.dao;

import com.example.airportmanagement.db.DatabaseConnection;
import com.example.airportmanagement.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Booking entity
 */
public class BookingDAO {
    private static final Logger logger = LoggerFactory.getLogger(BookingDAO.class);
    
    private static final String INSERT_BOOKING = 
        "INSERT INTO bookings (user_id, flight_id, seats_booked, total_price, status, " +
        "booking_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW(), NOW())";
    
    private static final String SELECT_BOOKING_BY_ID = 
        "SELECT b.*, u.username, f.flight_number, " +
        "sa.code as source_code, da.code as dest_code, f.departure_time " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN flights f ON b.flight_id = f.id " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "WHERE b.id = ?";
    
    private static final String SELECT_ALL_BOOKINGS = 
        "SELECT b.*, u.username, f.flight_number, " +
        "sa.code as source_code, da.code as dest_code, f.departure_time " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN flights f ON b.flight_id = f.id " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "ORDER BY b.booking_date DESC";
    
    private static final String SELECT_BOOKINGS_BY_USER = 
        "SELECT b.*, u.username, f.flight_number, " +
        "sa.code as source_code, da.code as dest_code, f.departure_time " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN flights f ON b.flight_id = f.id " +
        "JOIN airports sa ON f.source_airport_id = sa.id " +
        "JOIN airports da ON f.destination_airport_id = da.id " +
        "WHERE b.user_id = ? " +
        "ORDER BY b.booking_date DESC";
    
    private static final String UPDATE_BOOKING = 
        "UPDATE bookings SET status = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String DELETE_BOOKING = 
        "DELETE FROM bookings WHERE id = ?";
    
    /**
     * Create a new booking
     */
    public Booking create(Booking booking) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setLong(1, booking.getUserId());
            stmt.setLong(2, booking.getFlightId());
            stmt.setInt(3, booking.getSeatsBooked());
            stmt.setDouble(4, booking.getTotalPrice());
            stmt.setString(5, booking.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setId(rs.getLong(1));
            }
            
            logger.info("Booking created: userId={}, flightId={}", booking.getUserId(), booking.getFlightId());
            return booking;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Find booking by ID
     */
    public Optional<Booking> findById(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_BOOKING_BY_ID);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToBooking(rs));
            }
            
            return Optional.empty();
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> findAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_BOOKINGS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
            return bookings;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Get bookings by user ID
     */
    public List<Booking> findByUserId(Long userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_BOOKINGS_BY_USER);
            stmt.setLong(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
            return bookings;
            
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Update booking status
     */
    public boolean update(Booking booking) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_BOOKING);
            
            stmt.setString(1, booking.getStatus().name());
            stmt.setLong(2, booking.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Booking updated: id={}, status={}", booking.getId(), booking.getStatus());
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete booking
     */
    public boolean delete(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_BOOKING);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            logger.info("Booking deleted: {}", id);
            return affectedRows > 0;
            
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Map ResultSet to Booking object
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setFlightId(rs.getLong("flight_id"));
        booking.setSeatsBooked(rs.getInt("seats_booked"));
        booking.setTotalPrice(rs.getDouble("total_price"));
        booking.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        
        Timestamp bookingDate = rs.getTimestamp("booking_date");
        if (bookingDate != null) {
            booking.setBookingDate(bookingDate.toLocalDateTime());
        }
        
        // Set joined fields
        booking.setUsername(rs.getString("username"));
        booking.setFlightNumber(rs.getString("flight_number"));
        booking.setSourceAirportCode(rs.getString("source_code"));
        booking.setDestinationAirportCode(rs.getString("dest_code"));
        
        Timestamp departureTime = rs.getTimestamp("departure_time");
        if (departureTime != null) {
            booking.setDepartureTime(departureTime.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            booking.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return booking;
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
