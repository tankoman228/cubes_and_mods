package com.cubes_and_mods.game.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

import jakarta.servlet.http.HttpServletRequest;
import service_repos.ServiceBackup;

@RestController
@RequestMapping("/backup")
public class BackupController {

	@Autowired
	private ServiceBackup service;
	
	private Random random = new Random();
	
	@PostMapping("/all")
	public ResponseEntity<List<Backup>> all(@RequestBody int id) {	
		return new ResponseEntity(service.GetBackupsForMineserver(id), HttpStatus.OK);
	}
	
	@PostMapping("/create/{id_server}")
	public ResponseEntity<Integer> create(@RequestBody String name, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.CreateBackup(WebsocketMinecraftConsole.HANDLED.get(id_server), name, id_task);	
		return new ResponseEntity(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/rollback/{id_server}")
	public ResponseEntity<Integer> rollback(@PathVariable int id_server, @RequestBody int id_b) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.RollbackBackupArchive(WebsocketMinecraftConsole.HANDLED.get(id_server), id_b, id_task);
		return new ResponseEntity(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/delete/{id_server}")
	public ResponseEntity<Integer> delete(@RequestBody Backup backup, @PathVariable int id_server) {
		
		int id_task = random.nextInt(9999) + id_server * 100000;
		
		service.RemoveBackupArchive(WebsocketMinecraftConsole.HANDLED.get(id_server), backup, id_task);
		return new ResponseEntity(id_task, HttpStatus.OK);
	}
	
	@PostMapping("/get_status")
	public ResponseEntity<String> get_status(@RequestBody int id) {
		return new ResponseEntity(service.getStatus(id), HttpStatus.OK);
	}	

	@PostMapping("/upload")
	public ResponseEntity<Void> uploadFile(HttpServletRequest request, @RequestBody String filePath) throws IOException {
		return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
	}
}
