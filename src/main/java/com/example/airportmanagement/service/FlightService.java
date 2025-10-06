package com.example.airportmanagement.service;

import com.example.airportmanagement.dao.FlightDAO;
import com.example.airportmanagement.model.Flight;
import com.example.airportmanagement.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Flight business logic
 */
public class FlightService {
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    private final FlightDAO flightDAO;
    
    public FlightService() {
        this.flightDAO = new FlightDAO();
    }
    
    /**
     * Schedule a new flight
     */
    public Flight scheduleFlight(String flightNumber, Long sourceAirportId, 
                                Long destinationAirportId, LocalDateTime departureTime,
                                LocalDateTime arrivalTime, Integer seatsAvailable, 
                                Double price) throws ServiceException {
        try {
            // Validate inputs
            if (!ValidationUtil.isValidFlightNumber(flightNumber)) {
                throw new ServiceException("Invalid flight number format. Expected format: AA123 or BA1234.");
            }
            
            if (sourceAirportId == null || destinationAirportId == null) {
                throw new ServiceException("Source and destination airports are required.");
            }
            
            if (sourceAirportId.equals(destinationAirportId)) {
                throw new ServiceException("Source and destination airports cannot be the same.");
            }
            
            if (!ValidationUtil.isValidFlightTimes(departureTime, arrivalTime)) {
                throw new ServiceException("Arrival time must be after departure time.");
            }
            
            if (!ValidationUtil.isFutureDateTime(departureTime)) {
                throw new ServiceException("Departure time must be in the future.");
            }
            
            if (!ValidationUtil.isPositiveInteger(seatsAvailable)) {
                throw new ServiceException("Seats available must be a positive number.");
            }
            
            if (!ValidationUtil.isPositiveDouble(price)) {
                throw new ServiceException("Price must be a positive number.");
            }
            
            // Create flight
            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber.toUpperCase());
            flight.setSourceAirportId(sourceAirportId);
            flight.setDestinationAirportId(destinationAirportId);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            flight.setSeatsAvailable(seatsAvailable);
            flight.setPrice(price);
            flight.setStatus(Flight.FlightStatus.SCHEDULED);
            
            Flight createdFlight = flightDAO.create(flight);
            logger.info("Flight scheduled successfully: {}", flightNumber);
            
            return createdFlight;
            
        } catch (SQLException e) {
            logger.error("Error scheduling flight", e);
            throw new ServiceException("Failed to schedule flight: " + e.getMessage());
        }
    }
    
    /**
     * Get flight by ID
     */
    public Flight getFlightById(Long id) throws ServiceException {
        try {
            return flightDAO.findById(id)
                .orElseThrow(() -> new ServiceException("Flight not found"));
        } catch (SQLException e) {
            logger.error("Error fetching flight", e);
            throw new ServiceException("Failed to fetch flight: " + e.getMessage());
        }
    }
    
    /**
     * Get all flights
     */
    public List<Flight> getAllFlights() throws ServiceException {
        try {
            return flightDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching flights", e);
            throw new ServiceException("Failed to fetch flights: " + e.getMessage());
        }
    }
    
    /**
     * Search flights
     */
    public List<Flight> searchFlights(String sourceCode, String destCode, String date) 
            throws ServiceException {
        try {
            return flightDAO.search(sourceCode, destCode, date);
        } catch (SQLException e) {
            logger.error("Error searching flights", e);
            throw new ServiceException("Failed to search flights: " + e.getMessage());
        }
    }
    
    /**
     * Update flight
     */
    public boolean updateFlight(Flight flight) throws ServiceException {
        try {
            if (!ValidationUtil.isValidFlightNumber(flight.getFlightNumber())) {
                throw new ServiceException("Invalid flight number format.");
            }
            
            if (!ValidationUtil.isValidFlightTimes(flight.getDepartureTime(), flight.getArrivalTime())) {
                throw new ServiceException("Arrival time must be after departure time.");
            }
            
            if (!ValidationUtil.isPositiveInteger(flight.getSeatsAvailable())) {
                throw new ServiceException("Seats available must be a positive number.");
            }
            
            if (!ValidationUtil.isPositiveDouble(flight.getPrice())) {
                throw new ServiceException("Price must be a positive number.");
            }
            
            return flightDAO.update(flight);
            
        } catch (SQLException e) {
            logger.error("Error updating flight", e);
            throw new ServiceException("Failed to update flight: " + e.getMessage());
        }
    }
    
    /**
     * Cancel flight
     */
    public boolean cancelFlight(Long id) throws ServiceException {
        try {
            Flight flight = getFlightById(id);
            flight.setStatus(Flight.FlightStatus.CANCELLED);
            
            boolean updated = flightDAO.update(flight);
            logger.info("Flight cancelled: {}", flight.getFlightNumber());
            
            return updated;
            
        } catch (SQLException e) {
            logger.error("Error cancelling flight", e);
            throw new ServiceException("Failed to cancel flight: " + e.getMessage());
        }
    }
    
    /**
     * Delete flight
     */
    public boolean deleteFlight(Long id) throws ServiceException {
        try {
            return flightDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Error deleting flight", e);
            throw new ServiceException("Failed to delete flight: " + e.getMessage());
        }
    }
    
    /**
     * Check if seats are available for booking
     */
    public boolean checkSeatAvailability(Long flightId, int requestedSeats) throws ServiceException {
        Flight flight = getFlightById(flightId);
        return flight.getSeatsAvailable() >= requestedSeats;
    }
}
