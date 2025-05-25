package com.cubes_and_mods.host.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.ServiceCheckClientAllowed;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins.MService;
import com.cubes_and_mods.host.security.annotations.CheckUserSession;
import com.cubes_and_mods.host.service.ServiceGame;

@RestController
@RequestMapping("/game")
public class GameController {

	@Autowired
	private ServiceGame service;

	@Autowired
    private ServiceCheckClientAllowed serviceCheckClientAllowed;


	@PostMapping("/{id}/is_alive")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Boolean> is_alive(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {
			return ResponseEntity.ok(service.is_alive(request, id));
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/{id}/launch")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<String> launch(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {
			service.launch(request, id);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/{id}/kill")
	@AllowedOrigins({MService.WEB, MService.SERVERS})
	@CheckUserSession
	public ResponseEntity<String> kill(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id){ 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {
			service.kill(request, id);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/{id}/unpack_by_version/{vid}")
	@AllowedOrigins({MService.WEB, MService.SERVERS})
	@CheckUserSession
	public ResponseEntity<String> unpack_by_version(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id, @PathVariable Integer vid) { 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {
			service.unpack_by_version(request, id, vid);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/{id}/installed")
	@AllowedOrigins({MService.WEB, MService.SERVERS})
	@CheckUserSession
	public ResponseEntity<Boolean> installed(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {		
			return ResponseEntity.ok(service.installed(request, id));
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/{id}/uninstall")
	@AllowedOrigins({MService.WEB})
	@CheckUserSession
	public ResponseEntity<String> uninstall(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		try {
			service.uninstall(request, id);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
