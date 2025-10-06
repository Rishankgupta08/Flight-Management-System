# Airport Management System (AMS)

A full-stack web application for managing airport operations, flights, and bookings using Java Maven backend, MySQL database, and HTML/CSS/JavaScript frontend.

## 🚀 Features

### User Management
- User registration with role-based access (Admin, Staff, Passenger)
- Secure login/logout with session management
- Password hashing using SHA-256 with salt
- Role-based authorization

### Airport Management (Admin)
- Add, edit, and delete airports
- Search airports by name, code, city, or country
- View all registered airports
- IATA code validation

### Flight Management (Admin/Staff)
- Schedule new flights
- Update flight information
- Cancel flights
- Search flights by source, destination, and date
- Real-time seat availability tracking
- Flight status management

### Booking Management (Passenger)
- Search and book available flights
- View booking history
- Cancel bookings
- Automatic seat availability updates
- Price calculation

### Technical Features
- Layered architecture (Controller → Service → DAO → Database)
- RESTful API with JSON responses
- Connection pooling for database optimization
- Input validation and exception handling
- SLF4J + Logback logging
- Responsive UI design
- AJAX-based dynamic updates

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Apache Tomcat 10.x or Jetty 11.x (for deployment)

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Airport-Management-System
```

### 2. Database Setup

Create the MySQL database and tables:

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE airport_management_system;

# Use the database
USE airport_management_system;

# Run schema script
source sql/schema.sql

# Load sample data
source sql/sample_data.sql
```

### 3. Configure Database Connection

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/airport_management_system
db.username=root
db.password=your_password
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

**Option 1: Using Jetty Maven Plugin**

```bash
mvn jetty:run
```

The application will be available at: `http://localhost:8080/AirportManagementSystem`

**Option 2: Deploy to Tomcat**

1. Build the WAR file: `mvn package`
2. Copy `target/AirportManagementSystem.war` to Tomcat's `webapps` directory
3. Start Tomcat
4. Access: `http://localhost:8080/AirportManagementSystem`

## 📁 Project Structure

```
Airport-Management-System/
│
├── pom.xml                                 # Maven configuration
├── README.md                               # Project documentation
├── sql/                                    # Database scripts
│   ├── schema.sql                         # Database schema
│   └── sample_data.sql                    # Sample data
│
├── src/
│   ├── main/
│   │   ├── java/com/example/airportmanagement/
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   └── AppConfig.java
│   │   │   ├── db/                        # Database connection pool
│   │   │   │   └── DatabaseConnection.java
│   │   │   ├── model/                     # POJOs
│   │   │   │   ├── User.java
│   │   │   │   ├── Airport.java
│   │   │   │   ├── Flight.java
│   │   │   │   └── Booking.java
│   │   │   ├── dao/                       # Data Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── AirportDAO.java
│   │   │   │   ├── FlightDAO.java
│   │   │   │   └── BookingDAO.java
│   │   │   ├── service/                   # Business logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── AirportService.java
│   │   │   │   ├── FlightService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   └── ServiceException.java
│   │   │   ├── controller/                # Servlets
│   │   │   │   ├── BaseServlet.java
│   │   │   │   ├── AuthServlet.java
│   │   │   │   ├── AirportServlet.java
│   │   │   │   ├── FlightServlet.java
│   │   │   │   └── BookingServlet.java
│   │   │   └── util/                      # Utilities
│   │   │       ├── PasswordUtil.java
│   │   │       ├── ValidationUtil.java
│   │   │       └── DateUtil.java
│   │   │
│   │   ├── resources/
│   │   │   ├── db.properties
│   │   │   ├── messages.properties
│   │   │   └── logback.xml
│   │   │
│   │   └── webapp/
│   │       ├── index.html                 # Home page
│   │       ├── login.html                 # Login/Register
│   │       ├── dashboard.html             # User dashboard
│   │       ├── flights.html               # Flight search & management
│   │       ├── airports.html              # Airport management
│   │       ├── bookings.html              # Booking management
│   │       └── assets/
│   │           ├── css/
│   │           │   └── style.css          # Styles
│   │           ├── js/
│   │           │   ├── main.js            # Common utilities
│   │           │   ├── flights.js         # Flights page
│   │           │   └── airports.js        # Airports page
│   │           └── images/
│   │
│   └── test/
│       └── java/com/example/airportmanagement/
│           ├── dao/
│           │   └── UserDAOTest.java
│           └── service/
│               └── UserServiceTest.java
```

## 🔌 API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `GET /auth/current` - Get current user

