package com.example.airportmanagement.controller;

import com.example.airportmanagement.model.Flight;
import com.example.airportmanagement.model.User;
import com.example.airportmanagement.service.FlightService;
import com.example.airportmanagement.service.ServiceException;
import com.example.airportmanagement.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Flight Management Servlet
 */
@WebServlet(name = "FlightServlet", urlPatterns = {"/flight/*"})
public class FlightServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(FlightServlet.class);
    private final FlightService flightService;
    
    public FlightServlet() {
        this.flightService = new FlightService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || "/list".equals(pathInfo)) {
            handleGetAll(request, response);
        } else if ("/search".equals(pathInfo)) {
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
        
        // Check authentication and authorization (Admin/Staff for create)
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        if (!hasRole(session, User.UserRole.ADMIN, User.UserRole.STAFF)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators and staff can schedule flights");
            return;
        }
        
        handleCreate(request, response);
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication and authorization
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        if (!hasRole(session, User.UserRole.ADMIN, User.UserRole.STAFF)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators and staff can update flights");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/cancel/")) {
            handleCancel(request, response);
        } else {
            handleUpdate(request, response);
        }
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
                "Only administrators can delete flights");
            return;
        }
        
        handleDelete(request, response);
    }
    
    /**
     * Get all flights
     */
    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Flight> flights = flightService.getAllFlights();
            sendSuccess(response, flights, "Flights retrieved successfully");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Get flight by ID
     */
    private void handleGetById(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            Flight flight = flightService.getFlightById(id);
            sendSuccess(response, flight, "Flight retrieved successfully");
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid flight ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
    
    /**
     * Search flights
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String sourceCode = request.getParameter("from");
            String destCode = request.getParameter("to");
            String date = request.getParameter("date");
            
            List<Flight> flights = flightService.searchFlights(sourceCode, destCode, date);
            sendSuccess(response, flights, "Search completed successfully");
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Schedule new flight
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String flightNumber = request.getParameter("flightNumber");
            Long sourceAirportId = Long.parseLong(request.getParameter("sourceAirportId"));
            Long destAirportId = Long.parseLong(request.getParameter("destinationAirportId"));
            String departureStr = request.getParameter("departureTime");
            String arrivalStr = request.getParameter("arrivalTime");
            Integer seats = Integer.parseInt(request.getParameter("seatsAvailable"));
            Double price = Double.parseDouble(request.getParameter("price"));
            
            LocalDateTime departureTime = DateUtil.parse(departureStr);
            LocalDateTime arrivalTime = DateUtil.parse(arrivalStr);
            
            Flight flight = flightService.scheduleFlight(flightNumber, sourceAirportId, 
                destAirportId, departureTime, arrivalTime, seats, price);
            
            sendSuccess(response, flight, "Flight scheduled successfully");
            logger.info("Flight scheduled: {}", flightNumber);
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Update flight
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String flightNumber = request.getParameter("flightNumber");
            Long sourceAirportId = Long.parseLong(request.getParameter("sourceAirportId"));
            Long destAirportId = Long.parseLong(request.getParameter("destinationAirportId"));
            String departureStr = request.getParameter("departureTime");
            String arrivalStr = request.getParameter("arrivalTime");
            Integer seats = Integer.parseInt(request.getParameter("seatsAvailable"));
            Double price = Double.parseDouble(request.getParameter("price"));
            String statusStr = request.getParameter("status");
            
            Flight flight = flightService.getFlightById(id);
            flight.setFlightNumber(flightNumber);
            flight.setSourceAirportId(sourceAirportId);
            flight.setDestinationAirportId(destAirportId);
            flight.setDepartureTime(DateUtil.parse(departureStr));
            flight.setArrivalTime(DateUtil.parse(arrivalStr));
            flight.setSeatsAvailable(seats);
            flight.setPrice(price);
            
            if (statusStr != null && !statusStr.isEmpty()) {
                flight.setStatus(Flight.FlightStatus.valueOf(statusStr.toUpperCase()));
            }
            
            boolean updated = flightService.updateFlight(flight);
            
            if (updated) {
                sendSuccess(response, flight, "Flight updated successfully");
                logger.info("Flight updated: {}", flightNumber);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to update flight");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Cancel flight
     */
    private void handleCancel(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring("/cancel/".length()));
            
            boolean cancelled = flightService.cancelFlight(id);
            
            if (cancelled) {
                sendSuccess(response, null, "Flight cancelled successfully");
                logger.info("Flight cancelled: {}", id);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to cancel flight");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid flight ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Delete flight
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            boolean deleted = flightService.deleteFlight(id);
            
            if (deleted) {
                sendSuccess(response, null, "Flight deleted successfully");
                logger.info("Flight deleted: {}", id);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to delete flight");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid flight ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
