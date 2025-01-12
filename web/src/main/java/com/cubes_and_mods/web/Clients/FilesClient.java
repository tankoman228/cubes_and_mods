package com.cubes_and_mods.web.Clients;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.FileInfo;

import reactor.core.publisher.Mono;

@Service
public class FilesClient {
    private WebClient webClient;

    private String MainUri = ProxyConfig.getGame() + "/files";
    
    public FilesClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Mono<ResponseEntity<FileInfo>> files(int id_server){
    	System.out.println(MainUri + "/" + id_server);
        return webClient.post()
                .uri("/" + id_server)
                .retrieve()
                .toEntity(FileInfo.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
     
    public Mono<ResponseEntity<FileInfo>> file(int id_server, String path){
        return webClient.post()
                .uri("/read/" + id_server)
                .bodyValue(path)
                .retrieve()
                .toEntity(FileInfo.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
    
    public Mono<ResponseEntity<Void>> upload(int id_server, FileInfo file){
        return webClient.post()
                .uri("/upload/" + id_server)
                .bodyValue(file)
                .retrieve()
                .toEntity(Void.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
    
    public Mono<ResponseEntity<Void>> delete(int id_server, String path){
        return webClient.post()
                .uri("/delete/" + id_server)
                .bodyValue(path)
                .retrieve()
                .toEntity(Void.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
}
