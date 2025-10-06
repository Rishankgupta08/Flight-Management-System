package com.example.airportmanagement.service;

import com.example.airportmanagement.dao.BookingDAO;
import com.example.airportmanagement.dao.FlightDAO;
import com.example.airportmanagement.model.Booking;
import com.example.airportmanagement.model.Flight;
import com.example.airportmanagement.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Booking business logic
 */
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingDAO bookingDAO;
    private final FlightDAO flightDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
    }
    
    /**
     * Create a new booking
     */
    public Booking createBooking(Long userId, Long flightId, Integer seatsBooked) 
            throws ServiceException {
        try {
            // Validate inputs
            if (userId == null || flightId == null) {
                throw new ServiceException("User ID and Flight ID are required.");
            }
            
            if (!ValidationUtil.isPositiveInteger(seatsBooked)) {
                throw new ServiceException("Seats booked must be a positive number.");
            }
            
            // Get flight details
            Flight flight = flightDAO.findById(flightId)
                .orElseThrow(() -> new ServiceException("Flight not found."));
            
            // Check if flight is cancelled
            if (flight.getStatus() == Flight.FlightStatus.CANCELLED) {
                throw new ServiceException("Cannot book a cancelled flight.");
            }
            
            // Check seat availability
            if (flight.getSeatsAvailable() < seatsBooked) {
                throw new ServiceException("Not enough seats available. Only " + 
                    flight.getSeatsAvailable() + " seats left.");
            }
            
            // Calculate total price
            double totalPrice = flight.getPrice() * seatsBooked;
            
            // Create booking
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setFlightId(flightId);
            booking.setSeatsBooked(seatsBooked);
            booking.setTotalPrice(totalPrice);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            
            // Update flight seats
            boolean seatsUpdated = flightDAO.updateSeats(flightId, seatsBooked);
            if (!seatsUpdated) {
                throw new ServiceException("Failed to update flight seats.");
            }
            
            Booking createdBooking = bookingDAO.create(booking);
            logger.info("Booking created successfully: userId={}, flightId={}, seats={}", 
                userId, flightId, seatsBooked);
            
            return createdBooking;
            
        } catch (SQLException e) {
            logger.error("Error creating booking", e);
            throw new ServiceException("Failed to create booking: " + e.getMessage());
        }
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(Long id) throws ServiceException {
        try {
            return bookingDAO.findById(id)
                .orElseThrow(() -> new ServiceException("Booking not found"));
        } catch (SQLException e) {
            logger.error("Error fetching booking", e);
            throw new ServiceException("Failed to fetch booking: " + e.getMessage());
        }
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() throws ServiceException {
        try {
            return bookingDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching bookings", e);
            throw new ServiceException("Failed to fetch bookings: " + e.getMessage());
        }
    }
    
    /**
     * Get bookings by user ID
     */
    public List<Booking> getBookingsByUserId(Long userId) throws ServiceException {
        try {
            return bookingDAO.findByUserId(userId);
        } catch (SQLException e) {
            logger.error("Error fetching user bookings", e);
            throw new ServiceException("Failed to fetch user bookings: " + e.getMessage());
        }
    }
    
    /**
     * Cancel booking
     */
    public boolean cancelBooking(Long bookingId, Long userId) throws ServiceException {
        try {
            // Get booking
            Booking booking = getBookingById(bookingId);
            
            // Check if booking belongs to user
            if (!booking.getUserId().equals(userId)) {
                throw new ServiceException("Unauthorized: This booking does not belong to you.");
            }
            
            // Check if booking is already cancelled
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                throw new ServiceException("Booking is already cancelled.");
            }
            
            // Update booking status
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            boolean updated = bookingDAO.update(booking);
            
            if (updated) {
                // Return seats to flight
                Flight flight = flightDAO.findById(booking.getFlightId())
                    .orElseThrow(() -> new ServiceException("Flight not found."));
                
                flight.setSeatsAvailable(flight.getSeatsAvailable() + booking.getSeatsBooked());
                flightDAO.update(flight);
                
                logger.info("Booking cancelled: bookingId={}, userId={}", bookingId, userId);
            }
            
            return updated;
            
        } catch (SQLException e) {
            logger.error("Error cancelling booking", e);
            throw new ServiceException("Failed to cancel booking: " + e.getMessage());
        }
    }
    
    /**
     * Delete booking (admin only)
     */
    public boolean deleteBooking(Long id) throws ServiceException {
        try {
            return bookingDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Error deleting booking", e);
            throw new ServiceException("Failed to delete booking: " + e.getMessage());
        }
    }
}
