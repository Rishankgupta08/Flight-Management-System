package com.example.airportmanagement.model;

import java.time.LocalDateTime;

/**
 * Booking Entity Model
 */
public class Booking {
    private Long id;
    private Long userId;
    private Long flightId;
    private Integer seatsBooked;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined queries
    private String username;
    private String flightNumber;
    private String sourceAirportCode;
    private String destinationAirportCode;
    private LocalDateTime departureTime;
    
    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
    
    // Constructors
    public Booking() {}
    
    public Booking(Long id, Long userId, Long flightId, Integer seatsBooked, 
                   Double totalPrice, BookingStatus status) {
        this.id = id;
        this.userId = userId;
        this.flightId = flightId;
        this.seatsBooked = seatsBooked;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bookingDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public Integer getSeatsBooked() {
        return seatsBooked;
    }
    
    public void setSeatsBooked(Integer seatsBooked) {
        this.seatsBooked = seatsBooked;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getSourceAirportCode() {
        return sourceAirportCode;
    }
    
    public void setSourceAirportCode(String sourceAirportCode) {
        this.sourceAirportCode = sourceAirportCode;
    }
    
    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }
    
    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", flightId=" + flightId +
                ", seatsBooked=" + seatsBooked +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
