package com.cubes_and_mods.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.auth.jpa.Client;
import com.cubes_and_mods.auth.security.ClientSession;
import com.cubes_and_mods.auth.security.ProtectedRequest;
import com.cubes_and_mods.auth.service.ServiceClientSessions;
import com.cubes_and_mods.auth.service.ServiceLoginRegister;


@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private ServiceClientSessions serviceUsers;

	@Autowired
	private ServiceLoginRegister serviceLoginRegister;


	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody ProtectedRequest<Client> request) { 

		try {
			var client = serviceLoginRegister.login(request.data);
			if (client == null || client.getBanned()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			var token = serviceUsers.startSessionGetCode(client);
			return ResponseEntity.ok(token);
		}
		catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody ProtectedRequest<Client> request) { 
		try {
			var client = request.data;
			var code = serviceLoginRegister.requireRegisterGetCode(client);
			if (code == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			return ResponseEntity.ok(code);
		}
		catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} 
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<String> confirm(@RequestBody ProtectedRequest<String> request) { 
		try {
			var client = serviceLoginRegister.confirmByCode(request.data);
			if (client == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			var token = serviceUsers.startSessionGetCode(client);
			return ResponseEntity.ok(token);
		}
		catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
	
	@PutMapping("/change_password")
	public ResponseEntity<String> change_password(@RequestBody ProtectedRequest<Client> request) { 
		try {
			var client = request.data;
			var code = serviceLoginRegister.changePasswordGetCode(client);
			if (code == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			return ResponseEntity.ok(code);
		}
		catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody ProtectedRequest<String> request) { 

		try {
			serviceUsers.deleteSession(request.data);		
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PostMapping("/get_session")
	public ResponseEntity<ClientSession> get_session(@RequestBody ProtectedRequest<String> request) { 
		
		try {
			return ResponseEntity.status(HttpStatus.OK).body(serviceUsers.getSession(request.data));
		} catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}	
}
