package com.flight;

import com.flightapp.dto.*;
import com.flightapp.entity.*;
import com.flightapp.exception.CancellationNotAllowedException;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.exception.SeatNotAvailableException;
import com.flightapp.repository.*;
import com.flightapp.service.implimentation.BookingServiceImpl; // if your class is in .implimentation

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Flight outwardFlight;
    private Flight returnFlight;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Tanmay");
        user.setEmail("tanmay@example.com");
        user.setRole(Role.USER);

        outwardFlight = new Flight();
        outwardFlight.setId(1);
        outwardFlight.setFromAirport("DEL");
        outwardFlight.setToAirport("BOM");
        outwardFlight.setDepartureTime(LocalDateTime.now().plusDays(3));
        outwardFlight.setArrivalTime(outwardFlight.getDepartureTime().plusHours(2));
        outwardFlight.setPrice(5000);
        outwardFlight.setTotalSeats(180);
        outwardFlight.setAvailableSeats(180);
        outwardFlight.setStatus(FlightStatus.SCHEDULED);

        returnFlight = new Flight();
        returnFlight.setId(2);
        returnFlight.setFromAirport("BOM");
        returnFlight.setToAirport("DEL");
        returnFlight.setDepartureTime(LocalDateTime.now().plusDays(7));
        returnFlight.setArrivalTime(returnFlight.getDepartureTime().plusHours(2));
        returnFlight.setPrice(5500);
        returnFlight.setTotalSeats(180);
        returnFlight.setAvailableSeats(180);
        returnFlight.setStatus(FlightStatus.SCHEDULED);
    }

    // ---------- helper: build a typical BookingRequest ----------

    private BookingRequest buildOneWayRequest() {
        BookingRequest req = new BookingRequest();
        req.setName("Tanmay Dhelia");
        req.setEmail("tanmay@example.com");
        req.setTripType(TripType.ONE_WAY);
        req.setReturnFlightId(null);
        req.setNumberOfSeats(2);

        PassengerRequest p1 = new PassengerRequest();
        p1.setName("Tanmay Dhelia");
        p1.setGender(Gender.MALE);
        p1.setAge(22);
        p1.setMealType(MealType.VEG);
        p1.setSeatNumber("12A");

        PassengerRequest p2 = new PassengerRequest();
        p2.setName("Rahul Sharma");
        p2.setGender(Gender.MALE);
        p2.setAge(23);
        p2.setMealType(MealType.NON_VEG);
        p2.setSeatNumber("12B");

        req.setPassengers(List.of(p1, p2));
        return req;
    }

    private BookingRequest buildRoundTripRequest() {
        BookingRequest req = buildOneWayRequest();
        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnFlightId(2);
        return req;
    }
    
//    @Test
//    void bookItinerary_oneWay_success() {
//        BookingRequest request = buildOneWayRequest();
//
//        when(userRepository.findByEmail("tanmay@example.com"))
//                .thenReturn(Optional.of(user));
//        when(flightRepository.findById(1))
//                .thenReturn(Optional.of(outwardFlight));
//        when(itineraryRepository.save(any(Itinerary.class)))
//                .thenAnswer(invocation -> {
//                    Itinerary it = invocation.getArgument(0);
//                    it.setId(10);
//                    return it;
//                });
//
//        ItineraryDto dto = bookingService.bookItinerary(1, request);
//
//        assertNotNull(dto.getPnr());
//        assertEquals("Tanmay Dhelia", dto.getUserName());
//        assertEquals("tanmay@example.com", dto.getEmail());
//        assertEquals(BookingStatus.BOOKED, dto.getStatus());
//        assertEquals(1, dto.getLegs().size());
//
//        LegDto leg = dto.getLegs().get(0);
//        assertEquals(1L, leg.getFlightId());
//        assertEquals("DEL", leg.getFromAirport());
//        assertEquals("BOM", leg.getToAirport());
//        assertEquals(TripSegmentType.ONE_WAY, leg.getSegmentType());
//        assertEquals(2, leg.getPassengers().size());
//
//        PassengerDto passenger1 = leg.getPassengers().get(0);
//        assertEquals("Tanmay Dhelia", passenger1.getName());
//        assertEquals(MealType.VEG, passenger1.getMealType());
//        assertEquals("12A", passenger1.getSeatNumber());
//
//        // Verify available seats reduced
//        assertEquals(178, outwardFlight.getAvailableSeats());
//        verify(itineraryRepository, times(1)).save(any(Itinerary.class));
//    }

