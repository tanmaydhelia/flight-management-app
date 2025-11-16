package com.flightapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.CancelResponse;
import com.flightapp.dto.ItineraryDto;
import com.flightapp.dto.TripType;
import com.flightapp.entity.Flight;
import com.flightapp.entity.User;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.ItineraryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService{
	private final UserRepository userRepository;
	private final FlightRepository flightRepository;
	private final ItineraryRepository itineraryRepository;
	private final BookingRepository bookingRepository;
	private final PassengerRepository passengerRepository;
	
	public BookingServiceImpl(UserRepository userRepository, FlightRepository flightRepository,
			ItineraryRepository itineraryRepository, BookingRepository bookingRepository,
			PassengerRepository passengerRepository) {
		super();
		this.userRepository = userRepository;
		this.flightRepository = flightRepository;
		this.itineraryRepository = itineraryRepository;
		this.bookingRepository = bookingRepository;
		this.passengerRepository = passengerRepository;
	}

	@Override
	public ItineraryDto bookItinerary(int outwardFlightId, BookingRequest req) {
		log.info("Booking initireary for email={} tripType={} outwardFlight={}", req.getEmail(), req.getTripType(), outwardFlightId);
		
		validateBookingReq(req);
		
		User user = getOrCreateUser(req.getName(), req.getEmail());
		
		Flight outwardFlight = getFlightOrThrow(outwardFlightId);
		Flight returnFlight = null;
		boolean isRoundTrip = req.getTripType()==TripType.ROUND_TRIP;
		
		if(isRoundTrip) {
			if(req.getReturnFlightId()==null) {
				throw new IllegalArgumentException("Return FlightId required!!!");
			}
			returnFlight = getFlightOrThrow(req.getReturnFlightId());
		}
		
		List<String> requestedSeats = req.getPassengers().stream()
				.map(passengerReq -> passengerReq.getSeatNumber())
				.toList();
		
		validateFLightSeats(outwardFlight, requestedSeats, req.getNumberOfSeats());
		
		if(isRoundTrip) validateFLightSeats(returnFlight, requestedSeats, req.getNumberOfSeats());
		
		
	}




	private void validateBookingReq(BookingRequest req) {
		// TODO Auto-generated method stub
		
	}
	private User getOrCreateUser(String name, String email) {
		// TODO Auto-generated method stub
		return null;
	}
	private Flight getFlightOrThrow(int outwardFlightId) {
		// TODO Auto-generated method stub
		return null;
	}
	private void validateFLightSeats(Flight outwardFlight, List<String> requestedSeats, int numberOfSeats) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItineraryDto getItineraryByPnr(String pnr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItineraryDto> getHistoryByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CancelResponse cancelByPnr(String pnr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Booking Ticket
	
	
	
}
