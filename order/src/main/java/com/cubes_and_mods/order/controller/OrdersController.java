package com.cubes_and_mods.order.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.order.service_repos.Order;
import com.cubes_and_mods.order.service_repos.ServicePay;
import com.cubes_and_mods.order.service_repos.ServiceTariff;

/**
 * Stores orders and its' statuses. Uses ServicePay for validation and calling API in "res" microservice
 * */
@RestController
@RequestMapping("/orders")
public class OrdersController {

	
	@PostMapping("/make_order")
	public ResponseEntity<Void> make_order(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PutMapping("/confirm/{code}")
	public ResponseEntity<Void> confirm(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@PutMapping("/cancel/{code}")
	public ResponseEntity<Void> cancel(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/status/{code}")
	public ResponseEntity<Void> status(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/statuses")
	public ResponseEntity<Void> statuses(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("/")
	public ResponseEntity<Void> orders(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	/*
	@Autowired
	ServiceTariff tariffs;
	
	@Autowired
	ServicePay service;
	
	// String (key) is code that /make_order returns
	private static volatile ConcurrentHashMap<String,Order> orders;
	
	
	// Order for extending time or changing tariff
	@PostMapping("/make_order")
	public ResponseEntity<String> request(@RequestBody ORDER_REQUEST body) {
			
		try {		
			if (orders == null) {
				orders = new ConcurrentHashMap<String, Order>(); // autocreate (bicycle)
			}		
		
			if (body.mineserver == null)
				return new ResponseEntity<String>("dd", HttpStatus.I_AM_A_TEAPOT);
			
			var order = service.MakeOrder(body.mineserver, body.newTariff);
			var key = String.valueOf(body.hashCode()) + body.mineserver.getName();
			orders.put(key, order);			
			
			// Expired await thread
			new Thread(() -> {
				try {
					while(true) {		
						
						Thread.sleep(1000);	
						
						if (order.IsExpired()) {
							decline(key);
							System.out.println("ORDER EXPIRED");
							break;
						}
						else if (order.IsAccepted) {
							orders.remove(key);
							System.out.println("ORDER ACCEPTED");
							break;
						}
					}
				} catch (Exception e) {
					System.out.println("ERROR IN THREAD OF ORDER EXPIRE");
					e.printStackTrace();
				}	
			}).start();
			
			System.err.println("Отправлен: " + key);
			return new ResponseEntity<String>(key, HttpStatus.OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}		
	}
	private static class ORDER_REQUEST {
		public Mineserver mineserver; // MUST BE ALWAYS NOT NULL
		public Tariff newTariff; // Contents null if not changing tariff of already existing server
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<Void> confirm(@RequestBody String key) {

		System.err.println("Принят" + key);
		
		if (orders.get(key) == null)
			new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
		
		var order = orders.get(key);
		service.confirm(order);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PostMapping("/decline")
	public ResponseEntity<Void> decline(@RequestBody String key) {
		
		System.err.println("Отменен" + key);
		
		if (!orders.containsKey(key))
			new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
		
		var order = orders.get(key);
		service.decline(order);
		orders.remove(key);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PostMapping("/status")
	public ResponseEntity<Order> getStatus(@RequestBody String key) {
		
		if (!orders.containsKey(key))
			new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<Order>(orders.get(key), HttpStatus.OK);
	}
	
	@GetMapping("/statuses")
	public ResponseEntity<List<Order>> getStatusы() {
		
		return new ResponseEntity<List<Order>>(List.copyOf(orders.values()), HttpStatus.OK);
	}
	
	@PostMapping("/return_money")
	public ResponseEntity<Void> return_() {
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED); //И не нужно)
	}*/
}
