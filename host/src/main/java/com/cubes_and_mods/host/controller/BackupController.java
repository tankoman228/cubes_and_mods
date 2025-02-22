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
import com.cubes_and_mods.host.service.ServiceBackup;
import com.cubes_and_mods.host.service.ServiceHandlers;

/**
 * Work to minecraft server backups
 * */
@RestController
@RequestMapping("/backup")
public class BackupController {

	
	@PostMapping("/{id_host}/all")
	public ResponseEntity<Void> all(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/{id_host}")
	public ResponseEntity<Void> backup(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PutMapping("/{id_host}/rollback/{id_back}")
	public ResponseEntity<Void> rollback(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@DeleteMapping("/{id_host}/{id_back}")
	public ResponseEntity<Void> backuphgf(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@GetMapping("/get_status/{id_operation}")
	public ResponseEntity<Void> get_status(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	
	/*
	@Autowired
	private ServiceBackup service;
	
	@Autowired
	private BackupRepos br;
	
	@Autowired
	ServiceHandlers ServiceHandlers;
	
	private Random random = new Random();

	@GetMapping("/all/{id}")
	public ResponseEntity<List<Backup>> all(@PathVariable int id) {	
		
		return new ResponseEntity<List<Backup>>(br.findAll(), HttpStatus.OK);
		//return new ResponseEntity<List<Backup>>(service.GetBackupsForMineserver(id), HttpStatus.OK);
	}
	
	@PostMapping("/create/{id_server}")
	public ResponseEntity<Integer> create(@RequestBody String name, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		//service.CreateBackup(ServiceHandlers.get(id_server), name, id_task);	
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/rollback/{id_server}/{id_backup}")
	public ResponseEntity<Integer> rollback(@PathVariable int id_server, @PathVariable long id_backup) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		//service.RollbackBackupArchive(ServiceHandlers.get(id_server), id_backup, id_task);
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/delete/{id_server}")
	public ResponseEntity<Integer> delete(@RequestBody Backup backup, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		//service.DeleteBackup(ServiceHandlers.get(id_server), backup, id_task);
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/get_status")
	public ResponseEntity<String> get_status(@RequestBody String id_) {
		
		int id = Integer.parseInt(id_);
		return new ResponseEntity<String>(service.getStatus(id), HttpStatus.OK);
	}	*/
}
