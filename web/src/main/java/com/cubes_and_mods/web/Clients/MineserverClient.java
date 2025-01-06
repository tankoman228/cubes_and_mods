package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MineserverClient {
    private WebClient webClient;

    public MineserverClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8089/res/mineservers")
        		.build();
    }
    
    public Flux<Mineserver> getAllMineServers(int id){ 
    	return webClient.post()
	    		.uri("/all/" + id)
	            .retrieve()
	            .bodyToFlux(Mineserver.class)
	            .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();
                        return Flux.error(new ResponseStatusException(statusCode, e.getMessage()));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Flux.error(new ResponseStatusException(
        	            		HttpStatus.INTERNAL_SERVER_ERROR, 
        	            		"Ошибка при получении серверов"));
                    }
	            });
    }
}
