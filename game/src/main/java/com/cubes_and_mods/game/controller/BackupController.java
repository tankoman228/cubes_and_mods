package com.cubes_and_mods.game.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.game.db.Backup;
import com.cubes_and_mods.game.service.ServiceBackup;
import com.cubes_and_mods.game.service.mineserver_process.ServiceHandlers;

/**
 * Work to minecraft server backups
 * */
@RestController
@RequestMapping("/backup")
public class BackupController {

	@Autowired
	private ServiceBackup service;
	
	@Autowired
	ServiceHandlers ServiceHandlers;
	
	private Random random = new Random();

	@PostMapping("/all/{id}")
	public ResponseEntity<List<Backup>> all(@PathVariable int id) {	
		return new ResponseEntity<List<Backup>>(service.GetBackupsForMineserver(id), HttpStatus.OK);
	}
	
	@PostMapping("/create/{id_server}")
	public ResponseEntity<Integer> create(@RequestBody String name, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.CreateBackup(ServiceHandlers.get(id_server), name, id_task);	
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/rollback/{id_server}/{id_backup}")
	public ResponseEntity<Integer> rollback(@PathVariable int id_server, @PathVariable long id_backup) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.RollbackBackupArchive(ServiceHandlers.get(id_server), id_backup, id_task);
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/delete/{id_server}")
	public ResponseEntity<Integer> delete(@RequestBody Backup backup, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.DeleteBackup(ServiceHandlers.get(id_server), backup, id_task);
		return new ResponseEntity<Integer>(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/get_status")
	public ResponseEntity<String> get_status(@RequestBody String id_) {
		
		int id = Integer.parseInt(id_);
		return new ResponseEntity<String>(service.getStatus(id), HttpStatus.OK);
	}	
}
