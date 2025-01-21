package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.model.*;
import com.cubes_and_mods.web.web_clients.BuyClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Autowired
	BuyClient buyClient;
	
	@PostMapping("/request")
	public Mono<ResponseEntity<String>> request(@RequestBody ORDER_REQUEST body) {
		return buyClient.request(body);
	}
	
	@PostMapping("/confirm")
	public Mono<ResponseEntity<Void>> confirm(@RequestBody KeyRequest keyRequest) {
		String key = keyRequest.getKey();
		System.err.println("Ключ поступивший: " + key);
		
		return buyClient.confirm(key);
	}
	
	@PostMapping("/decline")
	public Mono<ResponseEntity<Void>> decline(@RequestBody KeyRequest keyRequest) {
		String key = keyRequest.getKey();
		System.err.println("Ключ поступивший: " + key);
		
		return buyClient.decline(key);
	}
	
	@PostMapping("/status")
	public Mono<ResponseEntity<Order>> status(@RequestBody KeyRequest keyRequest) {
		String key = keyRequest.getKey();
		return buyClient.status(key);
	}
}
