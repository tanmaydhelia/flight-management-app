package com.flightapp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.dto.AirlineInventoryRequest;
import com.flightapp.dto.AirlineInventoryResponse;
import com.flightapp.service.AdminService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1.0/flight/airline")
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		super();
		this.adminService = adminService;
	}
	
	@PostMapping("inventory/add")
	public AirlineInventoryResponse addInventory(@RequestBody @Valid AirlineInventoryRequest req) {
		
		log.info("POST /api/v1.0/flight/airline/inventory/add for airlineCode={} flights={}", req.getAirlineCode(), req.getFlights()!=null?req.getFlights().size():0);
		return adminService.addInventory(req);
	}
}
