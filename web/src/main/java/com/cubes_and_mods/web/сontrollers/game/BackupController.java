package com.cubes_and_mods.web.сontrollers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.model.BackupRequest;
import com.cubes_and_mods.web.jpa.Backup;
import com.cubes_and_mods.web.web_clients.game.BackupClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/backups")
public class BackupController {

	@Autowired
	BackupClient backupClient;
	
	@PostMapping("")
	Flux<Backup> getBackupsForMineServer(@RequestParam int id){
		return backupClient.getBackupsForMineServer(id);
	}
	
	@PostMapping("/create")
	Mono<ResponseEntity<Integer>> create(@RequestParam int id_server, @RequestBody BackupRequest backupRequest){
		return backupClient.create(id_server, backupRequest);
	}
	
	@PostMapping("/rollBack")
	Mono<ResponseEntity<Integer>> rollback(@RequestParam int id_server, @RequestParam long id_backup){
		return backupClient.rollback(id_server, id_backup);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Integer>> delete(@RequestParam int id_server, @RequestBody Backup backup){
		return backupClient.delete(id_server, backup.getId());
	}
	
	@PostMapping("/status")
	public Mono<ResponseEntity<String>> get_status(@RequestParam String id_task){
		
		return backupClient.get_status(id_task);
	}
}
