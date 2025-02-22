package com.cubes_and_mods.servers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class ControllerGames {

	@GetMapping()
	public ResponseEntity<Void> games() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("games/{id}/versions")
	public ResponseEntity<Void> versions() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("games/{id}/versions/search")
	public ResponseEntity<Void> search() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
}
