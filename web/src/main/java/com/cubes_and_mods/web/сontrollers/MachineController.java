package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.web_clients.res.MachineClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/machines")
public class MachineController {
	
	@Autowired
	MachineClient machineClient;
	
	@GetMapping
	public Flux<Server> getAllMachines(HttpSession session){
		String token = (String) session.getAttribute("email");
		return machineClient.getAllMachines(token);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Server>> getMachineById(@PathVariable int id, HttpSession session){
		String token = (String) session.getAttribute("email");
		return machineClient.getMachineById(id, token);
	}
}
