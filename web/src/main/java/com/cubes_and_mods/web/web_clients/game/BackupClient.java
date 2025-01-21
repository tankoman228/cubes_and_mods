package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Backup;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BackupClient {
	
    private WebClient webClient;
    
    /**
     * To get ID of mineserver by operation id to get IP of game server to send request about status
     * */
    private volatile ConcurrentHashMap<Integer, Integer> mapOperationIdToUdMineserver; 
    
    @Autowired
    ProxyConfig ProxyConfig;
    
    @Autowired
    ServiceAddressKeeper Ips;
    
    public BackupClient() {
    	
        this.webClient = WebClient.builder()
        		.build();
        
        if (mapOperationIdToUdMineserver == null)
        	mapOperationIdToUdMineserver = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public Flux<Backup> getBackupsForMineServer(int id) {
        return Ips.getIp(id)
                .flatMapMany(ip -> webClient.post()
                        .uri(ip + "/backup/all/{id}", id)
                        .retrieve()
                        .bodyToFlux(Backup.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorFlux(e)));
    }

    public Mono<ResponseEntity<Integer>> create(int id_server, String name) {
        return Ips.getIp(id_server)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/create/" + id_server)
                        .bodyValue(name)
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_server);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> rollback(int id_server, long id_backup) {
        return Ips.getIp(id_server)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/rollback/" + id_server + "/" + id_backup)
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_server);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> delete(int id_server, Backup backup) {
        return Ips.getIp(id_server)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/delete/" + id_server)
                        .bodyValue(backup)
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_server);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<String>> get_status(String id_task) {
        int serverId = mapOperationIdToUdMineserver.get(Integer.parseInt(id_task));
        return Ips.getIp(serverId)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/get_status")
                        .bodyValue(id_task)
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorString(e)));
    }

    
    
}
