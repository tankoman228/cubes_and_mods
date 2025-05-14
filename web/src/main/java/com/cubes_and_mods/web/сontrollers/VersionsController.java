package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.web_clients.res.VersionsClient;

import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/versions")
public class VersionsController {
	
	@Autowired
	VersionsClient versionsClient;
	
	/*@GetMapping("/")
	public Flux<VersionWithoutArchive> getAll(){
		return versionsClient.getAll();
	}*/

	@PostMapping("/getAllGames")
	public Flux<Game> postMethodName() {
		return versionsClient.getAllGames();
	}
	
	@PostMapping("getVersionByGameID")
	public Flux<Version> getVersionByGameID(@RequestParam int id) {
		return versionsClient.getVersionByGameID(id);
	}
	
	@PostMapping("searchVersionByGameID")
	public Flux<Version> searchVersionByGameID(@RequestParam int id, @RequestBody String search) {
		return versionsClient.searchVersionByGameID(id, search);
	}
	
}
