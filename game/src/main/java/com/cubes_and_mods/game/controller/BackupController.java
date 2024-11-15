package com.cubes_and_mods.game.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backup")
public class BackupController {

	@PostMapping("/all")
	public void all() {}
	
	@PostMapping("/create")
	public void create() {}
	
	@PostMapping("/rollback")
	public void rollback() {}
	
	@PostMapping("/delete")
	public void delete() {}
	
	@PostMapping("/get_status")
	public void get_status() {}
}
