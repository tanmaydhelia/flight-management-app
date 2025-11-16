package com.flightapp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Itinerary itinerary;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Flight flight;
	
	@NotNull
	private LocalDate journeyDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TripSegmentType segmentType;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
	private List<Passenger> passengers = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}
	
	public LocalDate getJourneyDate() {
		return journeyDate;
	}

	public void setJourneyDate(LocalDate journeyDate) {
		this.journeyDate = journeyDate;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	public Itinerary getItinerary() {
		return itinerary;
	}

	public void setItinerary(Itinerary itinerary) {
		this.itinerary = itinerary;
	}
	
	public TripSegmentType getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(TripSegmentType segmentType) {
		this.segmentType = segmentType;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}
}

