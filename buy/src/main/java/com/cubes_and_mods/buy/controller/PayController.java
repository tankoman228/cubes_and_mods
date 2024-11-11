package com.cubes_and_mods.buy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PayController {

	@PostMapping("/request")
	public ResponseEntity<Void> request() {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<Void> confirm() {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@PostMapping("/decline")
	public ResponseEntity<Void> decline() {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@PostMapping("/prolong_request")
	public ResponseEntity<Void> prolong_request() {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@PostMapping("/return")
	public ResponseEntity<Void> return_() {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
}
