# Flight Booking Management System 

A comprehensive Spring Boot REST API application for managing flight bookings, enabling users to search flights, book tickets, manage itineraries, and handle cancellations.

## Overview

This application provides a complete flight booking system with features for both users and administrators. It supports one-way and round-trip bookings, passenger management, seat allocation, and booking history tracking.

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL
- **ORM**: Spring Data JPA (Hibernate)
- **Validation**: Jakarta Bean Validation
- **Additional Libraries**:
  - Lombok (code generation)
  - Spring Boot DevTools (development)
  - JUnit 5 (testing)

## Features

### User Features
- Search flights by route and date
- Book one-way or round-trip tickets
- Manage passenger details (name, age, gender, meal preferences, seat selection)
- View booking details by PNR
- View booking history by email
- Cancel bookings (with 24-hour advance requirement)

### Admin Features
- Add airline inventory
- Add multiple flights in bulk
- Manage flight schedules and availability

## Project Structure

```
com.flightapp/
├── controller/          # REST API endpoints
│   ├── AdminController
│   └── FlightController
├── service/            # Business logic layer
│   ├── AdminService
│   ├── BookingService
│   ├── FlightService
│   └── implimentation/
├── repository/         # Data access layer
│   ├── AirlineRepository
│   ├── BookingRepository
│   ├── FlightRepository
│   ├── ItineraryRepository
│   ├── PassengerRepository
│   └── UserRepository
├── entity/            # JPA entities
│   ├── Airline
│   ├── Booking
│   ├── Flight
│   ├── Itinerary
│   ├── Passenger
│   └── User
├── dto/              # Data transfer objects
├── exception/        # Custom exceptions and error handling
└── Application.java  # Main application class
```

## Domain Model

### Core Entities

**User**
- User profile with name, email, and role (USER/ADMIN)
- One-to-many relationship with itineraries

**Airline**
- Airline information (name, code)
- One-to-many relationship with flights

**Flight**
- Flight details (route, timing, price, capacity)
- Linked to an airline
- Tracks available and total seats
- Status (SCHEDULED/CANCELLED)

**Itinerary**
- Master booking record with unique PNR
- Contains booking metadata (user, creation time, total amount)
- Status tracking (BOOKED/CANCELLED/COMPLETED)
- One-to-many relationship with bookings

**Booking**
- Individual flight booking leg
- Links itinerary to a specific flight
- Journey date and segment type (ONE_WAY/OUTBOUND/RETURN)
- One-to-many relationship with passengers

**Passenger**
- Passenger details (name, age, gender)
- Seat assignment and meal preference (VEG/NON_VEG)
- Linked to a specific booking

## API Endpoints

### 1. Add Airline Inventory/Schedule

**Endpoint**: `POST /api/v1.0/flight/airline/inventory/add`

**Description**: Add inventory or schedule for an existing airline. Multiple flights can be added in a single request.

**Request Body**:
```json
{
  "airlineCode": "AI",
  "flights": [
    {
      "fromAirport": "BOM",
      "toAirport": "DEL",
      "departureTime": "25/12/25 10:00 AM",
      "arrivalTime": "25/12/25 12:30 PM",
      "price": 5000,
      "totalSeats": 180,
      "availabeSeats": 180
    }
  ]
}
```

**Response**:
```json
{
  "airLineCode": "AI",
  "flightsAdded": 1,
  "flightIds": [1]
}
```

**Validations**:
- `airlineCode` - Must not be blank (required field)
- `flights` - Must not be empty (at least one flight required)
- Each flight must have:
  - `fromAirport` - Must not be blank
  - `toAirport` - Must not be blank
  - `departureTime` - Must not be null, valid datetime format
  - `arrivalTime` - Must not be null, valid datetime format, **must be after departure time**
  - `price` - Must be positive (> 0)
  - `totalSeats` - Must be positive (> 0)
  - `availabeSeats` - Must be >= 0

**Error Responses**:
- `400 Bad Request` - Invalid input, missing required fields, or validation errors
- `404 Not Found` - Airline not found with provided code
- `500 Internal Server Error` - Server-side error

---

### 2. Search Flights

**Endpoint**: `POST /api/v1.0/flight/search`

