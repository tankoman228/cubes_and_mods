package com.cubes_and_mods.game.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import service_repos.ServiceBackup;

@RestController
@RequestMapping("/backup")
public class BackupController {

	
	@PostMapping("/all")
	public void all() {
		
	}
	
	@PostMapping("/create")
	public void create() {
		
		
	}
	
	@PostMapping("/rollback")
	public void rollback() {
		
	}
	
	@PostMapping("/delete")
	public void delete() {
		
	}
	
	@PostMapping("/get_status")
	public void get_status() {
		
	}
	

	@PostMapping("/upload")
	public void uploadFile(HttpServletRequest request, @RequestBody String filePath) throws IOException {
		
		// TODO: idk
		
	    try (
	    	InputStream inputStream = request.getInputStream();    
	    	OutputStream outputStream = new FileOutputStream(filePath)) {
	        byte[] buffer = new byte[ServiceBackup.FILE_CHUNK_SIZE];
	        int bytesRead;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }
	    }
	}
}
