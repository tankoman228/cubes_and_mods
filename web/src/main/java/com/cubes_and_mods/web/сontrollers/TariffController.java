package com.cubes_and_mods.web.сontrollers;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.dto.*;
import com.cubes_and_mods.web.web_clients.TariffClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/getTariffs")
public class TariffController {

	@Autowired
	TariffClient tariffClient;
	
	@GetMapping("/")
	public Flux<Tariff> getAllTariffs(Model model, HttpSession session) {
		String token = (String) session.getAttribute("email");
	    return tariffClient.getAllTariffs(token)
	    		.filter(tariff -> tariff.getEnabled() != null && tariff.getEnabled())
	    		.collectList()
    	        .flatMapMany(tariffs -> {
    	            tariffs.sort(Comparator.comparing(Tariff::getCostRub));
    	            return Flux.fromIterable(tariffs); 
    	        });
	}
	
	@GetMapping("/getById")
	public Mono<ResponseEntity<Tariff>> getTariffByID(Model model, @RequestParam int TariffId, HttpSession session) {
		String token = (String) session.getAttribute("email");
		return tariffClient.getTariffById(TariffId, token);
	}

	@GetMapping("/AvalibleServers")
	public Flux<Server> getAvalibleServers(Model model, @RequestParam int TariffId, HttpSession session) {
		String token = (String) session.getAttribute("email");
		return tariffClient.getServersForTariffs(TariffId, token);
	}
}
