package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.UnpackPayload;
import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Mono;

@Service
public class RootClient {
    private WebClient webClient;

    private String MainUri = ProxyConfig.getGame();
    
    public RootClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Mono<ResponseEntity<Void>> Launch(int id) {
        try {
            Mono<ResponseEntity<Void>> responseMono = webClient.post()
                .uri("/launch")
                .bodyValue(id)
                .retrieve()
                .toEntity(Void.class);

            ResponseEntity<Void> response = responseMono.block();

            if (response != null) {
                return Mono.just(response);
            } else {
                return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        }catch(WebClientResponseException e) { 
        	e.printStackTrace();
        	System.out.println("ID сервера: "+id);
            return Mono.just(new ResponseEntity<>(e.getStatusCode()));
        }
        catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    public Mono<ResponseEntity<Void>> kill(int id){
    	return webClient.post()
    			.uri("/kill")
    			.bodyValue(id)
		        .retrieve()
		        .toEntity(Void.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleError(e);
		        });
    }
    
    public Mono<ResponseEntity<Boolean>> isAlive(int id){
        return webClient.post()
                .uri("/is_alive")
                .bodyValue(id)
                .retrieve()
                .toEntity(Boolean.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleErrorBool(e);
                });
    }
    
    public Mono<ResponseEntity<Void>> unpackServer(UnpackPayload payload){
    	System.err.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
    	return webClient.post()
    			.uri("/unpack_server")
    			.bodyValue(payload)
		        .retrieve()
		        .toEntity(Void.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleError(e);
		        });
    }
    
    public Mono<ResponseEntity<Boolean>> mineserverInstalled(int id){
    	return webClient.post()
    			.uri("/mineserver_installed/"+id)
		        .retrieve()
		        .toEntity(Boolean.class)
		        .onErrorResume(e -> {
		            return ErrorHandler.handleErrorBool(e);
		        });
    }
    
    public Mono<ResponseEntity<Void>> delete_server(int id){
    	return webClient.post()
    			.uri("/delete_server/"+id)
		        .retrieve()
		        .toEntity(Void.class)
		        .onErrorResume(e -> {
		            return ErrorHandler.handleError(e);
		        });
    }
}
