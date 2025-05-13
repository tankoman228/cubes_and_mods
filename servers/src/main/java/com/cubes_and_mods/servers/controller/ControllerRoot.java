package com.cubes_and_mods.servers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.servers.security.ProtectedRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CRUD + resource reserving
 * + conditions checkers "if this server has enough resources for a new server with tariff X"
 * */
@RestController
@RequestMapping
public class ControllerRoot {

	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	

        var re = new VerifyWebResponce(r.a + r.b);
        ProtectedRequest.c = String.valueOf(r.a + r.b);
		return new ResponseEntity<>(re, HttpStatus.OK); 
	}
    public static class VerifyWebRequest { 
        public int a, b;
    }
    public static class VerifyWebResponce {
        public int c;
        @JsonCreator
        public VerifyWebResponce (@JsonProperty("c") int c) {
            this.c = c;
        }
        public VerifyWebResponce () {}
    }
	
	@GetMapping()
	public ResponseEntity<Void> all(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("logs")
	public ResponseEntity<Void> logs(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("{id}")
	public ResponseEntity<Void> id(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
}
