package com.flightapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flightapp.entity.Booking;
import com.flightapp.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Integer>{
	
	List<Booking> findByItineraryId(int itineraryId);
}
