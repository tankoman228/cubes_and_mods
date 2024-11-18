package com.cubes_and_mods.game.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backup_destinations")
public class BackupDestinationController {

	@PostMapping("/add")
	public ResponseEntity<Void> ff() {
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
