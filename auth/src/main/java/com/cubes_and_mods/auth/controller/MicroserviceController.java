package com.cubes_and_mods.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/microservice")
public class MicroserviceController {

	@PutMapping("register")
	public ResponseEntity<Void> register(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@GetMapping("log")
	public ResponseEntity<Void> log(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@GetMapping("registered_sessions")
	public ResponseEntity<Void> registered_sessions(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("service_type_check")
	public ResponseEntity<Void> service_type_check(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
}
