package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubes_and_mods.web.Clients.MachineClient;
import com.cubes_and_mods.web.DB.Machine;
import com.cubes_and_mods.web.DB.Tariff;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/machines")
public class MachineController {
	
	@Autowired
	MachineClient machineClient;
	
	@GetMapping
	public Flux<Machine> getAllMachines(){
		return machineClient.getAllMachines();
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Machine>> getMachineById(@PathVariable int id){
		return machineClient.getMachineById(id);
	}
	
	@PostMapping("/whichCan")
	public Flux<Machine> getWhichCanMachines(@RequestBody Tariff tariff){
		return machineClient.getWhichCanMachines(tariff);
	}
	
	@PostMapping("/reserve")
	public Mono<ResponseEntity<Void>> reserve(@RequestParam int id, @RequestBody Tariff tariff){
		return machineClient.reserve(id, tariff);
	}
	
	@PostMapping("/free")
	public Mono<ResponseEntity<Void>> free(@RequestParam int id, @RequestBody Tariff tariff){
		return machineClient.free(id, tariff);
	}
	
	@PostMapping("/recount")
	public Mono<ResponseEntity<Void>> recount(@RequestParam int id){
		return machineClient.recount(id);
	}
}
