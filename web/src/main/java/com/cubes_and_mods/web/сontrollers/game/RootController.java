package com.cubes_and_mods.web.сontrollers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public Mono<ResponseEntity<String>> Launch(@RequestBody int id, HttpSession session){
			
		return rootClient.launch(id);
	}

	@PostMapping("/is_alive")
	public Mono<ResponseEntity<Boolean>> isAlive(@RequestBody int id){
		return rootClient.isAlive(id);
	}
	
	@PostMapping("/unpack")
	public Mono<ResponseEntity<String>> unpack(@RequestBody UnpackPayload payload) {
		System.out.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
		return rootClient.unpackServer(payload);
	}
	
	@PostMapping("/isInstalled")
	public Mono<ResponseEntity<Boolean>> mineserverInstalled(@RequestBody int id){
		System.err.println("Биба и боба c ID = " + id);
		return rootClient.mineserverInstalled(id);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<String>> deleteServer(@RequestBody int id){
		System.out.println("ID удаляемого сервера: " + id);
		return rootClient.deleteServer(id);
	}
	
	@PostMapping("/kill")
	public Mono<ResponseEntity<String>> killServer(@RequestBody int id){
		System.out.println("Команда на лстановку: " + id);
		return rootClient.kill(id);
	}
}
