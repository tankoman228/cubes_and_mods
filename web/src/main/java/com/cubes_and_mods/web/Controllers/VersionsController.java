package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.cubes_and_mods.web.Clients.VersionsClient;
import com.cubes_and_mods.web.Clients.model.VersionWithoutArchive;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/versions")
public class VersionsController {
	
	@Autowired
	VersionsClient versionsClient;
	
	@GetMapping("/")
	public Flux<VersionWithoutArchive> getAll(){
		return versionsClient.getAll();
	}
}
