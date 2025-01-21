package com.cubes_and_mods.web.сontrollers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.model.UnpackPayload;
import com.cubes_and_mods.web.web_clients.game.RootClient;
import com.cubes_and_mods.web.web_clients.res.MineserverClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/root")
public class RootController {
	
	@Autowired
	RootClient rootClient;
	
	@Autowired
	MineserverClient msClient;
	
	@PostMapping("/launch")
	public Mono<ResponseEntity<Void>> Launch(@RequestBody int id, HttpSession session){
			
		return rootClient.launch(id);
	}

	@PostMapping("/is_alive")
	public Mono<ResponseEntity<Boolean>> isAlive(@RequestBody int id){
		return rootClient.isAlive(id);
	}
	
	@PostMapping("/unpack")
	public Mono<ResponseEntity<Void>> unpack(@RequestBody UnpackPayload payload) {
		System.out.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
		return rootClient.unpackServer(payload);
	}
	
	@PostMapping("/isInstalled")
	public Mono<ResponseEntity<Boolean>> mineserverInstalled(@RequestBody int id){
		return rootClient.mineserverInstalled(id);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Void>> deleteServer(@RequestBody int id){
		return rootClient.deleteServer(id);
	}
	
	@PostMapping("/kill")
	public Mono<ResponseEntity<Void>> killServer(@RequestBody int id){
		return rootClient.kill(id);
	}
}
