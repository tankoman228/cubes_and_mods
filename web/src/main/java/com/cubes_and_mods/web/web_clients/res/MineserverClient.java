package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
//import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.dto.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MineserverClient {
	
    private WebClient webClient;
    
	@Value("${servers-address}")
    private String MainUri;

    @PostConstruct
    private void INIT() {
    	
    	MainUri += "hosts";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("servers"))
        		.build();
    }
    
    public Flux<Host> getAllMineServers(int id){ 
    	return webClient.post()
	    		.uri("/of_user/" + id)
                .bodyValue(new ProtectedRequest<Void>(null, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт
	            .retrieve()
	            .bodyToFlux(Host.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Host>> getByIdMineserver(int id){ 
    	return webClient.post()
	    		.uri("/" + id)
                .bodyValue(new ProtectedRequest<Void>(null, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт
	            .retrieve()
	            .toEntity(Host.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
	            });
    }

    public Mono<ResponseEntity<Void>> edit(int id, Host host){ 
    	return webClient.put()
	    		.uri("/edit/" + id)
                .bodyValue(new ProtectedRequest<Host>(host, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт
	            .retrieve()
	            .toEntity(Void.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
	            });
    }

    public Mono<ResponseEntity<Void>> share(int id, String email){ 
    	return webClient.post()
	    		.uri("/edit/" + id)
                .bodyValue(new ProtectedRequest<String>(email, "Сессии токен")) // TODO: ДОБАВЬ СЕССИЮ, 401 иначе вернёт
	            .retrieve()
	            .toEntity(Void.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
	            });
    }
}