**Description**: Search for available flights based on route and journey date.

**Request Body**:
```json
{
  "from": "BOM",
  "to": "DEL",
  "journeyDate": "25/12/25",
  "tripType": "ONE_WAY"
}
```

**Response**:
```json
[
  {
    "flightId": 1,
    "airlineName": "Air India",
    "airlineCode": "AI",
    "fromAirport": "BOM",
    "toAirport": "DEL",
    "departureTime": "2025-12-25T10:00:00",
    "arrivalTime": "2025-12-25T12:30:00",
    "price": 5000
  }
]
```

**Validations**:
- `from` - Must not be blank (3-letter airport code)
- `to` - Must not be blank (3-letter airport code)
- `journeyDate` - Must not be null, valid date format
- `tripType` - Must not be null, valid enum value (ONE_WAY or ROUND_TRIP)

**Business Logic**:
- Only returns flights with status "SCHEDULED"
- Searches flights for the entire day
- Does not return flights with no available seats

**Error Responses**:
- `400 Bad Request` - Invalid input or validation errors
- `500 Internal Server Error` - Server-side error

---

### 3. Book Flight Ticket

**Endpoint**: `POST /api/v1.0/flight/booking/{flightId}`

**Description**: Book a flight ticket for one or more passengers. Supports both one-way and round-trip bookings.

