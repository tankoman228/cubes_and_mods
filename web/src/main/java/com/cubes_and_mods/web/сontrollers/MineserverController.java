package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.web_clients.res.MineserverClient;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcserver")
public class MineserverController {

	@Autowired
	MineserverClient mineserverClient;
	
	@GetMapping("/my")
	public Flux<Mineserver> getMyMCServers(@RequestParam int id)
	{
		return mineserverClient.getAllMineServers(id);
	}
}
