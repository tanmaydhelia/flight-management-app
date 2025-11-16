package com.flightapp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingRequest {

	@NotBlank
	private String name;
	
	@NotBlank
	@Email
	private String email;
	
	@NotNull
	private TripType tripType;
	
	private int returnFlightId;
	
	@NotNull
	@Positive
	private int numberOfSeats;
	
	@NotEmpty
	@Valid
	private List<PassengerRequest> passengers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TripType getTripType() {
		return tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}

	public int getReturnFlightId() {
		return returnFlightId;
	}

	public void setReturnFlightId(int returnFlightId) {
		this.returnFlightId = returnFlightId;
	}

	public int getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public List<PassengerRequest> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerRequest> passengers) {
		this.passengers = passengers;
	}
}