**Path Parameters**:
- `flightId` - ID of the outward flight

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "tripType": "ROUND_TRIP",
  "returnFlightId": 2,
  "numberOfSeats": 2,
  "passengers": [
    {
      "name": "John Doe",
      "gender": "MALE",
      "age": 30,
      "mealType": "NON_VEG",
      "seatNumber": "12A"
    },
    {
      "name": "Jane Doe",
      "gender": "FEMALE",
      "age": 28,
      "mealType": "VEG",
      "seatNumber": "12B"
    }
  ]
}
```

**Response**:
```json
{
  "pnr": "TADAB123",
  "userName": "John Doe",
  "email": "john@example.com",
  "status": "BOOKED",
  "totalAmount": 20000,
  "createdTime": "2025-12-20T14:30:00",
  "legs": [
    {
      "bookingId": 1,
      "flightId": 1,
      "fromAirport": "BOM",
      "toAirport": "DEL",
      "departureTime": "2025-12-25T10:00:00",
      "arrivalTime": "2025-12-25T12:30:00",
      "segmentType": "OUTBOUND",
      "status": "BOOKED",
      "passengers": [
        {
          "name": "John Doe",
          "gender": "MALE",
          "age": 30,
          "mealType": "NON_VEG",
          "seatNumber": "12A"
        }
      ]
    }
  ]
}
```

**Validations**:
- `name` - Must not be blank, required
- `email` - Must not be blank and valid email format (e.g., user@domain.com)
- `tripType` - Must not be null (ONE_WAY or ROUND_TRIP)
- `returnFlightId` - Required if tripType is ROUND_TRIP, optional for ONE_WAY
- `numberOfSeats` - Must be positive (> 0)
- `passengers` - Must not be empty, list required
- Each passenger must have:
  - `name` - Must not be blank
  - `gender` - Must not be null (MALE, FEMALE, OTHER)
  - `age` - Must be between 0 and 100 (inclusive)
  - `mealType` - Must not be null (VEG or NON_VEG)
  - `seatNumber` - Must not be blank (e.g., "12A")

**Business Logic Validations**:
- **Number of passengers must equal numberOfSeats** - Throws error if mismatch
- If tripType is ROUND_TRIP, returnFlightId is mandatory
- Both outward and return flights must exist in the system
- Users are automatically created if they don't exist with the provided email
- Each booking generates a unique PNR (TAD + 5 random characters)
- Total amount calculated as: (outwardFlightPrice × numberOfSeats) + (returnFlightPrice × numberOfSeats)
- Seats are decremented from flight inventory upon successful booking
- At least one passenger is required

**Error Responses**:
- `400 Bad Request` - Validation errors or business logic violations
- `404 Not Found` - Flight not found
- `409 Conflict` - Insufficient seats available
- `500 Internal Server Error` - Server-side error

---

### 4. Get Booking Details by PNR

**Endpoint**: `GET /api/v1.0/flight/ticket/{pnr}`

**Description**: Retrieve complete booking details using the PNR (Passenger Name Record).

**Path Parameters**:
- `pnr` - Booking PNR (e.g., "TADAB123")

**Response**:
```json
{
  "pnr": "TADAB123",
  "userName": "John Doe",
  "email": "john@example.com",
  "status": "BOOKED",
  "totalAmount": 20000,
  "createdTime": "2025-12-20T14:30:00",
  "legs": [
    {
      "bookingId": 1,
      "flightId": 1,
      "fromAirport": "BOM",
      "toAirport": "DEL",
      "departureTime": "2025-12-25T10:00:00",
      "arrivalTime": "2025-12-25T12:30:00",
      "segmentType": "OUTBOUND",
      "status": "BOOKED",
      "passengers": [
        {
          "name": "John Doe",
          "gender": "MALE",
          "age": 30,
          "mealType": "NON_VEG",
          "seatNumber": "12A"
        }
      ]
    }
  ]
}
```

**Error Responses**:
- `404 Not Found` - No booking found for the provided PNR
- `500 Internal Server Error` - Server-side error

---

### 5. Get Booking History

**Endpoint**: `GET /api/v1.0/flight/booking/history/{emailId}`

**Description**: Retrieve all bookings associated with an email address.

**Path Parameters**:
- `emailId` - Email address of the user

**Response**:
```json
[
  {
    "pnr": "TADAB123",
    "userName": "John Doe",
    "email": "john@example.com",
    "status": "BOOKED",
    "totalAmount": 20000,
    "createdTime": "2025-12-20T14:30:00",
    "legs": [...]
  },
  {
    "pnr": "TADCD456",
    "userName": "John Doe",
    "email": "john@example.com",
    "status": "CANCELLED",
    "totalAmount": 10000,
    "createdTime": "2025-12-15T10:00:00",
    "legs": [...]
  }
]
```

**Business Logic**:
- Returns all bookings (BOOKED, CANCELLED, COMPLETED) for the email
- Returns empty list if no bookings found

**Error Responses**:
- `500 Internal Server Error` - Server-side error

---

### 6. Cancel Booking

**Endpoint**: `DELETE /api/v1.0/flight/booking/cancel/{pnr}`

**Description**: Cancel an existing booking. Must be cancelled at least 24 hours before flight departure.

**Path Parameters**:
- `pnr` - Booking PNR to cancel

**Response**:
```json
{
  "pnr": "TADAB123",
  "status": "CANCELLED",
  "message": "Booking Cancelled Successfully!!!"
}
```

**Cancellation Rules**:
- Booking can only be cancelled if flight departure is more than 24 hours away
- When cancelled, seats are released back to the flight inventory
- For round-trip bookings, all legs (outbound and return) are cancelled
- Already cancelled bookings remain unchanged
- Current time must be at least 24 hours before departure time to allow cancellation

**Error Responses**:
- `400 Bad Request` - Cancellation not allowed (within 24 hours of departure)
- `404 Not Found` - Booking not found for the provided PNR
- `409 Conflict` - Cancellation policy violated
- `500 Internal Server Error` - Server-side error

## Configuration

### Database Configuration

Update `src/main/resources/application.properties`:

```properties
# Application Configuration
spring.application.name=com.flightapp
server.port=9000

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/flightapp_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Logging Configuration
logging.level.root=INFO
logging.level.com.flightapp=DEBUG
logging.level.org.springframework.boot=WARN
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE flightapp_db;
```

2. Update database credentials in `application.properties`

### Running the Application

**Using Maven Wrapper (Unix/Linux/Mac)**
```bash
./mvnw spring-boot:run
```

**Using Maven Wrapper (Windows)**
```bash
mvnw.cmd spring-boot:run
```

**Using Maven directly**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:9000`

### Building the Application

```bash
./mvnw clean package
```

The JAR file will be created in the `target/` directory.

### Running Tests

```bash
./mvnw test
```

## Business Rules

### Booking Rules
1. Number of passengers must match number of seats requested
2. All passenger details are mandatory (name, age, gender, meal preference, seat number)
3. Users are automatically created if they don't exist
4. Each booking generates a unique PNR (format: TAD + 5 random characters)

