package com.cubes_and_mods.res.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.res.db.Mineserver;
import com.cubes_and_mods.res.service_repos.ServiceMineservers;

/**
 * Getter of mineservers for one user, for all users, getting mineserver obj from DB by ID
 * */
@RestController
@RequestMapping("/mineservers")
public class ControllerMineservers {
	
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
    }
}
