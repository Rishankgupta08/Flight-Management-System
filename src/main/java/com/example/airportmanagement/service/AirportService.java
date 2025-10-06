package com.example.airportmanagement.service;

import com.example.airportmanagement.dao.AirportDAO;
import com.example.airportmanagement.model.Airport;
import com.example.airportmanagement.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Airport business logic
 */
public class AirportService {
    private static final Logger logger = LoggerFactory.getLogger(AirportService.class);
    private final AirportDAO airportDAO;
    
    public AirportService() {
        this.airportDAO = new AirportDAO();
    }
    
    /**
     * Create a new airport
     */
    public Airport createAirport(String name, String code, String city, String country) 
            throws ServiceException {
        try {
            // Validate inputs
            if (!ValidationUtil.isNotEmpty(name)) {
                throw new ServiceException("Airport name is required.");
            }
            
            if (!ValidationUtil.isValidAirportCode(code)) {
                throw new ServiceException("Invalid airport code. Must be 3 uppercase letters (e.g., JFK).");
            }
            
            if (!ValidationUtil.isNotEmpty(city)) {
                throw new ServiceException("City is required.");
            }
            
            if (!ValidationUtil.isNotEmpty(country)) {
                throw new ServiceException("Country is required.");
            }
            
            // Check if airport code already exists
            if (airportDAO.findByCode(code).isPresent()) {
                throw new ServiceException("Airport code already exists.");
            }
            
            // Create airport
            Airport airport = new Airport();
            airport.setName(name);
            airport.setCode(code.toUpperCase());
            airport.setCity(city);
            airport.setCountry(country);
            
            Airport createdAirport = airportDAO.create(airport);
            logger.info("Airport created successfully: {}", code);
            
            return createdAirport;
            
        } catch (SQLException e) {
            logger.error("Error creating airport", e);
            throw new ServiceException("Failed to create airport: " + e.getMessage());
        }
    }
    
    /**
     * Get airport by ID
     */
    public Airport getAirportById(Long id) throws ServiceException {
        try {
            return airportDAO.findById(id)
                .orElseThrow(() -> new ServiceException("Airport not found"));
        } catch (SQLException e) {
            logger.error("Error fetching airport", e);
            throw new ServiceException("Failed to fetch airport: " + e.getMessage());
        }
    }
    
    /**
     * Get airport by code
     */
    public Airport getAirportByCode(String code) throws ServiceException {
        try {
            return airportDAO.findByCode(code)
                .orElseThrow(() -> new ServiceException("Airport not found"));
        } catch (SQLException e) {
            logger.error("Error fetching airport", e);
            throw new ServiceException("Failed to fetch airport: " + e.getMessage());
        }
    }
    
    /**
     * Get all airports
     */
    public List<Airport> getAllAirports() throws ServiceException {
        try {
            return airportDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching airports", e);
            throw new ServiceException("Failed to fetch airports: " + e.getMessage());
        }
    }
    
    /**
     * Search airports
     */
    public List<Airport> searchAirports(String keyword) throws ServiceException {
        try {
            if (!ValidationUtil.isNotEmpty(keyword)) {
                return getAllAirports();
            }
            return airportDAO.search(keyword);
        } catch (SQLException e) {
            logger.error("Error searching airports", e);
            throw new ServiceException("Failed to search airports: " + e.getMessage());
        }
    }
    
    /**
     * Update airport
     */
    public boolean updateAirport(Airport airport) throws ServiceException {
        try {
            if (!ValidationUtil.isNotEmpty(airport.getName())) {
                throw new ServiceException("Airport name is required.");
            }
            
            if (!ValidationUtil.isValidAirportCode(airport.getCode())) {
                throw new ServiceException("Invalid airport code.");
            }
            
            if (!ValidationUtil.isNotEmpty(airport.getCity())) {
                throw new ServiceException("City is required.");
            }
            
            if (!ValidationUtil.isNotEmpty(airport.getCountry())) {
                throw new ServiceException("Country is required.");
            }
            
            return airportDAO.update(airport);
            
        } catch (SQLException e) {
            logger.error("Error updating airport", e);
            throw new ServiceException("Failed to update airport: " + e.getMessage());
        }
    }
    
    /**
     * Delete airport
     */
    public boolean deleteAirport(Long id) throws ServiceException {
        try {
            return airportDAO.delete(id);
        } catch (SQLException e) {
            logger.error("Error deleting airport", e);
            throw new ServiceException("Failed to delete airport: " + e.getMessage());
        }
    }
}
