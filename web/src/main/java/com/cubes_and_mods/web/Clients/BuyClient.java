package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.Clients.model.ORDER_REQUEST;
import com.cubes_and_mods.web.Clients.model.Order;

import reactor.core.publisher.Mono;

@Service
public class BuyClient {
	
	/*@Value("${services.buy.uri}")
	private String MainUri;*/
	
	private String MainUri = "http://localhost:8089/buy";
	
    private WebClient webClient;

    public BuyClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri + "/pay")
        		.build();
    }
    
    public Mono<ResponseEntity<String>> request(ORDER_REQUEST body){
    	return webClient.post()
            .uri("/make_order")
            .bodyValue(body)
            .retrieve()
            .toEntity(String.class)
	        .onErrorResume(e -> {
	        	return handleErrorString(e);
	        });
    }
    
    public Mono<ResponseEntity<Void>> confirm(String key){
    	return webClient.post()
            .uri("/confirm")
            .bodyValue(key)
            .retrieve()
            .toEntity(Void.class)
	        .onErrorResume(e -> {
	        	return handleErrorVoid(e);
	        });
    }
    
    public Mono<ResponseEntity<Void>> decline(String key){
    	return webClient.post()
            .uri("/decline")
            .bodyValue(key)
            .retrieve()
            .toEntity(Void.class)
	        .onErrorResume(e -> {
	        	return handleErrorVoid(e);
	        });
    }
    
    public Mono<ResponseEntity<Order>> status(String key){
    	return webClient.post()
            .uri("/status")
            .bodyValue(key)
            .retrieve()
            .toEntity(Order.class)
	        .onErrorResume(e -> {
	        	return handleErrorOrder(e);
	        });
    }
    
    public Mono<ResponseEntity<Void>> returnBuy(){
    	return webClient.post()
            .uri("/return_money")
            .retrieve()
            .toEntity(Void.class)
	        .onErrorResume(e -> {
	            return handleErrorVoid(e);
	        });
    }
    
    private Mono<ResponseEntity<Void>> handleErrorVoid(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(null));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
        }
    }
    
    private Mono<ResponseEntity<Order>> handleErrorOrder(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(null));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
        }
    }
    
    private Mono<ResponseEntity<String>> handleErrorString(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(e.getMessage()));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }
    }
}
