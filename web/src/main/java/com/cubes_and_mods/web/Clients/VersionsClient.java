package com.cubes_and_mods.web.Clients;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.VersionWithoutArchive;

import reactor.core.publisher.Flux;

@Service
public class VersionsClient {
	private String MainUri = ProxyConfig.getRes() + "/version";
	
    private WebClient webClient;

    public VersionsClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<VersionWithoutArchive> getAll(){
    	return webClient.get()
    			.uri("/all")
	            .retrieve()
	            .bodyToFlux(VersionWithoutArchive.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
}
