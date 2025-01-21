package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MineserverClient {
	
    private WebClient webClient;
    
    @Autowired
    ProxyConfig ProxyConfig;
    
    private String MainUri;

    @PostConstruct
    private void INIT() {
    	
    	MainUri = ProxyConfig.getRes() + "/mineservers";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<Mineserver> getAllMineServers(int id){ 
    	return webClient.post()
	    		.uri("/all/" + id)
	            .retrieve()
	            .bodyToFlux(Mineserver.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Mineserver>> getByIdMineserver(int id){ 
    	return webClient.post()
	    		.uri("/" + id)
	            .retrieve()
	            .toEntity(Mineserver.class);
    }
}
