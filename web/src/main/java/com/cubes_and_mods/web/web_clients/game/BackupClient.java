package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BackupClient {

    private WebClient webClient;
    
    private volatile ConcurrentHashMap<Integer, Integer> mapOperationIdToUdMineserver; 
    
    @Autowired
    ServiceAddressKeeper Ips;

    @PostConstruct
    private void INIT() {

    	if (mapOperationIdToUdMineserver == null)
        	mapOperationIdToUdMineserver = new ConcurrentHashMap<Integer, Integer>();

        //MainUri += "backup";
    	
        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }
    
    public Flux<Backup> getBackupsForMineServer(int id, String token) {
        return Ips.getIp(id, token).flatMapMany(ip -> 
                    webClient.post()
                        .uri(ip + "/backup/" + id + "/all")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .bodyToFlux(Backup.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorFlux(e)));
    }

    public Mono<ResponseEntity<Integer>> create(int id, String name, String token) {
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/backup/" + id)
                        .bodyValue(new ProtectedRequest<String>(name, token))
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> rollback(int id, long id_back, String token) {
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.put()
                        .uri(ip + "/backup/" + id + "/rollback/" + id_back)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> delete(int id_server, int id_back, String token) {
        return Ips.getIp(id_server, token).flatMap(ip -> 
                    webClient.put()
                        .uri(ip + "/backup/" + id_server + "/" + id_back)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_server);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<String>> get_status(String id_task, String token) {
        int serverId = mapOperationIdToUdMineserver.get(Integer.parseInt(id_task));
        return Ips.getIp(serverId, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/backup/get_status/" + id_task)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorString(e)));
    }
}
