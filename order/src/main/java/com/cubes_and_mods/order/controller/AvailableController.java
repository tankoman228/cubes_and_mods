package com.cubes_and_mods.order.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.order.jpa.Server;
import com.cubes_and_mods.order.jpa.Tariff;
import com.cubes_and_mods.order.jpa.repos.ServerRepos;
import com.cubes_and_mods.order.jpa.repos.TariffRepos;
import com.cubes_and_mods.order.security.ProtectedRequest;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins.MService;

@RestController
@RequestMapping("/available")
public class AvailableController {
	
	/* P.S. Логика слишком простая, чтобы выносить в несколько слоёв */

	@Autowired
	private TariffRepos tariffRepos;

	@Autowired
	private ServerRepos serverRepos;

	//Эта хуйня тоже теперь мозг ебет
	@PostMapping("/tariffs")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<Tariff>> tariffs(@RequestBody ProtectedRequest<Void> request) { 
		
		// Перебираем тарифы, смотрим, чтобы свободного было достаточно для указанного тарифа хотя бы на одном сервере
		var tariffs = new ArrayList<Tariff>();
		for (var tariff: tariffRepos.findAll()) {
			boolean ok = false;
			for (var server: serverRepos.findAll()) {
				if (
					server.getCpuThreadsFree() >= tariff.getCpuThreads() && 
					server.getRamFree() >= tariff.getRam() && 
					server.getMemoryFree() >= tariff.getMemoryLimit()) {

					ok = true;
					break;
				}
			}
			if (ok) {
				tariffs.add(tariff);
			}
		}

		return ResponseEntity.ok(tariffs);
	}
	
	@PostMapping("/tariff/{id}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Tariff> tariffsByID(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 
		
		var tariff = tariffRepos.findById(id);
		if (tariff.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(tariff.get());
	}

	@PostMapping("/servers_for_tariff/{id}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<List<Server>> servers_for_tariff(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 

		var tariff = tariffRepos.findById(id); 
		if (tariff.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		// Перебираем сервера, смотрим, чтобы свободного было достаточно для указанного тарифа
		var servers = new ArrayList<Server>();
		for (var server: serverRepos.findAll()) {
			
			if (server.getCpuThreadsFree() < tariff.get().getCpuThreads()) continue;
			if (server.getRamFree() < tariff.get().getRam()) continue;
			if (server.getMemoryFree() < tariff.get().getMemoryLimit()) continue;

			servers.add(server);
		}

		return ResponseEntity.ok(servers);
	}
}
