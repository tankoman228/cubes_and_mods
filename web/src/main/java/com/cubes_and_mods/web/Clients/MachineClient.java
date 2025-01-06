package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.DB.Tariff;
import com.cubes_and_mods.web.DB.Machine;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MachineClient {
    private WebClient webClient;

    public MachineClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8089/res/machines")
        		.build();
    }
    
    public Flux<Machine> getAllMachines(){ 
    	return webClient.get()
	    		.uri("")
	            .retrieve()
	            .bodyToFlux(Machine.class)
	            .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                        return Flux.error(new ResponseStatusException(statusCode, e.getMessage()));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Flux.error(new ResponseStatusException(
        	            		HttpStatus.INTERNAL_SERVER_ERROR, 
        	            		"Ошибка при получении подходящих серверов"));
                    }
	            });
    }
    
    public Mono<ResponseEntity<Machine>> getMachineById(int id){
    	return webClient.get()
	    		.uri("/" + id)
	            .retrieve()
	            .toEntity(Machine.class)
	            .onErrorResume(e -> {
	                if (e instanceof WebClientResponseException) {
	                    WebClientResponseException webClientResponseException = (WebClientResponseException) e;
	                    HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
	                    return Mono.just(ResponseEntity.status(statusCode).body(null));
	                } else {
	                    System.err.println("Error occurred: " + e.getMessage());
	                    System.err.println("Error occurred: " + e.getStackTrace());
	                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
	                }
	            });
    }
    
    public Flux<Machine> getWhichCanMachines(Tariff tariff){ 
    	return webClient.post()
	    		.uri("/which_can")
	    		.bodyValue(tariff)
	            .retrieve()
	            .bodyToFlux(Machine.class)
	            .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                        return Flux.error(new ResponseStatusException(statusCode, e.getMessage()));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Flux.error(new ResponseStatusException(
        	            		HttpStatus.INTERNAL_SERVER_ERROR, 
        	            		"Ошибка при получении подходящих серверов"));
                    }
	            });
    }
    
    public Mono<ResponseEntity<Void>> reserve(int id, Tariff tariff){
    	return webClient.post()
	    		.uri("/reserve/" + id)
	    		.bodyValue(tariff)
	            .retrieve()
	            .toEntity(Void.class)
	            .onErrorResume(e -> {
	            return handleErrorVoid(e);
	            });
    }
    
    public Mono<ResponseEntity<Void>> free(int id, Tariff tariff){
    	return webClient.post()
	    		.uri("/free/" + id)
	    		.bodyValue(tariff)
	            .retrieve()
	            .toEntity(Void.class)
	            .onErrorResume(e -> {
	            	return handleErrorVoid(e);
	            });
    }
    
    public Mono<ResponseEntity<Void>> recount(int id){
    	return webClient.post()
	    		.uri("/recount")
	    		.bodyValue(id)
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
}
