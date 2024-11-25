package com.cubes_and_mods.web.Clients;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Mono;

@Service
public class BuyClient {
	
    private WebClient webClient;

    public BuyClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8082/pay")
        		.build();
    }
    
    public Mono<Tariff> request(){
    	return webClient.post()
            .uri("/request")
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> confirm(){
    	return webClient.post()
            .uri("/confirm")
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> decline(){
    	return webClient.post()
            .uri("/decline")
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> prolongRequest(){
    	return webClient.post()
            .uri("/prolong_request")
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> returnBuy(){
    	return webClient.post()
            .uri("/return")
            .retrieve()
            .bodyToMono(Tariff.class);
    }
}