### Airports
- `GET /airport/list` - Get all airports
- `GET /airport/{id}` - Get airport by ID
- `GET /airport/search?q={keyword}` - Search airports
- `POST /airport/create` - Create airport (Admin only)
- `PUT /airport/update` - Update airport (Admin only)
- `DELETE /airport/{id}` - Delete airport (Admin only)

### Flights
- `GET /flight/list` - Get all flights
- `GET /flight/{id}` - Get flight by ID
- `GET /flight/search?from={code}&to={code}&date={date}` - Search flights
- `POST /flight/create` - Schedule flight (Admin/Staff)
- `PUT /flight/update` - Update flight (Admin/Staff)
- `PUT /flight/cancel/{id}` - Cancel flight (Admin/Staff)
- `DELETE /flight/{id}` - Delete flight (Admin only)

### Bookings
- `GET /booking/my-bookings` - Get user's bookings
- `GET /booking/list` - Get all bookings (Admin/Staff)
- `GET /booking/{id}` - Get booking by ID
- `POST /booking/create` - Create booking
- `PUT /booking/cancel/{id}` - Cancel booking
- `DELETE /booking/{id}` - Delete booking (Admin only)

## 👥 Demo Credentials

### Admin
- **Username:** admin
- **Password:** Password123

### Staff
- **Username:** staff1
- **Password:** Password123

### Passenger
- **Username:** john_doe
- **Password:** Password123

## 🧪 Running Tests

```bash
mvn test
```

## 📊 Database Schema

### Users Table
- id (BIGINT, PK, AUTO_INCREMENT)
- username (VARCHAR, UNIQUE)
- password_hash (VARCHAR)
- email (VARCHAR, UNIQUE)
- role (ENUM: ADMIN, STAFF, PASSENGER)
- created_at, updated_at (TIMESTAMP)

### Airports Table
- id (BIGINT, PK, AUTO_INCREMENT)
- name (VARCHAR)
- code (CHAR(3), UNIQUE)
- city (VARCHAR)
- country (VARCHAR)
- created_at, updated_at (TIMESTAMP)

### Flights Table
- id (BIGINT, PK, AUTO_INCREMENT)
- flight_number (VARCHAR)
- source_airport_id (BIGINT, FK)
- destination_airport_id (BIGINT, FK)
- departure_time, arrival_time (DATETIME)
- seats_available (INT)
- price (DECIMAL)
- status (ENUM)
- created_at, updated_at (TIMESTAMP)

### Bookings Table
- id (BIGINT, PK, AUTO_INCREMENT)
- user_id (BIGINT, FK)
- flight_id (BIGINT, FK)
- seats_booked (INT)
- total_price (DECIMAL)
- status (ENUM: CONFIRMED, CANCELLED, COMPLETED)
- booking_date, created_at, updated_at (TIMESTAMP)

## 🎨 Frontend Technologies

- **HTML5** - Semantic markup
- **CSS3** - Modern responsive design
- **JavaScript (ES6+)** - Dynamic interactions
- **Fetch API** - AJAX requests
- **Session Storage** - Client-side session management

## 🔒 Security Features

- Password hashing with SHA-256 and salt
- Session-based authentication
- Role-based access control (RBAC)
- Input validation on both client and server
- SQL injection prevention using PreparedStatements
- XSS protection

## 📝 Best Practices Implemented

1. **Layered Architecture** - Clear separation of concerns
2. **Dependency Injection** - Loose coupling
3. **Exception Handling** - Comprehensive error management
4. **Logging** - SLF4J with Logback
5. **Code Documentation** - Javadoc comments
6. **Design Patterns** - DAO, Service, Factory patterns
7. **RESTful API Design** - Standard HTTP methods and status codes
8. **Responsive Design** - Mobile-friendly UI

## 🚧 Future Enhancements

- [ ] Email notifications for bookings
- [ ] Payment gateway integration
- [ ] Flight recommendations
- [ ] Advanced search filters
- [ ] Reporting and analytics dashboard
- [ ] Mobile application
- [ ] Integration with external flight APIs
- [ ] Multi-language support
- [ ] OAuth2 authentication

## 🐛 Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `db.properties`
- Ensure database exists and schema is loaded

### Port Already in Use
- Change port in `pom.xml` (Jetty plugin configuration)
- Kill process using the port: `lsof -ti:8080 | xargs kill -9`

### Build Failures
- Clean Maven cache: `mvn clean`
- Update dependencies: `mvn clean install -U`
- Check Java version: `java -version`

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Airport Management System - Full Stack Web Application

## 🙏 Acknowledgments

- Maven for dependency management
- MySQL for database
- Jetty for embedded server
- Jackson for JSON processing
- SLF4J & Logback for logging

---

**Note:** This is a demonstration project for educational purposes. For production use, implement additional security measures, proper error handling, and comprehensive testing.
