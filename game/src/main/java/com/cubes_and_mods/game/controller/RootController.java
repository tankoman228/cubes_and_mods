package com.cubes_and_mods.game.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;
import com.cubes_and_mods.game.repos.ReposVersion;
import com.cubes_and_mods.game.service.Config;
import com.cubes_and_mods.game.service.ServiceMinecraftServerObserver;
import com.cubes_and_mods.game.service.mineserver_process.IMinecraftHandler;
import com.cubes_and_mods.game.service.mineserver_process.MinecraftHandler;
import com.cubes_and_mods.game.service.mineserver_process.MinecraftServerObserver;

@RestController
@RequestMapping("/")
public class RootController {
	
	@Autowired
	private ReposMineserver mineservers;
	
	@Autowired
	private ReposVersion versions;
	
	@Autowired
	private ReposTariff tariffs;
	
	@Autowired
	private ServiceMinecraftServerObserver observers;
	

	@PostMapping("launch")
	public ResponseEntity<Void> launch(@RequestBody Integer id) {
		
		
		Mineserver mineserver;
		
		try {
			mineserver = mineservers.findById(id).get();
		}
		catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
			
		if (WebsocketMinecraftConsole.HANDLED.containsKey(id)) {
			
			var handler = getHandler(id);
			
			try {
				handler.launch();
			} catch (IOException e) {				
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
			}

		}
		else {		
		
			var handler = new MinecraftHandler(mineserver, "run.sh");
			//observers.StartObserving(handler);

			WebsocketMinecraftConsole.HANDLED.put(id, handler);
			try {
				handler.launch();
			} catch (IOException e) {				
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
			}
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
	private static class UnpackPayload {
		public int id_version;
		public int id_mineserver;
	}
	
	@PostMapping("delete_server")
	public ResponseEntity<Void> delete_server(@RequestBody Integer id) {
		
		var h = getHandler(id);
		h.DeleteFile(Config.PATH_TO_SERVERS);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private IMinecraftHandler getHandler(int id) {
		
		if (WebsocketMinecraftConsole.HANDLED.containsKey(id)) {
			
			return WebsocketMinecraftConsole.HANDLED.get(id);
		}
		else {
			
			var h = new MinecraftHandler(mineservers.findById(id).get(), "sh run.sh");
			observers.StartObserving(h);
			WebsocketMinecraftConsole.HANDLED.put(id, h);
			
			return h;
		}
	}
}