//    @Test
//    void bookItinerary_roundTrip_success() {
//        BookingRequest request = buildRoundTripRequest();
//
//        when(userRepository.findByEmail("tanmay@example.com"))
//                .thenReturn(Optional.of(user));
//        when(flightRepository.findById(1))
//                .thenReturn(Optional.of(outwardFlight));
//        when(flightRepository.findById(2))
//                .thenReturn(Optional.of(returnFlight));
//
//        when(itineraryRepository.save(any(Itinerary.class)))
//                .thenAnswer(invocation -> {
//                    Itinerary it = invocation.getArgument(0);
//                    it.setId(11);
//                    return it;
//                });
//
//        ItineraryDto dto = bookingService.bookItinerary(1, request);
//
//        assertEquals(BookingStatus.BOOKED, dto.getStatus());
//        assertEquals(2, dto.getLegs().size());
//
//        // Outbound leg
//        LegDto outbound = dto.getLegs().get(0);
//        assertEquals(TripSegmentType.OUTBOUND, outbound.getSegmentType());
//        assertEquals(1L, outbound.getFlightId());
//
//        // Return leg
//        LegDto ret = dto.getLegs().get(1);
//        assertEquals(TripSegmentType.RETURN, ret.getSegmentType());
//        assertEquals(2L, ret.getFlightId());
//
//        // Seats reduced on both flights
//        assertEquals(178, outwardFlight.getAvailableSeats());
//        assertEquals(178, returnFlight.getAvailableSeats());
//    }
//    @Test
//    void bookItinerary_notEnoughSeats_throwsSeatNotAvailableException() {
//        BookingRequest request = buildOneWayRequest();
//        outwardFlight.setAvailableSeats(1); // less than numberOfSeats = 2
//
//        when(userRepository.findByEmail("tanmay@example.com"))
//                .thenReturn(Optional.of(user));
//        when(flightRepository.findById(1))
//                .thenReturn(Optional.of(outwardFlight));
//
//        SeatNotAvailableException ex = assertThrows(
//                SeatNotAvailableException.class,
//                () -> bookingService.bookItinerary(1, request)
//        );
//        assertTrue(ex.getMessage().contains("Not enough seats available"));
//        verify(itineraryRepository, never()).save(any());
//    }

//    @Test
//    void bookItinerary_seatConflict_throwsSeatNotAvailableException() {
//        BookingRequest request = buildOneWayRequest();
//
//        when(userRepository.findByEmail("tanmay@example.com"))
//                .thenReturn(Optional.of(user));
//        when(flightRepository.findById(1))
//                .thenReturn(Optional.of(outwardFlight));
//
//        SeatNotAvailableException ex = assertThrows(
//                SeatNotAvailableException.class,
//                () -> bookingService.bookItinerary(1, request)
//        );
//        assertTrue(ex.getMessage().contains("Seats already taken"));
//        verify(itineraryRepository, never()).save(any());
//    }

