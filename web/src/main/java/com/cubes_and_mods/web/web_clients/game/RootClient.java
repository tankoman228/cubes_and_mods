package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.Clients.model.UnpackPayload;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import reactor.core.publisher.Mono;

@Service
public class RootClient {
	
    private WebClient webClient;
    
    @Autowired
    ServiceAddressKeeper Ips;
    
    public RootClient() {
    	
        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
        		.build();
    }
    
    public Mono<ResponseEntity<String>> launch(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/game/{id}/launch")
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(WebClientResponseException.class, e -> {
                            e.printStackTrace();
                            System.out.println("ID сервера: " + id);
                            return Mono.just(new ResponseEntity<>(e.getStatusCode()));
                        })
                        .onErrorResume(e -> {
                            e.printStackTrace();
                            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                        }));
    }

    public Mono<ResponseEntity<String>> kill(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/game/{id}/kill", id)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }

    public Mono<ResponseEntity<Boolean>> isAlive(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/game/{id}/is_alive", id)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e)));
    }

    public Mono<ResponseEntity<String>> unpackServer(UnpackPayload payload) {
        System.err.println("unpacking version client start");
        return Ips.getIp(payload.id_mineserver)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/game/{payload.id_mineserver}/unpack_by_version/{payload.id_version}", 
                            payload.id_mineserver, payload.id_version)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }

    public Mono<ResponseEntity<Boolean>> mineserverInstalled(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/{id}/installed", id)
                        .bodyValue(new ProtectedRequest<Void>())
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e)));
    }

    public Mono<ResponseEntity<String>> deleteServer(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/{id}/uninstall", id)
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }
}
