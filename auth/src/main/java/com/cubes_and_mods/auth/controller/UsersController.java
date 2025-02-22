package com.cubes_and_mods.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/users")
public class UsersController {

	@PostMapping("/login")
	public ResponseEntity<Void> login(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("/confirm")
	public ResponseEntity<Void> confirm(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PutMapping("/change_password")
	public ResponseEntity<Void> change_password(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/{id}")
	public ResponseEntity<Void> users(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("/by_email")
	public ResponseEntity<Void> by_email(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("/get_session")
	public ResponseEntity<Void> get_session(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	
}
