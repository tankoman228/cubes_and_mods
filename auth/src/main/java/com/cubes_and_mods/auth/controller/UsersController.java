package com.cubes_and_mods.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.auth.jpa.Client;
import com.cubes_and_mods.auth.security.ProtectedRequest;
import com.cubes_and_mods.auth.service.ServiceUsers;


@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private ServiceUsers serviceUsers;

	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody ProtectedRequest<Client> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody ProtectedRequest<Client> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<Void> confirm(@RequestBody ProtectedRequest<String> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@PutMapping("/change_password")
	public ResponseEntity<Void> change_password(@RequestBody ProtectedRequest<Client> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Void> users(@RequestBody ProtectedRequest<Void> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@PostMapping("/by_email")
	public ResponseEntity<Void> by_email(@RequestBody ProtectedRequest<String> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody ProtectedRequest<Client> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
	
	@PostMapping("/get_session")
	public ResponseEntity<Void> get_session(@RequestBody ProtectedRequest<Client> request) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}	
}
