package com.cubes_and_mods.host.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.jpa.Backup;
import com.cubes_and_mods.host.jpa.repos.BackupRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins.MService;
/**
 * Work to minecraft server backups
 * */
@RestController
@RequestMapping("/backup")
public class BackupController {

	@Autowired
	private BackupRepos backupRepos;
	
	@PostMapping("/{id_host}/all")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> all(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_host) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@PostMapping("/{id_host}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> backup() { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@PutMapping("/{id_host}/rollback/{id_back}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> rollback() { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@DeleteMapping("/{id_host}/{id_back}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> backuphgf() { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@GetMapping("/get_status/{id_operation}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> get_status() {  
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}
}
