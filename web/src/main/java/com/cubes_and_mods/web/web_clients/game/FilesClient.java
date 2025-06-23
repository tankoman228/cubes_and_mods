package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.Clients.model.FileInfo;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class FilesClient {

    private WebClient webClient;

    private volatile ConcurrentHashMap<Integer, Integer> mapOperationIdToUdMineserver; 
    
    @Autowired
    ServiceAddressKeeper Ips;

    @PostConstruct
    private void INIT() {
    	
        //MainUri += "files";

        if (mapOperationIdToUdMineserver == null)
        	mapOperationIdToUdMineserver = new ConcurrentHashMap<Integer, Integer>();

        this.webClient = WebClient.builder()
        		//.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }
    
    public Mono<ResponseEntity<FileInfo>> files(int id, String token) {
        System.out.println("/files/" + id);
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/files/" + id)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(FileInfo.class)
                        .onErrorResume(e -> {
                            return ErrorHandler.handleError(e);
                        }));
    }
     
    public Mono<ResponseEntity<FileInfo>> file(int id, String path, String token){
        System.out.println("Путь: " + path);
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/files/" + id + "/read")
                        .bodyValue(new ProtectedRequest<String>(path, token))
                        .retrieve()
                        .toEntity(FileInfo.class)
                        .onErrorResume(e -> {
                            return ErrorHandler.handleError(e);
                        }));
    }
    
    public Mono<ResponseEntity<Void>> upload(int id, FileInfo file, String token){
        System.out.println(file == null);
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/files/" + id + "/upload")
                        .bodyValue(new ProtectedRequest<FileInfo>(file, token))
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> {
                            return ErrorHandler.handleError(e);
                        }));
    }
    
    public Mono<ResponseEntity<Void>> delete(int id, String path, String token){
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/files/" + id + "/delete")
                        .bodyValue(new ProtectedRequest<String>(path, token))
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> {
                            System.err.println("ошибка при удалении файла: " + e.getMessage());
                            return ErrorHandler.handleError(e);
                        }));
    }

    public Mono<ResponseEntity<Void>> copy(int id, String[] paths, String token){
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.put()
                        .uri(ip + "/files/" + id + "/copy")
                        .bodyValue(new ProtectedRequest<String[]>(paths, token))
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> {
                            System.err.println("ошибка при копировании файла: " + e.getMessage());
                            return ErrorHandler.handleError(e);
                        }));
    }

    public Mono<ResponseEntity<Void>> move(int id, String[] paths, String token){
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.put()
                        .uri(ip + "/files/" + id + "/move")
                        .bodyValue(new ProtectedRequest<String[]>(paths, token))
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> {
                            System.err.println("ошибка при перемешении файла: " + e.getMessage());
                            return ErrorHandler.handleError(e);
                        }));
    }
}
