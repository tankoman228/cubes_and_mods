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
import com.cubes_and_mods.order.security.ServiceClientSession;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins;
import com.cubes_and_mods.order.security.annotations.CheckUserSession;
import com.cubes_and_mods.order.security.annotations.AllowedOrigins.MService;
import com.cubes_and_mods.order.security.session_checkers.SessionCheckerOrder;
import com.cubes_and_mods.order.service.ServiceOrder;

/**
 * Stores orders and its' statuses. Uses ServicePay for validation and calling API in "res" microservice
 * */
@RestController
@RequestMapping("/orders")
public class OrdersController {

	@Autowired
	private OrderRepos orderRepos;

	@Autowired
	private ServiceOrder serviceOrder;

	@Autowired
	private ServiceClientSession sessionClient;


	@PostMapping("/make_order")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession(validator = SessionCheckerOrder.class)
	public ResponseEntity<String> make_order(@RequestBody ProtectedRequest<Order> request) { 

		var result = serviceOrder.MakeOrder(request.data, sessionClient.getSession(request.userSession).client);
		if (result == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/confirm")
	@AllowedOrigins(MService.PAY)
	public ResponseEntity<Void> confirm(@RequestBody ProtectedRequest<String> request) { 
		var result = serviceOrder.AcceptOrder(request.data);
		return ResponseEntity.status(result).build();
	}
	
	@PutMapping("/cancel")
	@AllowedOrigins({MService.WEB, MService.PAY})
	public ResponseEntity<Void> cancel(@RequestBody ProtectedRequest<String> request) { 
		var result = serviceOrder.CancelOrder(request.data);
		return ResponseEntity.status(result).build();
	}
	
	@PostMapping("/status")
	@AllowedOrigins({MService.WEB, MService.PAY})
	public ResponseEntity<Order> status(@RequestBody ProtectedRequest<String> request) { 
		var order = orderRepos.findById(request.data);
		if (order.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(order.get());
	}
	
	@PostMapping("/statuses")
	@AllowedOrigins(MService.PAY)
	public ResponseEntity<List<Order>> statuses(@RequestBody ProtectedRequest<Void> request) { 
		return ResponseEntity.ok(orderRepos.findAll());
	}
}
