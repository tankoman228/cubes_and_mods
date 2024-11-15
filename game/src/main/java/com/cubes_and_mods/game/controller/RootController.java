package com.cubes_and_mods.game.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mineserver_process.IMinecraftHandler;
import mineserver_process.MinecraftHandler;
import mineserver_process.MinecraftServerObserver;
import service_repos.ReposMineserver;
import service_repos.ReposVersion;

@RestController
@RequestMapping
public class RootController {
	
	@Autowired
	private ReposMineserver mineservers;
	
	@Autowired
	private ReposVersion versions;
	

	@PostMapping("launch")
	public ResponseEntity<Void> launch(@RequestBody Integer id) {
		
		if (WebsocketMinecraftConsole.HANDLED.containsKey(id)) {
			
			var handler = getHandler(id);
			
			try {
				handler.launch();
			} catch (IOException e) {				
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
			}
			new MinecraftServerObserver(handler);
			WebsocketMinecraftConsole.HANDLED.put(id, handler);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("is_alive")
	public ResponseEntity<Boolean> is_alive(@RequestBody Integer id) {	
		return new ResponseEntity<>(getHandler(id).isLaunched(), HttpStatus.OK);
	}
	
	@PostMapping("unpack_server")
	public ResponseEntity<Void> create_server(@RequestBody UnpackPayload payload) {
		
		try {
			getHandler(payload.id_mineserver).initializeByVersion(
					versions.getReferenceById(payload.id_version));
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	private class UnpackPayload {
		public int id_version;
		public int id_mineserver;
	}
	
	@PostMapping("delete_server")
	public ResponseEntity<Void> delete_server(@RequestBody Integer id) {
		
		var h = getHandler(id);
		h.DeleteFile(MinecraftHandler.BASE_PATH_FOR_SERVERS);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private IMinecraftHandler getHandler(int id) {
		
		if (WebsocketMinecraftConsole.HANDLED.containsKey(id)) {
			
			return WebsocketMinecraftConsole.HANDLED.get(id);
		}
		else {
			
			var h = new MinecraftHandler(mineservers.getReferenceById(id), "sh run.sh");
			new MinecraftServerObserver(h);
			WebsocketMinecraftConsole.HANDLED.put(id, h);
			
			return h;
		}
	}
}
