package com.cubes_and_mods.web.Clients;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Backup;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BackupClient {
    private WebClient webClient;

    private String MainUri = ProxyConfig.getGame() + "/backup";
    
    public BackupClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<Backup> getBackupsForMineServer(int id) {
        return webClient.post()
                .uri("/all/{id}", id)
                .retrieve()
                .bodyToFlux(Backup.class)
        		.onErrorResume(e -> {
        			return ErrorHandler.handleErrorFlux(e);
        		});
    }
    
    public Mono<ResponseEntity<Integer>> create(int id_server, String name){
    	return webClient.post()
    			.uri("/create/" + id_server)
    			.bodyValue(name)
		        .retrieve()
		        .toEntity(Integer.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleErrorInt(e);
		        });
    }
    
    public Mono<ResponseEntity<Integer>> rollback(int id_server, long id_backup){
    	return webClient.post()
    			.uri("/rollback/" + id_server + "/" + id_backup)
		        .retrieve()
		        .toEntity(Integer.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleErrorInt(e);
		        });
    }
    
    public Mono<ResponseEntity<Integer>> delete(int id_server, Backup backup){
    	return webClient.post()
    			.uri("/delete/" + id_server)
    			.bodyValue(backup)
		        .retrieve()
		        .toEntity(Integer.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleErrorInt(e);
		        });
    }
    
    public Mono<ResponseEntity<String>> get_status(String id_task){
    	return webClient.post()
    			.uri("/get_status")
    			.bodyValue(id_task)
		        .retrieve()
		        .toEntity(String.class)
		        .onErrorResume(e -> {
		        	return ErrorHandler.handleErrorString(e);
		        });
    }
}
