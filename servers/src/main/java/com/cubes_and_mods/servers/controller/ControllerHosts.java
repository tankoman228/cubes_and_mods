package com.cubes_and_mods.servers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.cubes_and_mods.servers.service_repos.ServiceMineservers;

/**
 * Getter of mineservers for one user, for all users, getting mineserver obj from DB by ID
 * */
@RestController
@RequestMapping("/hosts")
public class ControllerHosts {
	
	@GetMapping("/hosts")	
	public ResponseEntity<Void> hosts(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/hosts/of_server/{id}")
	public ResponseEntity<Void> of_server(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/hosts/of_user/{id}")
	public ResponseEntity<Void> of_user(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/hosts/{id}")
	public ResponseEntity<Void> id(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PutMapping("/hosts/{id}/edit")
	public ResponseEntity<Void> edit(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PostMapping("/hosts/{id}/share")
	public ResponseEntity<Void> share(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	
	/*
	@Autowired
	private ServiceMineservers serviceMineservers;
	
    @PostMapping("/all/{id}")
    public ResponseEntity<List<Mineserver>> getAllOfUser(@PathVariable Integer id) {
		return new ResponseEntity<>(serviceMineservers.findByIdOfUser(id), HttpStatus.OK);
    }
    
    @PostMapping("/all")
    public ResponseEntity<List<Mineserver>> getAll() {
		return new ResponseEntity<>(serviceMineservers.findAll(), HttpStatus.OK);
    }
    
    @PostMapping("/{id}")
    public ResponseEntity<Mineserver> get(@PathVariable Integer id) {
		return new ResponseEntity<>(serviceMineservers.findById(id), HttpStatus.OK);
    }
    
    // DO NOT INVOKE THIS METHOD! USED IN ADMIN PANEL ONLY. After deleting youl'll have a lot of garbage files
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
    	serviceMineservers.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
    }*/
}
