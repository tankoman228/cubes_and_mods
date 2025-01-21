package com.cubes_and_mods.web.сontrollers;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.DB.Tariff;
import com.cubes_and_mods.web.web_clients.TariffClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tariffs")
public class TariffController {

	@Autowired
	TariffClient tariffClient;
	
	@GetMapping("/")
	public Flux<Tariff> getAllTariffs(Model model) {
	    return tariffClient.getAllTariffs()
	    		.filter(tariff -> tariff.getEnabled() != null && tariff.getEnabled())
	    		.collectList()
    	        .flatMapMany(tariffs -> {
    	            tariffs.sort(Comparator.comparing(Tariff::getCostRub));
    	            return Flux.fromIterable(tariffs); 
    	        });
	}
	
	@GetMapping("/getById")
	public Mono<ResponseEntity<Tariff>> getTariffByID(Model model, @RequestParam int TariffId) {
		return tariffClient.getTariffById(TariffId);
	}
}
