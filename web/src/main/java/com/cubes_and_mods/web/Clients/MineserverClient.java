package com.cubes_and_mods.web.Clients;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Mineserver;
import reactor.core.publisher.Flux;

@Service
public class MineserverClient {
    private WebClient webClient;
    
    private String MainUri = ProxyConfig.getRes() + "/mineservers";

    public MineserverClient() {
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
}
