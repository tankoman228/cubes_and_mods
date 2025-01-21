package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.DB.Machine;
import com.cubes_and_mods.web.DB.Tariff;
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
	
	// Удалил запросы, ты дурак проксировать админские запросы клиенту? Простых проверок ему хватит, а остальным взломают за секунду
	
	@PostMapping("/canHandle")
	public Mono<ResponseEntity<Boolean>> canHandle(@RequestParam int id_machine, @RequestParam int id_tariff){
		return machineClient.canHandle(id_machine, id_tariff);
	}
	
	@PostMapping("/canHandleWithNewTariff")
	public Mono<ResponseEntity<Boolean>> canHandleWithNewTariff(@RequestParam int id_mineserver, @RequestParam int id_tariff){
		return machineClient.canHandleWithNewTariff(id_mineserver, id_tariff);
	}
}
