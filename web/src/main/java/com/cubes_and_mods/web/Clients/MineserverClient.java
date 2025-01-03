package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Flux;

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
	            return Flux.error(new ResponseStatusException(
	            		HttpStatus.INTERNAL_SERVER_ERROR, 
	            		"Ошибка при получении серверов"));
	            });
    }
}