### Cancellation Rules
1. Bookings can only be cancelled if flight departure is more than 24 hours away
2. Cancelled bookings release seats back to flight inventory
3. Entire itinerary is cancelled (all legs if round-trip)

### Flight Availability
1. Seats are decremented upon successful booking
2. Seats are released back when booking is cancelled
3. Flights must be in SCHEDULED status to be bookable

## Error Handling

The application includes comprehensive error handling:

- **400 Bad Request**: Validation errors, invalid requests
- **404 Not Found**: Resource not found (flight, itinerary, user)
- **409 Conflict**: Seat availability issues
- **500 Internal Server Error**: Unexpected server errors

Custom exceptions:
- `ResourceNotFoundException`: When requested resource doesn't exist
- `SeatNotAvailableException`: When requested seats are unavailable
- `CancellationNotAllowedException`: When cancellation policy is violated

## Enums

### TripType (DTO)
- `ONE_WAY`: Single direction travel
- `ROUND_TRIP`: Return journey included

### TripSegmentType (Entity)
- `ONE_WAY`: Single segment journey
- `OUTBOUND`: First leg of round trip
- `RETURN`: Return leg of round trip

### BookingStatus
- `BOOKED`: Active booking
- `CANCELLED`: Cancelled booking
- `COMPLETED`: Journey completed

### FlightStatus
- `SCHEDULED`: Flight is active and bookable
- `CANCELLED`: Flight cancelled

### Role
- `USER`: Regular user
- `ADMIN`: Administrator with inventory access

### Gender
- `MALE`, `FEMALE`, `OTHER`

### MealType
- `VEG`: Vegetarian meal
- `NON_VEG`: Non-vegetarian meal

## Validations Implemented

The application implements comprehensive input and business logic validations across all endpoints:

### Request-Level Validations (Jakarta Bean Validation)

#### Global Validations
- **@NotBlank**: Ensures string fields are not null or empty
- **@NotNull**: Ensures required fields are present
- **@NotEmpty**: Ensures collections are not empty
- **@Email**: Validates email format for email fields
- **@Positive**: Ensures numeric values are greater than zero
- **@Range**: Ensures numeric values are within specified range
- **@Valid**: Enables cascading validation for nested objects

#### Endpoint-Specific Validations

**Add Airline Inventory (`POST /api/v1.0/flight/airline/inventory/add`)**:
- `airlineCode`: @NotBlank - Must provide airline code
- `flights`: @NotEmpty - Must have at least one flight
- Per flight:
  - `fromAirport`: @NotBlank
  - `toAirport`: @NotBlank
  - `departureTime`: @NotNull, must be valid datetime
  - `arrivalTime`: @NotNull, must be valid datetime
  - `price`: @NotNull, @Positive - Must be greater than 0
  - `totalSeats`: @NotNull, @Positive - Must be greater than 0
  - `availabeSeats`: @NotNull, @Min(0) - Must be >= 0
- **Custom validation**: Arrival time must be after departure time

**Search Flights (`POST /api/v1.0/flight/search`)**:
- `from`: @NotBlank - Must provide origin airport
- `to`: @NotBlank - Must provide destination airport
- `journeyDate`: @NotNull - Must provide journey date
- `tripType`: @NotNull - Must specify trip type (ONE_WAY or ROUND_TRIP)

**Book Flight Ticket (`POST /api/v1.0/flight/booking/{flightId}`)**:
- `name`: @NotBlank - User name required
- `email`: @NotBlank, @Email - Must be valid email format
- `tripType`: @NotNull - Must specify ONE_WAY or ROUND_TRIP
- `numberOfSeats`: @NotNull, @Positive - Must be greater than 0
- `passengers`: @NotEmpty, @Valid - Must have at least one passenger
- Per passenger:
  - `name`: @NotBlank - Passenger name required
  - `gender`: @NotNull - Must specify MALE, FEMALE, or OTHER
  - `age`: @NotNull, @Range(min=0, max=100) - Age must be 0-100
  - `mealType`: @NotNull - Must specify VEG or NON_VEG
  - `seatNumber`: @NotBlank - Seat number required (e.g., "12A")

### Business Logic Validations

