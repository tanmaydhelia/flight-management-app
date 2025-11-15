package com.flight.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotBlank
	private String pnr;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Flight flight;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	
	private LocalDateTime bookingTime;
	
	private LocalDate journeyDate;
	
	private int totalAmount;
	
	private BookingStatus status;
}
