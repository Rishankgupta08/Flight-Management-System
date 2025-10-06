-- Airport Management System Sample Data

-- Insert sample users (passwords are hashed - original password for all is "Password123")
-- Note: In production, use the actual password hashing from the application
INSERT INTO users (username, password_hash, email, role) VALUES
('admin', 'yQ8K2vxJZkQwMjA=:2XfJqKvXZqwWJfXmLqBYvXzJqKmZqXwYJfBvLqXzJqKmZqXwYJfBvLq=', 'admin@airport.com', 'ADMIN'),
('staff1', 'xR7L3wvKYlRxNjB=:3YgKrLwYarxXKgYnMrCZwYaLrLnaqYxZKgCwMrYaLrLnaqYxZKgCwMr=', 'staff1@airport.com', 'STAFF'),
('staff2', 'zS9M5yvLZmSyOkC=:5ZhLsMyZbsyYLhZoNsDaxZbMsModbrZaLhDxNsZbMsModbrZaLhDxNs=', 'staff2@airport.com', 'STAFF'),
('john_doe', 'aT1N6zwMamTzPlD=:6aiMtNzactaNMiatPtEbyacNtNpecsabMiEyPtacNtNpecsabMiEyPt=', 'john@example.com', 'PASSENGER'),
('jane_smith', 'bU2O7axNbnUaQmE=:7bjNuOadbduaOjbuQuFczbdOuOqfdtbcNjFzQudbOuOqfdtbcNjFzQu=', 'jane@example.com', 'PASSENGER');

-- Insert sample airports
INSERT INTO airports (name, code, city, country) VALUES
('John F. Kennedy International Airport', 'JFK', 'New York', 'United States'),
('Los Angeles International Airport', 'LAX', 'Los Angeles', 'United States'),
('Heathrow Airport', 'LHR', 'London', 'United Kingdom'),
('Dubai International Airport', 'DXB', 'Dubai', 'United Arab Emirates'),
('Tokyo Haneda Airport', 'HND', 'Tokyo', 'Japan'),
('Singapore Changi Airport', 'SIN', 'Singapore', 'Singapore'),
('Hong Kong International Airport', 'HKG', 'Hong Kong', 'Hong Kong'),
('Paris Charles de Gaulle Airport', 'CDG', 'Paris', 'France'),
('Frankfurt Airport', 'FRA', 'Frankfurt', 'Germany'),
('Amsterdam Airport Schiphol', 'AMS', 'Amsterdam', 'Netherlands'),
('Chicago O\'Hare International Airport', 'ORD', 'Chicago', 'United States'),
('San Francisco International Airport', 'SFO', 'San Francisco', 'United States'),
('Sydney Kingsford Smith Airport', 'SYD', 'Sydney', 'Australia'),
('Toronto Pearson International Airport', 'YYZ', 'Toronto', 'Canada'),
('Mumbai Chhatrapati Shivaji Airport', 'BOM', 'Mumbai', 'India');

-- Insert sample flights
INSERT INTO flights (flight_number, source_airport_id, destination_airport_id, departure_time, arrival_time, seats_available, price, status) VALUES
-- JFK flights
('AA101', 1, 2, '2025-10-15 08:00:00', '2025-10-15 11:30:00', 150, 350.00, 'SCHEDULED'),
('AA102', 1, 3, '2025-10-15 18:00:00', '2025-10-16 06:00:00', 200, 850.00, 'SCHEDULED'),
('AA103', 1, 11, '2025-10-16 09:00:00', '2025-10-16 11:00:00', 180, 280.00, 'SCHEDULED'),

-- LAX flights
('UA201', 2, 1, '2025-10-15 10:00:00', '2025-10-15 18:30:00', 160, 380.00, 'SCHEDULED'),
('UA202', 2, 12, '2025-10-15 14:00:00', '2025-10-15 15:30:00', 140, 220.00, 'SCHEDULED'),
('UA203', 2, 5, '2025-10-16 20:00:00', '2025-10-18 05:00:00', 250, 1200.00, 'SCHEDULED'),

-- LHR flights
('BA301', 3, 1, '2025-10-15 10:00:00', '2025-10-15 13:00:00', 220, 900.00, 'SCHEDULED'),
('BA302', 3, 8, '2025-10-15 12:00:00', '2025-10-15 13:30:00', 180, 150.00, 'SCHEDULED'),
('BA303', 3, 4, '2025-10-16 16:00:00', '2025-10-17 01:00:00', 200, 450.00, 'SCHEDULED'),

-- Dubai flights
('EK401', 4, 6, '2025-10-15 03:00:00', '2025-10-15 10:00:00', 300, 650.00, 'SCHEDULED'),
('EK402', 4, 3, '2025-10-15 08:00:00', '2025-10-15 12:30:00', 280, 480.00, 'SCHEDULED'),
('EK403', 4, 15, '2025-10-16 22:00:00', '2025-10-17 03:00:00', 250, 380.00, 'SCHEDULED'),

-- Tokyo flights
('NH501', 5, 6, '2025-10-15 11:00:00', '2025-10-15 17:00:00', 200, 580.00, 'SCHEDULED'),
('NH502', 5, 7, '2025-10-15 15:00:00', '2025-10-15 19:00:00', 180, 320.00, 'SCHEDULED'),
('NH503', 5, 13, '2025-10-16 10:00:00', '2025-10-16 19:00:00', 220, 950.00, 'SCHEDULED'),

-- Singapore flights
('SQ601', 6, 3, '2025-10-15 23:00:00', '2025-10-16 06:00:00', 280, 780.00, 'SCHEDULED'),
('SQ602', 6, 7, '2025-10-15 08:00:00', '2025-10-15 12:00:00', 200, 280.00, 'SCHEDULED'),
('SQ603', 6, 13, '2025-10-16 14:00:00', '2025-10-16 22:00:00', 240, 680.00, 'SCHEDULED'),

-- Additional flights for variety
('DL701', 11, 2, '2025-10-15 07:00:00', '2025-10-15 10:00:00', 170, 320.00, 'SCHEDULED'),
('AF801', 8, 1, '2025-10-15 11:00:00', '2025-10-15 14:00:00', 190, 820.00, 'SCHEDULED'),
('LH901', 9, 5, '2025-10-16 18:00:00', '2025-10-17 14:00:00', 210, 1100.00, 'SCHEDULED'),
('KL101', 10, 14, '2025-10-15 13:00:00', '2025-10-15 21:00:00', 230, 720.00, 'SCHEDULED');

-- Insert sample bookings
INSERT INTO bookings (user_id, flight_id, seats_booked, total_price, status) VALUES
(4, 1, 2, 700.00, 'CONFIRMED'),
(4, 7, 1, 900.00, 'CONFIRMED'),
(5, 2, 3, 2550.00, 'CONFIRMED'),
(5, 10, 2, 1300.00, 'CONFIRMED'),
(4, 15, 1, 280.00, 'CANCELLED');
