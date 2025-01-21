package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.VersionWithoutArchive;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Service
public class VersionsClient {
	
    @Autowired
    ProxyConfig ProxyConfig;
	
	private String MainUri;
	
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	
    	MainUri = ProxyConfig.getRes() + "/version";
    	
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
