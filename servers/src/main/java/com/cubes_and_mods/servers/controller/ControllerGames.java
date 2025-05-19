package com.cubes_and_mods.servers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.servers.jpa.Game;
import com.cubes_and_mods.servers.jpa.repos.GameRepos;
import com.cubes_and_mods.servers.jpa.repos.VersionRepos;
import com.cubes_and_mods.servers.security.ProtectedRequest;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins.MService;

@RestController
@RequestMapping("/games")
public class ControllerGames {

	@Autowired
	private GameRepos gameRepos;

	@Autowired 
	private VersionRepos versionRepos;

	@PostMapping
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<Game>> games(@RequestBody ProtectedRequest<Void> request) { 
		List<Game> games = gameRepos.findAll();
		return ResponseEntity.ok(games);
	}
	
	@PostMapping("/{id}/versions")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<VersionDto>> versions(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		List<VersionDto> versions = versionRepos.findByGameIdAndNameContaining(id, "");
		return ResponseEntity.ok(versions);
	}
	
	@PostMapping("/{id}/versions/search")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<VersionDto>> search(@RequestBody ProtectedRequest<String> request, @PathVariable Integer id) { 
		List<VersionDto> versions = versionRepos.findByGameIdAndNameContaining(id, request.data);
		return ResponseEntity.ok(versions);
	}
}