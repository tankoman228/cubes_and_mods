package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.web_clients.res.MineserverClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mcserver")
public class MineserverController {

	@Autowired
	MineserverClient mineserverClient;
	
	@GetMapping("/my")
	public Flux<Host> getMyMCServers(@RequestParam int id)
	{
		return mineserverClient.getAllMineServers(id);
	}

	@GetMapping("/my/")
	public Mono<ResponseEntity<Host>> getByIdMineserver(@RequestParam int id){
		return mineserverClient.getByIdMineserver(id);
	}

	@PutMapping("/my/edit")
	public Mono<ResponseEntity<Void>> edit(@RequestParam int id, @RequestBody Host host){
		return mineserverClient.edit(id, host);
	}

	@PostMapping("/my/share")
	public Mono<ResponseEntity<Void>> share(@RequestParam int id, @RequestBody String email){
		return mineserverClient.share(id, email);
	}
}
