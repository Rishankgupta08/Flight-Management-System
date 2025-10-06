package com.example.airportmanagement.model;

import java.time.LocalDateTime;

/**
 * Flight Entity Model
 */
public class Flight {
    private Long id;
    private String flightNumber;
    private Long sourceAirportId;
    private Long destinationAirportId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer seatsAvailable;
    private Double price;
    private FlightStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined queries
    private String sourceAirportCode;
    private String sourceAirportName;
    private String destinationAirportCode;
    private String destinationAirportName;
    
    public enum FlightStatus {
        SCHEDULED, BOARDING, DEPARTED, ARRIVED, CANCELLED, DELAYED
    }
    
    // Constructors
    public Flight() {}
    
    public Flight(Long id, String flightNumber, Long sourceAirportId, 
                  Long destinationAirportId, LocalDateTime departureTime, 
                  LocalDateTime arrivalTime, Integer seatsAvailable, Double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.sourceAirportId = sourceAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsAvailable = seatsAvailable;
        this.price = price;
        this.status = FlightStatus.SCHEDULED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public Long getSourceAirportId() {
        return sourceAirportId;
    }
    
    public void setSourceAirportId(Long sourceAirportId) {
        this.sourceAirportId = sourceAirportId;
    }
    
    public Long getDestinationAirportId() {
        return destinationAirportId;
    }
    
    public void setDestinationAirportId(Long destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }
    
    public void setSeatsAvailable(Integer seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public FlightStatus getStatus() {
        return status;
    }
    
    public void setStatus(FlightStatus status) {
        this.status = status;
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
    
    public String getSourceAirportCode() {
        return sourceAirportCode;
    }
    
    public void setSourceAirportCode(String sourceAirportCode) {
        this.sourceAirportCode = sourceAirportCode;
    }
    
    public String getSourceAirportName() {
        return sourceAirportName;
    }
    
    public void setSourceAirportName(String sourceAirportName) {
        this.sourceAirportName = sourceAirportName;
    }
    
    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }
    
    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }
    
    public String getDestinationAirportName() {
        return destinationAirportName;
    }
    
    public void setDestinationAirportName(String destinationAirportName) {
        this.destinationAirportName = destinationAirportName;
    }
    
    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", sourceAirportId=" + sourceAirportId +
                ", destinationAirportId=" + destinationAirportId +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", seatsAvailable=" + seatsAvailable +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
