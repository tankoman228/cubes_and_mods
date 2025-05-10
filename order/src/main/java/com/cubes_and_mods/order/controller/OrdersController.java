package com.cubes_and_mods.order.controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.order.jpa.Order;
import com.cubes_and_mods.order.jpa.repos.ClientRepos;
import com.cubes_and_mods.order.jpa.repos.OrderRepos;
import com.cubes_and_mods.order.jpa.repos.ServerRepos;
import com.cubes_and_mods.order.jpa.repos.TariffRepos;
import com.cubes_and_mods.order.security.ProtectedRequest;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins.MService;

/**
 * Stores orders and its' statuses. Uses ServicePay for validation and calling API in "res" microservice
 * */
@RestController
@RequestMapping("/orders")
public class OrdersController {

	@Autowired
	private OrderRepos orderRepos;

	@Autowired
	private ServerRepos serverRepos;

	@Autowired
	private TariffRepos tariffRepos;

	@Autowired
	private ClientRepos clientRepos;

	private final Object lock = new Object();

	private final SecureRandom random = new SecureRandom();

	@PostMapping("/make_order")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<String> make_order(@RequestBody ProtectedRequest<Order> request) { 

		// TODO: проверка валидности заказа, есть ли вообще ресурсы на это, как в контроллере для Available

		var orderCode = "eee" + random.nextInt();
		synchronized (lock) {
			
			var order = request.data;
			var tariff = tariffRepos.findById(request.data.getIdTariff());
			var serverOptional = serverRepos.findById(request.data.getIdServer());

			if (serverOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			if (tariff.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

			var server = serverOptional.get();
			server.setCpuThreadsFree((short) (server.getCpuThreadsFree() - tariff.get().getCpuThreads()));
			server.setRamFree((short) (server.getRamFree() - tariff.get().getRam()));
			server.setMemoryFree(server.getMemoryFree() - tariff.get().getMemoryLimit());

			order.setConfirmed(false);
			order.setClosedAt(null);
			order.setMadeAt(LocalDateTime.now());
			order.setClient(clientRepos.findAll().get(0)); // TODO: получить клиента из БД

			order.setCode(orderCode); // TODO: Сделать нормальный код заказа

			orderRepos.save(order);
			serverRepos.save(server);

			orderRepos.flush();
			serverRepos.flush();
		}

		return ResponseEntity.ok(orderCode);
	}
	
	@PutMapping("/confirm/{code}")
	@AllowedOrigins(MService.PAY)
	public ResponseEntity<Void> confirm(@RequestBody ProtectedRequest<Void> request, @PathVariable String code) { 
		
		synchronized (lock) {

			var orderOptional = orderRepos.findById(code);
			if (orderOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			var order = orderOptional.get();

			if (order.getClosedAt() != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

			order.setConfirmed(true);
			order.setClosedAt(LocalDateTime.now());

			// TODO: создать игровой сервер

			orderRepos.save(order);
			orderRepos.flush();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PutMapping("/cancel/{code}")
	@AllowedOrigins({MService.WEB, MService.PAY})
	public ResponseEntity<Void> cancel(@RequestBody ProtectedRequest<Void> request, @PathVariable String code) { 

		synchronized (lock) {
			
			var orderOptional = orderRepos.findById(code);
			if (orderOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			var order = orderOptional.get();

			if (order.getClosedAt() != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

			order.setConfirmed(false);
			order.setClosedAt(LocalDateTime.now());

			var server = serverRepos.findById(order.getIdServer()).get();
			var tariff = tariffRepos.findById(order.getIdTariff());

			server.setCpuThreadsFree((short) (server.getCpuThreadsFree() - tariff.get().getCpuThreads()));
			server.setRamFree((short) (server.getRamFree() - tariff.get().getRam()));
			server.setMemoryFree(server.getMemoryFree() - tariff.get().getMemoryLimit());

			serverRepos.save(server);
			orderRepos.save(order);

			orderRepos.flush();
			serverRepos.flush();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/status/{code}")
	@AllowedOrigins({MService.WEB, MService.PAY})
	public ResponseEntity<Order> status(@RequestBody ProtectedRequest<Void> request, @PathVariable String code) { 
		var order = orderRepos.findById(code);
		if (order.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(order.get());
	}
	
	@PostMapping("/statuses")
	@AllowedOrigins(MService.PAY)
	public ResponseEntity<List<Order>> statuses(@RequestBody ProtectedRequest<Void> request) { 
		return ResponseEntity.ok(orderRepos.findAll());
	}
}
