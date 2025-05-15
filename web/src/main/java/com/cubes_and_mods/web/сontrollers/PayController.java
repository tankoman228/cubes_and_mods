package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.model.KeyRequest;
import com.cubes_and_mods.web.dto.Order;
import com.cubes_and_mods.web.web_clients.BuyClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Autowired
	BuyClient buyClient;
	
	@PostMapping("/request")
	public Mono<ResponseEntity<String>> request(@RequestBody Order order, HttpSession session) {
		String token = (String) session.getAttribute("email");
		System.out.println(token);
		return buyClient.request(order, (String) session.getAttribute("email"))
		.onErrorResume(error -> {
			System.err.println(error.getMessage());
			System.err.println(error.getCause());
			error.printStackTrace();
			return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage()));
		});
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
