package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.BackupRequest;
import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
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
    	
        //TODO адаптировать получение ключа к работе с несколькими хостами
        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
        		.build();
        
        if (mapOperationIdToUdMineserver == null)
        	mapOperationIdToUdMineserver = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public Flux<Backup> getBackupsForMineServer(int id) {
        return Ips.getIp(id)
                .flatMapMany(ip -> webClient.post()
                        .uri(ip + "/backup/{id}/all", id)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .bodyToFlux(Backup.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorFlux(e)));
    }

    public Mono<ResponseEntity<Integer>> create(int id_server, BackupRequest backupRequest) {
        return Ips.getIp(id_server)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/{id_server}")
                        .bodyValue(new ProtectedRequest<BackupRequest>(backupRequest))
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_server);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> rollback(int id_host, long id_back) {
        return Ips.getIp(id_host)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/{id_host}/rollback/{id_back}", id_host, id_back)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(Integer.class)
                        .doOnSuccess(response -> {
                            mapOperationIdToUdMineserver.put(response.getBody(), id_host);
                        })
                        .onErrorResume(e -> ErrorHandler.handleErrorInt(e)));
    }

    public Mono<ResponseEntity<Integer>> delete(int id_server, int id_back) {
        return Ips.getIp(id_server)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/backup/{id_server}/{id_back}", id_server, id_back)
                        .bodyValue(new ProtectedRequest<Void>())
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
                        .uri(ip + "/backup/get_status/{id_task}", id_task)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorString(e)));
    }
}