**Booking Validations**:
- Number of passengers must exactly match numberOfSeats
- At least one passenger is required
- If tripType is ROUND_TRIP, returnFlightId must be provided
- If tripType is ONE_WAY, returnFlightId is optional and ignored
- Both outward and return flight IDs must correspond to existing flights
- Both flights must have status SCHEDULED
- Sufficient seats must be available on both flights

**Airline Inventory Validations**:
- Airline with provided airlineCode must exist in database
- Arrival time must be after departure time
- Total seats must be greater than 0
- All date/time values must be valid and properly formatted
- At least one flight must be provided in the request

**Cancellation Validations**:
- Booking with provided PNR must exist
- Current time must be at least 24 hours before flight departure
- Booking must be in BOOKED status to be cancelled
- For round-trip bookings, cancellation only allowed if both legs can be cancelled

**Flight Search Validations**:
- Origin and destination airport codes must not be empty
- Journey date must be a valid date
- Only flights with SCHEDULED status are returned
- Search is performed for the entire specified day (00:00 to 23:59)

### Validation Error Handling

All validation errors are handled by Spring's global error handler:
- Invalid request bodies return `400 Bad Request` with detailed error messages
- Business logic violations throw custom exceptions
- ResourceNotFoundException returns `404 Not Found`
- CancellationNotAllowedException returns `409 Conflict` or `400 Bad Request`
- All exceptions include descriptive error messages for debugging

### Data Integrity Validations

- **Foreign Key Constraints**: All relationships are enforced at database level
- **Unique Constraints**: Email addresses and PNRs are unique
- **Transactional Consistency**: Database operations are transactional to prevent data inconsistency
- **Seat Management**: Available seats are decremented on booking and incremented on cancellation
- **Status Management**: Booking and flight statuses are strictly controlled

## Development

### Hot Reload
Spring Boot DevTools is included for automatic restart during development.

### Lombok
The project uses Lombok annotations to reduce boilerplate code. Ensure your IDE has Lombok plugin installed.

### Logging
Uses SLF4J with Lombok's `@Slf4j` annotation. Configured in `application.properties`.

## Testing Summary

The application has been thoroughly tested across multiple layers to ensure functional correctness, robustness, and adherence to business rules.

---

### **1. Unit Testing (Service Layer)**  
Performed using **JUnit 5** and **Mockito**, focusing on validating core business logic.

#### **BookingServiceImpl Tests**
- Successful **ONE_WAY** bookings 
- Successful **ROUND_TRIP** bookings 
- **Seat availability** validation 
- **Taken seat** conflict detection 
- **Itinerary retrieval** via PNR 
- **Booking history retrieval** 
- **Cancellation rules**
  - Allowed (more than 24 hours before departure)
  - Not allowed (within 24 hours)

These tests mock the repositories and isolate service logic, resulting in high coverage.

#### **FlightServiceImpl Tests**
- Flight search logic 
- Date range and availability checks

#### **AdminServiceImpl Tests**
- Airline inventory creation 
- Validation for arrival time > departure time 
- Airline existence checks 

---

### **2. Controller Testing (Web Layer)**  
Executed using **@WebMvcTest** and `MockMvc`:

- `/search` – Flight search 
- `/booking/{flightId}` – Booking requests 
- `/ticket/{pnr}` – Ticket lookup 
- `/booking/history/{email}` – Booking history 
- `/booking/cancel/{pnr}` – Cancellations 
- `/airline/inventory/add` – Admin flight creation 

Verified:
- URL mappings 
- JSON request and response structure 
- HTTP status codes 
- Error response handling 

---

### **3. Repository Testing (Data Layer)**  
Using **@DataJpaTest** with an in-memory DB:

- `PassengerRepository.findTakenSeatNumbers` 
- `FlightRepository` derived queries 

Validated JPQL correctness, custom queries, and entity mappings.

---

### **4. Exception & Validation Testing**  
Tests verify correct handling of:

- `SeatNotAvailableException` 
- `ResourceNotFoundException` 
- `CancellationNotAllowedException` 
- `MethodArgumentNotValidException` via global exception handler 

Ensures proper HTTP status codes and message formatting.

---

### **5. Code Coverage (JaCoCo)**  

Coverage generated with:

```bash
mvn clean test

