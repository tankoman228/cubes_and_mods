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
		String token = (String) session.getAttribute("email");	
		return rootClient.launch(id, token);
	}

	@PostMapping("/is_alive")
	public Mono<ResponseEntity<Boolean>> isAlive(@RequestBody int id, HttpSession session){
		String token = (String) session.getAttribute("email");
		return rootClient.isAlive(id, token);
	}
	
	@PostMapping("/unpack")
	public Mono<ResponseEntity<String>> unpack(@RequestBody UnpackPayload payload, HttpSession session) {
		System.out.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
		String token = (String) session.getAttribute("email");
		return rootClient.unpackServer(payload, token);
	}
	
	@PostMapping("/isInstalled")
	public Mono<ResponseEntity<Boolean>> mineserverInstalled(@RequestBody int id, HttpSession session){
		System.err.println("Биба и боба c ID = " + id);
		String token = (String) session.getAttribute("email");
		return rootClient.mineserverInstalled(id, token);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<String>> deleteServer(@RequestBody int id, HttpSession session){
		System.out.println("ID удаляемого сервера: " + id);
		String token = (String) session.getAttribute("email");
		return rootClient.deleteServer(id, token);
	}
	
	@PostMapping("/kill")
	public Mono<ResponseEntity<String>> killServer(@RequestBody int id, HttpSession session){
		System.out.println("Команда на остановку: " + id);
		String token = (String) session.getAttribute("email");
		return rootClient.kill(id, token);
	}
}
