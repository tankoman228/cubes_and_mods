package com.cubes_and_mods.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.auth.service.ServiceMicroservices;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/microservice")
public class MicroserviceController {

	@Autowired
	private ServiceMicroservices serviceMicroservices;

	@PutMapping("register")
	public ResponseEntity<Void> register(HttpServletRequest request) { 
		
		String clientIpAndPort = request.getRemoteAddr() + ":" + request.getRemotePort();
		return ResponseEntity.status(serviceMicroservices.RegisterMicroservice(clientIpAndPort)).build(); 
	}

	@GetMapping("log")
	public ResponseEntity<Void> log(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

	@GetMapping("registered_sessions")
	public ResponseEntity<Void> registered_sessions(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

	@PostMapping("service_type_check")
	public ResponseEntity<Void> service_type_check(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
}
