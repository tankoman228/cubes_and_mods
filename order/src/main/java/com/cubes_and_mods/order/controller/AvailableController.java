package com.cubes_and_mods.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/available")
public class AvailableController {
	
	@GetMapping("/tariffs")
	public ResponseEntity<Void> tariffs(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/servers_for_tariff/{id}")
	public ResponseEntity<Void> servers_for_tariff(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
}
