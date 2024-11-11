package com.cubes_and_mods.game.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RootController {

	@PostMapping("launch")
	public void launch() {
		
	}
	
	@PostMapping("create_server")
	public void create_server() {
		
	}
	
	@PostMapping("delete_server")
	public void delete_server() {
		
	}
}
