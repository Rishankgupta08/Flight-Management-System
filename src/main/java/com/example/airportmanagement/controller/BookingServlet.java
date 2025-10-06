package com.example.airportmanagement.controller;

import com.example.airportmanagement.model.Booking;
import com.example.airportmanagement.model.User;
import com.example.airportmanagement.service.BookingService;
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
 * Booking Management Servlet
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/booking/*"})
public class BookingServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BookingServlet.class);
    private final BookingService bookingService;
    
    public BookingServlet() {
        this.bookingService = new BookingService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || "/list".equals(pathInfo)) {
            handleGetAll(request, response);
        } else if ("/my-bookings".equals(pathInfo)) {
            handleGetMyBookings(request, response);
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
        
        // Check authentication
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        handleCreate(request, response);
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check authentication
        if (!isAuthenticated(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/cancel/")) {
            handleCancel(request, response);
        } else {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
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
                "Only administrators can delete bookings");
            return;
        }
        
        handleDelete(request, response);
    }
    
    /**
     * Get all bookings (Admin/Staff only)
     */
    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            
            // Only Admin and Staff can see all bookings
            if (!hasRole(session, User.UserRole.ADMIN, User.UserRole.STAFF)) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                    "Only administrators and staff can view all bookings");
                return;
            }
            
            List<Booking> bookings = bookingService.getAllBookings();
            sendSuccess(response, bookings, "Bookings retrieved successfully");
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Get current user's bookings
     */
    private void handleGetMyBookings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);
            
            List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getId());
            sendSuccess(response, bookings, "Your bookings retrieved successfully");
            
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Get booking by ID
     */
    private void handleGetById(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            Booking booking = bookingService.getBookingById(id);
            
            // Check if user has permission to view this booking
            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);
            
            if (!hasRole(session, User.UserRole.ADMIN, User.UserRole.STAFF) && 
                !booking.getUserId().equals(currentUser.getId())) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, 
                    "You can only view your own bookings");
                return;
            }
            
            sendSuccess(response, booking, "Booking retrieved successfully");
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
    
    /**
     * Create new booking
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);
            
            Long flightId = Long.parseLong(request.getParameter("flightId"));
            Integer seatsBooked = Integer.parseInt(request.getParameter("seatsBooked"));
            
            Booking booking = bookingService.createBooking(currentUser.getId(), 
                flightId, seatsBooked);
            
            sendSuccess(response, booking, "Booking created successfully");
            logger.info("Booking created: userId={}, flightId={}", currentUser.getId(), flightId);
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Cancel booking
     */
    private void handleCancel(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);
            
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring("/cancel/".length()));
            
            boolean cancelled = bookingService.cancelBooking(id, currentUser.getId());
            
            if (cancelled) {
                sendSuccess(response, null, "Booking cancelled successfully");
                logger.info("Booking cancelled: bookingId={}, userId={}", id, currentUser.getId());
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to cancel booking");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * Delete booking (Admin only)
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            Long id = Long.parseLong(pathInfo.substring(1));
            
            boolean deleted = bookingService.deleteBooking(id);
            
            if (deleted) {
                sendSuccess(response, null, "Booking deleted successfully");
                logger.info("Booking deleted: {}", id);
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to delete booking");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (ServiceException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
