package com.cubes_and_mods.host.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.security.ProtectedRequest;
//import com.cubes_and_mods.host.db.Mineserver;
//import com.cubes_and_mods.host.repos.ReposMineserver;
//import com.cubes_and_mods.host.repos.ReposVersion;
import com.cubes_and_mods.host.service.ServiceHandlers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/")
public class RootController {
	
	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	

        if (ProtectedRequest.c != null) return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Чтоюы после регистрации ЭЦП не менялась никогда и никем

		var answ = new ResponseEntity<>(new VerifyWebResponce(r.a + r.b), HttpStatus.OK); 
        ProtectedRequest.c = String.valueOf(r.a + r.b);
        return answ;
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

	@PostMapping("/{id_host}")
	public ResponseEntity<Void> init(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@DeleteMapping("/{id_host}")
	public ResponseEntity<Void> rid_host(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@PostMapping("/log")
	public ResponseEntity<Void> log(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@GetMapping("/{id_user}")
	public ResponseEntity<Void> global_network_config(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	@GetMapping("/system_journal/{id_user}")
	public ResponseEntity<Void> system_journal(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	/*
	@Autowired
	private ReposMineserver mineservers;
	
	@Autowired
	private ReposVersion versions;
	
	@Autowired
	private ServiceHandlers ServiceHandlers;
	

	@PostMapping("launch")
	public ResponseEntity<Void> launch(@RequestBody Integer id) {
			
		Mineserver mineserver;	
		try {
			mineserver = mineservers.findById(id).get();
		}
		catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
			
		var handler = ServiceHandlers.get(id);
		
		try {
			handler.launch();
		} catch (IOException e) {				
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
		}	
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("kill")
	public ResponseEntity<Void> kill(@RequestBody Integer id) {
		
		ServiceHandlers.KillProcess(id); 
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("is_alive")
	public ResponseEntity<Boolean> is_alive(@RequestBody Integer id) {	
		return new ResponseEntity<>(ServiceHandlers.get(id).isLaunched(), HttpStatus.OK);
	}
	
	@PostMapping("unpack_server")
	public ResponseEntity<Void> create_server(@RequestBody UnpackPayload payload) {
		System.out.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
		try {
			ServiceHandlers.get(payload.id_mineserver).initializeByVersion(
					versions.getReferenceById(payload.id_version));
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	private static class UnpackPayload {
		public int id_version;
		public int id_mineserver;
	}
	
	@PostMapping("mineserver_installed/{id}")
	public ResponseEntity<Boolean> mineserverInstalled(@PathVariable int id) {	
		
		var handler = ServiceHandlers.get(id);
		return new ResponseEntity<>(handler.GetFilesTree().exists(), HttpStatus.OK);
	}
	
	@PostMapping("delete_server/{id}")
	public ResponseEntity<Void> delete_server(@PathVariable Integer id) {
		
		var h = ServiceHandlers.get(id);
		h.killProcess();
		h.DeleteFile("");
		
		return new ResponseEntity<>(HttpStatus.OK);
	}*/
}
