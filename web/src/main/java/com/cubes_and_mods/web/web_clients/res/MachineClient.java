package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MachineClient {
	
    //@Autowired
    //ProxyConfig ProxyConfig;
	
    private WebClient webClient;

	@Value("${servers-address}")
    private String MainUri;
    
    @PostConstruct
    private void INIT() {
    	
    	MainUri += "servers";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
				.clientConnector(ClientConnectorForKey.getForKey("servers"))
        		.build();
    }
    
    
    public Flux<Server> getAllMachines(){ 
    	return webClient.post()
	    		.uri("/all")
				.bodyValue(new ProtectedRequest<Void>(null, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт))
	            .retrieve()
	            .bodyToFlux(Server.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Server>> getMachineById(int id){
    	return webClient.post()
	    		.uri("/" + id)
				.bodyValue(new ProtectedRequest<Void>(null, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт
	            .retrieve()
	            .toEntity(Server.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
}
