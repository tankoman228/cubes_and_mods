package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubes_and_mods.web.Clients.TariffClient;
import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/tariffs")
public class TariffController {

	@Autowired
	TariffClient tariffClient;
	
	@GetMapping("/")
	public Flux<Tariff> getAllTariffs(Model model) {
		return tariffClient.getAllTariffs();
	}
	
	@GetMapping("/getById")
	public Mono<ResponseEntity<Tariff>> getTariffByID(Model model, @RequestParam int TariffId) {
		return tariffClient.getTariffById(TariffId);
	}
	
}