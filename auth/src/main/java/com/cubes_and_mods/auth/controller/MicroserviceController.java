package com.cubes_and_mods.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.auth.security.ProtectedRequest;
import com.cubes_and_mods.auth.service.ServiceMicroserviceSession;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/microservice")
public class MicroserviceController {

	@Autowired
	private ServiceMicroserviceSession serviceMicroservices;

	@PutMapping("register")
	public ResponseEntity<String> register(HttpServletRequest request, @RequestBody RegisterMsRequest body) { 
		
		String fullUrl = request.getRequestURL().toString();
		String requestUri = request.getRequestURI();

		System.out.println("URL запроса: " + fullUrl);
		System.out.println("URI запроса: " + requestUri);
		
		String clientIpAndPort = request.getRemoteAddr() + ":" + body.port;
		System.out.println("client " +  clientIpAndPort);

		var status = serviceMicroservices.RegisterMicroservice(clientIpAndPort,  body.ms_type, s -> {
			finalSessionIdForRegisterMethodOnly = s.getIpPort();
		});

		return ResponseEntity.status(status).body(finalSessionIdForRegisterMethodOnly);
	}
	private String finalSessionIdForRegisterMethodOnly;
	
	public static class RegisterMsRequest {
		public String ms_type;
		public String port;
	}

	@GetMapping("log")
	public ResponseEntity<Void> log() { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@GetMapping("registered_sessions")
	public ResponseEntity<Void> registered_sessions(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

	@PostMapping("service_type_check")
	public ResponseEntity<String> service_type_check(@RequestBody ProtectedRequest<?> body)
	{ 
		var result = serviceMicroservices.FindMicroserviceSession(body);
		if (result == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		return ResponseEntity.ok(result.getServiceType());
	}
}
