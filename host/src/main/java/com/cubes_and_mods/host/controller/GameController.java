package com.cubes_and_mods.host.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class GameController {

	@PostMapping("/{id}/is_alive")
	public ResponseEntity<Void> s_alive(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/{id}/launch")
	public ResponseEntity<Void> launch(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/{id}/kill")
	public ResponseEntity<Void> kill(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/{id}/unpack_by_version/{vid}")
	public ResponseEntity<Void> unpack_by_version(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/{id}/installed")
	public ResponseEntity<Void> installed(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@DeleteMapping("/{id}/uninstall")
	public ResponseEntity<Void> uninstall(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

}
