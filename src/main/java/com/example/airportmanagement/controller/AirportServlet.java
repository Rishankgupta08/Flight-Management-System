package com.example.airportmanagement.controller;

import com.example.airportmanagement.model.Airport;
import com.example.airportmanagement.model.User;
import com.example.airportmanagement.service.AirportService;
import com.example.airportmanagement.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Airport Management Servlet
 */
@WebServlet(name = "AirportServlet", urlPatterns = {"/airport/*"})
public class AirportServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AirportServlet.class);
    private final AirportService airportService;
    
    public AirportServlet() {
        this.airportService = new AirportService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || "/list".equals(pathInfo)) {
            handleGetAll(request, response);
        } else if (pathInfo.startsWith("/search")) {
            handleSearch(request, response);
        } else if (pathInfo.matches("/\\d+")) {
            handleGetById(request, response);
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication and authorization (Admin only for create)
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        if (!hasRole(session, User.UserRole.ADMIN)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can create airports");
            return;
        }
        
        handleCreate(request, response);
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication and authorization (Admin only for update)
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        if (!hasRole(session, User.UserRole.ADMIN)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can update airports");
            return;
        }
        
        handleUpdate(request, response);
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication and authorization (Admin only for delete)
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        if (!hasRole(session, User.UserRole.ADMIN)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can delete airports");
            return;
        }
        
        handleDelete(request, response);
    }
    
    /**
     * Get all airports
     */
    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Airport> airports = airportService.getAllAirports();
            sendSuccess(response, airports, "Airports retrieved successfully");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Get airport by ID
     */
    private void handleGetById(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            Airport airport = airportService.getAirportById(id);
            sendSuccess(response, airport, "Airport retrieved successfully");
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid airport ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
    
    /**
     * Search airports
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String keyword = request.getParameter("q");
            List<Airport> airports = airportService.searchAirports(keyword);
            sendSuccess(response, airports, "Search completed successfully");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Create new airport
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String name = request.getParameter("name");
            String code = request.getParameter("code");
            String city = request.getParameter("city");
            String country = request.getParameter("country");
            
            Airport airport = airportService.createAirport(name, code, city, country);
            sendSuccess(response, airport, "Airport created successfully");
            logger.info("Airport created: {}", code);
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Update airport
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String name = request.getParameter("name");
            String code = request.getParameter("code");
            String city = request.getParameter("city");
            String country = request.getParameter("country");
            
            Airport airport = airportService.getAirportById(id);
            airport.setName(name);
            airport.setCode(code);
            airport.setCity(city);
            airport.setCountry(country);
            
            boolean updated = airportService.updateAirport(airport);
            
            if (updated) {
                sendSuccess(response, airport, "Airport updated successfully");
                logger.info("Airport updated: {}", code);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to update airport");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid airport ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Delete airport
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            boolean deleted = airportService.deleteAirport(id);
            
            if (deleted) {
                sendSuccess(response, null, "Airport deleted successfully");
                logger.info("Airport deleted: {}", id);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to delete airport");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid airport ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
