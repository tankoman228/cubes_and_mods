package com.cubes_and_mods.servers.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.servers.VersionZipper;
import com.cubes_and_mods.servers.db.Version;
import com.cubes_and_mods.servers.dto.VersionWithoutArchive;
import com.cubes_and_mods.servers.service_repos.ServiceVersion;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CRD for versions (presets of minecraft servers)
 * */
@RestController
@RequestMapping("/version")
public class ControllerVersion {

	@Autowired
	private ServiceVersion serviceVersion;
	
	@GetMapping("/all")
	public List<VersionWithoutArchive> getAllVersions() {
		return serviceVersion.findAllVersions(); // Ignores blobs!
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> Add(@RequestBody AddRequest r) {
        try {

            Version version = VersionZipper.uploadVersion(
            		r.version.getName(), r.version.getDescription(), r.path
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
	
	private static class AddRequest {
		
	    @JsonProperty("version")
		public Version version;
	    
	    @JsonProperty("path")
		public String path;
	}
}
