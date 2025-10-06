# Quick Setup Guide - Airport Management System

## Prerequisites Check

Before starting, ensure you have:

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] MySQL 8.0+ installed and running
- [ ] Web browser (Chrome, Firefox, Safari, etc.)

## Step-by-Step Setup

### 1. Database Setup (5 minutes)

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE airport_management_system;

# Exit MySQL
exit;

# Load schema
mysql -u root -p airport_management_system < sql/schema.sql

# Load sample data
mysql -u root -p airport_management_system < sql/sample_data.sql
```

### 2. Configure Database Connection (2 minutes)

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/airport_management_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver
```

### 3. Build & Run (3 minutes)

```bash
# Clean and build the project
mvn clean install

# Run with Jetty (easiest option)
mvn jetty:run
```

**Alternative: Deploy to Tomcat**

```bash
# Build WAR file
mvn package

# Copy to Tomcat
cp target/AirportManagementSystem.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh
```

### 4. Access the Application

Open your browser and navigate to:

```
http://localhost:8080/AirportManagementSystem
```

### 5. Login with Demo Accounts

**Admin Account:**
- Username: `admin`
- Password: `Password123`
- Can manage users, airports, flights, and bookings

**Staff Account:**
- Username: `staff1`
- Password: `Password123`
- Can manage flights and view bookings

**Passenger Account:**
- Username: `john_doe`
- Password: `Password123`
- Can search flights and make bookings

## Common Issues & Solutions

### Issue 1: Database Connection Failed

**Solution:**
- Verify MySQL is running: `sudo systemctl status mysql`
- Check credentials in `db.properties`
- Test connection: `mysql -u root -p airport_management_system`

### Issue 2: Port 8080 Already in Use

**Solution:**
- Find process: `lsof -i :8080`
- Kill process: `kill -9 <PID>`
- Or change port in `pom.xml` (Jetty plugin configuration)

### Issue 3: Maven Build Fails

**Solution:**
- Clean cache: `mvn clean`
- Update dependencies: `mvn clean install -U`
- Check Java version: `java -version` (should be 17+)

### Issue 4: 404 Errors on API Calls

**Solution:**
- Verify application is running
- Check context path: `/AirportManagementSystem`
- Check servlet mappings in servlets

## Verification Checklist

After setup, verify:

- [ ] Home page loads successfully
- [ ] Can login with demo credentials
- [ ] Can view list of airports
- [ ] Can search for flights
- [ ] Can make a booking (as passenger)
- [ ] Can manage airports (as admin)
- [ ] Can schedule flights (as admin/staff)

## Testing the Application

### Test User Registration
1. Go to Login page
2. Click "Register" tab
3. Fill in details:
   - Username: `testuser`
   - Email: `test@example.com`
   - Password: `TestPass123`
   - Role: `Passenger`
4. Click "Register"
5. Login with new credentials

### Test Flight Booking
1. Login as passenger
2. Go to "Flights" page
3. Search for flights (e.g., from JFK to LAX)
4. Click "Book Now" on any flight
5. Enter number of seats
6. Confirm booking
7. Go to "My Bookings" to see confirmed booking

### Test Flight Management (Admin/Staff)
1. Login as admin or staff
2. Go to "Flights" page
3. Click "+ Add New Flight"
4. Fill in flight details
5. Save flight
6. Verify new flight appears in list

## Database Verification

Check if data was loaded correctly:

```sql
-- Check airports
SELECT COUNT(*) FROM airports; -- Should return 15

-- Check flights
SELECT COUNT(*) FROM flights; -- Should return ~20

-- Check users
SELECT username, role FROM users;

-- Check sample bookings
SELECT * FROM bookings;
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest
```

## Stopping the Application

**If using Jetty:**
- Press `Ctrl + C` in the terminal

**If using Tomcat:**
```bash
$TOMCAT_HOME/bin/shutdown.sh
```

## Next Steps

After successful setup:

1. Explore all features as different user roles
2. Try creating new flights and airports
3. Test the booking flow end-to-end
4. Review the code structure in your IDE
5. Check logs in `logs/airport-management-system.log`

## Need Help?

- Check `README.md` for detailed documentation
- Review API endpoints documentation
- Check application logs for errors
- Verify database connections

---

**Setup Time:** ~10-15 minutes  
**Difficulty:** Beginner-Intermediate  
**Support:** See README.md for troubleshooting
