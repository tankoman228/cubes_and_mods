package com.cubes_and_mods.game.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FilesController {

	@PostMapping("/")
	public void files() {
		
	}
	
	@PostMapping("/file")
	public void file() {
		
	}
	
	@PostMapping("/upload")
	public void upload() {
		
	}
	
	@PostMapping("/delete")
	public void delete() {
		
	}
}