//    @Test
//    void getItineraryByPnr_success() {
//        Itinerary itinerary = new Itinerary();
//        itinerary.setId(100);
//        itinerary.setPnr("PNR123");
//        itinerary.setUser(user);
//        itinerary.setStatus(BookingStatus.BOOKED);
//        itinerary.setCreatedTime(LocalDateTime.now());
//        itinerary.setTotalAmount(10000);
//
//        Booking booking = new Booking();
//        booking.setId(200);
//        booking.setItinerary(itinerary);
//        booking.setFlight(outwardFlight);
//        booking.setJourneyDate(LocalDate.now().plusDays(3));
//        booking.setSegmentType(TripSegmentType.ONE_WAY);
//        booking.setStatus(BookingStatus.BOOKED);
//
//        Passenger passenger = new Passenger();
//        passenger.setId(300);
//        passenger.setBooking(booking);
//        passenger.setName("Tanmay Dhelia");
//        passenger.setGender(Gender.MALE);
//        passenger.setAge(22);
//        passenger.setMealType(MealType.VEG);
//        passenger.setSeatNumber("12A");
//
//        booking.setPassengers(List.of(passenger));
//        itinerary.setBookings(List.of(booking));
//
//        when(itineraryRepository.findByPnr("PNR123"))
//                .thenReturn(Optional.of(itinerary));
//
//        ItineraryDto dto = bookingService.getItineraryByPnr("PNR123");
//
//        assertEquals("PNR123", dto.getPnr());
//        assertEquals("Tanmay", dto.getUserName());
//        assertEquals(1, dto.getLegs().size());
//        assertEquals(1, dto.getLegs().get(0).getPassengers().size());
//        assertEquals("12A", dto.getLegs().get(0).getPassengers().get(0).getSeatNumber());
//    }
    @Test
    void getItineraryByPnr_notFound_throwsResourceNotFound() {
        when(itineraryRepository.findByPnr("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.getItineraryByPnr("UNKNOWN")
        );
    }

    @Test
    void getHistoryByEmail_returnsItineraryList() {
        Itinerary it1 = new Itinerary();
        it1.setId(1);
        it1.setPnr("P1");
        it1.setUser(user);
        it1.setStatus(BookingStatus.BOOKED);
        it1.setCreatedTime(LocalDateTime.now());
        it1.setTotalAmount(1000);
        it1.setBookings(Collections.emptyList());

        Itinerary it2 = new Itinerary();
        it2.setId(2);
        it2.setPnr("P2");
        it2.setUser(user);
        it2.setStatus(BookingStatus.CANCELLED);
        it2.setCreatedTime(LocalDateTime.now().minusDays(1));
        it2.setTotalAmount(2000);
        it2.setBookings(Collections.emptyList());

        when(itineraryRepository.findByUserEmail("tanmay@example.com"))
                .thenReturn(List.of(it1, it2));

        List<ItineraryDto> result = bookingService.getHistoryByEmail("tanmay@example.com");

        assertEquals(2, result.size());
        assertEquals("P1", result.get(0).getPnr());
        assertEquals("P2", result.get(1).getPnr());
    }
    
    @Test
    void cancelByPnr_success() {
        Itinerary itinerary = new Itinerary();
        itinerary.setId(10);
        itinerary.setPnr("PNR123");
        itinerary.setUser(user);
        itinerary.setStatus(BookingStatus.BOOKED);
        itinerary.setCreatedTime(LocalDateTime.now());
        itinerary.setTotalAmount(10000);

        Booking booking = new Booking();
        booking.setId(20);
        booking.setItinerary(itinerary);
        booking.setFlight(outwardFlight);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setPassengers(List.of(new Passenger(), new Passenger()));
        itinerary.setBookings(List.of(booking));

        when(itineraryRepository.findByPnr("PNR123"))
                .thenReturn(Optional.of(itinerary));

        CancelResponse response = bookingService.cancelByPnr("PNR123");

        assertEquals("PNR123", response.getPnr());
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertEquals(BookingStatus.CANCELLED, itinerary.getStatus());
        assertEquals(BookingStatus.CANCELLED, itinerary.getBookings().get(0).getStatus());
        // 2 passengers → seats restored by +2
        assertEquals(182, outwardFlight.getAvailableSeats());
        verify(itineraryRepository, times(1)).save(itinerary);
    }
    
    @Test
    void cancelByPnr_within24Hours_throwsCancellationNotAllowedException() {
        // departure in 3 hours
        outwardFlight.setDepartureTime(LocalDateTime.now().plusHours(3));

        Itinerary itinerary = new Itinerary();
        itinerary.setId(10);
        itinerary.setPnr("PNR123");
        itinerary.setUser(user);
        itinerary.setStatus(BookingStatus.BOOKED);
        itinerary.setCreatedTime(LocalDateTime.now());
        itinerary.setTotalAmount(10000);

        Booking booking = new Booking();
        booking.setId(20);
        booking.setItinerary(itinerary);
        booking.setFlight(outwardFlight);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setPassengers(List.of(new Passenger()));

        itinerary.setBookings(List.of(booking));

        when(itineraryRepository.findByPnr("PNR123"))
                .thenReturn(Optional.of(itinerary));

        assertThrows(
                CancellationNotAllowedException.class,
                () -> bookingService.cancelByPnr("PNR123")
        );

        verify(itineraryRepository, never()).save(any());
    }
}
