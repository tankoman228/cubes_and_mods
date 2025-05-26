package com.cubes_and_mods.host.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.docker.FileInfo;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.ServiceCheckClientAllowed;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.CheckUserSession;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins.MService;
import com.cubes_and_mods.host.service.ServiceDockerContainersHandlers;

/**
 * Files of minecraft servers
 * */
@RestController
@RequestMapping("/files")
public class FilesController {

	@Autowired
	private ServiceDockerContainersHandlers serviceContainersHandlers;

	@Autowired
    private ServiceCheckClientAllowed serviceCheckClientAllowed;

	@PostMapping("/{id_host}")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<FileInfo> files(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id_host) 
	{ 
		serviceCheckClientAllowed.checkHostAllowed(request, id_host);
		try {
			var c = serviceContainersHandlers.getContainer(id_host, request);
			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();
			return ResponseEntity.ok(c.fileManager.getFileTree());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/{id_host}/read")
	@AllowedOrigins(MService.WEB) 
	@CheckUserSession
	public ResponseEntity<FileInfo> filesread(@RequestBody ProtectedRequest<String> request, @PathVariable Integer id_host)
	{ 
		serviceCheckClientAllowed.checkHostAllowed(request, id_host);
		try {
			var c = serviceContainersHandlers.getContainer(id_host, request);

			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();
			System.out.println("Контейнер получен");

			return ResponseEntity.ok(c.fileManager.getFileContents(request.data));
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/{id_host}/upload")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Void> filesupload(@RequestBody ProtectedRequest<FileInfo> request, @PathVariable Integer id_host)
	{ 
		serviceCheckClientAllowed.checkHostAllowed(request, id_host);
		try {
			var c = serviceContainersHandlers.getContainer(id_host, request);

			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();

			if(request.data.isDirectory) c.fileManager.mkDir(request.data.path);
			else c.fileManager.uploadFile(request.data);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 
	}

	@PostMapping("/{id_host}/delete")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Void> filesdelete(@RequestBody ProtectedRequest<String> request, @PathVariable Integer id_host)
	{ 
		serviceCheckClientAllowed.checkHostAllowed(request, id_host);
		try {
			System.out.println("начало удаления файла с " + id_host + " по пути " + request.data);
			var c = serviceContainersHandlers.getContainer(id_host, request);
			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();
			c.fileManager.deleteFile(request.data);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 
	}

	@PutMapping("/{id_host}/move")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> filesmove(@RequestBody ProtectedRequest<String[]> request, @PathVariable Integer id_host)
	{ 
		try {
			String[] paths = request.data;
			if(paths.length != 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			var c = serviceContainersHandlers.getContainer(id_host, request);
			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();
			c.fileManager.moveFile(paths[0], paths[1]);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 
	}

	@PutMapping("/{id_host}/copy")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Void> filescopy(@RequestBody ProtectedRequest<String[]> request, @PathVariable Integer id_host)
	{ 
		try {
			String[] paths = request.data;
			if(paths.length != 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			var c = serviceContainersHandlers.getContainer(id_host, request);
			if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();
			c.fileManager.copyFile(paths[0], paths[1]);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 
	}
}
