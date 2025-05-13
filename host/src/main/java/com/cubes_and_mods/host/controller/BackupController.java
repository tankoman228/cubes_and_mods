package com.cubes_and_mods.host.controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.cubes_and_mods.host.service.ServiceDockerContainersHandlers;
/**
 * Work to minecraft server backups
 * */
@RestController
@RequestMapping("/backup")
public class BackupController {

	@Autowired
	private BackupRepos backupRepos;

	@Autowired
	private ServiceDockerContainersHandlers serviceDockerContainersHandlers;
	
	public static class BackupRequest {
		public Integer id;
		public String name;
	}

	@PostMapping("/{id_host}/all")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<Backup>> all(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_host) { 
		return ResponseEntity.ok(backupRepos.findAll().stream().filter(x -> x.getIdHost().equals(id_host)).toList()); 
	}


	private final ConcurrentHashMap<Integer, String> backupOperations = new ConcurrentHashMap<>();
	private final SecureRandom random = new SecureRandom();

	@PostMapping("/{id_host}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Integer> backup(@RequestBody ProtectedRequest<BackupRequest> request, @PathVariable Integer id_host) { 
		try {
			var container = serviceDockerContainersHandlers.getContainer(id_host, request);
			var id_operation = random.nextInt();

			new Thread(() -> {
				try {
					Backup backup = new Backup();
					backup.setIdHost(id_host);
					backup.setName(request.data.name);

					backupOperations.put(id_operation, "making archive");

					container.fileManager.makeBackup(backup);

					backup.setCreatedAt(LocalDateTime.now());

					backupOperations.put(id_operation, "saving to db");

					backupRepos.save(backup);
					backupRepos.flush();

					backupOperations.put(id_operation, "success");

				} catch (Exception e) {
					backupOperations.put(id_operation, "error at " + backupOperations.get(id_operation) + " " + e.getMessage());
					e.printStackTrace();
				}
			}).start();
			
			return ResponseEntity.status(id_operation).build(); 

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} 
	}

	@PutMapping("/{id_host}/rollback/{id_back}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Integer> rollback(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_host, @PathVariable Integer id_back) { 
		try {
			var container = serviceDockerContainersHandlers.getContainer(id_host, request);
			var id_operation = random.nextInt();

			new Thread(() -> {
				try {
					backupOperations.put(id_operation, "finding backup");
					Backup backup = backupRepos.findById(id_back).orElseThrow();

					backupOperations.put(id_operation, "rolling back");
					container.fileManager.rollbackToBackup(backup);

					backupOperations.put(id_operation, "saving to db");
					backupRepos.save(backup);
					backupRepos.flush();

					backupOperations.put(id_operation, "success");

				} catch (Exception e) {
					backupOperations.put(id_operation, "error at " + backupOperations.get(id_operation) + " " + e.getMessage());
					e.printStackTrace();
				}
			}).start();
			
			return ResponseEntity.status(id_operation).build(); 

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} 
	}
	
	@PostMapping("/{id_host}/{id_back}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Integer> backup_delete(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_host, @PathVariable Integer id_back) { 
		try {
			var container = serviceDockerContainersHandlers.getContainer(id_host, request);
			var id_operation = random.nextInt();

			new Thread(() -> {
				try {
					backupOperations.put(id_operation, "finding backup");
					Backup backup = backupRepos.findById(id_back).orElseThrow();

					backupOperations.put(id_operation, "deleting backup");
					container.fileManager.deleteBackup(backup);

					backupOperations.put(id_operation, "removing from db");
					backupRepos.delete(backup);
					backupRepos.flush();

					backupOperations.put(id_operation, "success");

				} catch (Exception e) {
					backupOperations.put(id_operation, "error at " + backupOperations.get(id_operation) + " " + e.getMessage());
					e.printStackTrace();
				}
			}).start();
			
			return ResponseEntity.status(id_operation).build(); 

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} 
	}

	@PostMapping("/get_status/{id_operation}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<String> get_status(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_operation) {  
		if (backupOperations.containsKey(id_operation)) {
			return ResponseEntity.ok(backupOperations.get(id_operation));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
