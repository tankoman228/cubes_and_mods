package com.cubes_and_mods.res.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.res.ServerFilesManager;
import com.cubes_and_mods.res.db.Version;
import com.cubes_and_mods.res.service_repos.ServiceVersion;

@RestController
@RequestMapping("/version")
public class ControllerVersion {

	@Autowired
	private ServiceVersion serviceVersion;
	
	@GetMapping("/all")
	public List<String> getAllVersions() {
		return serviceVersion.findAllVersions();
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> Add(@RequestBody AddRequest r) {
        try {
            Version version = ServerFilesManager.uploadVersion(
            		r.version.getName(), r.version.getDescription(), r.path, r.version.getStartCommand()
            		);         
            serviceVersion.saveVersion(version);
            
            return ResponseEntity.ok("Version uploaded successfully: " + version.getName());
        } catch (IOException e) {
            return ResponseEntity.status(666).body("Infernal server error:" + e.getMessage());
        }
        // PPLGOND 2
	}
	
	@PostMapping("/delete")
	public void Delete(@RequestBody String name) {
		serviceVersion.deleteVersion(name);
	}
	
	private class AddRequest {
		public Version version;
		public String path;
	}
}
