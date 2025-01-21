package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import reactor.core.publisher.Mono;

@Service
public class RootClient {
	
    private WebClient webClient;

    @Autowired
    ProxyConfig ProxyConfig;
    
    @Autowired
    ServiceAddressKeeper Ips;
    
    public RootClient() {
    	
        this.webClient = WebClient.builder()
        		.build();
    }
    
    public Mono<ResponseEntity<Void>> launch(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/launch")
                        .bodyValue(id)
                        .retrieve()
                        .toEntity(Void.class)
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

    public Mono<ResponseEntity<Void>> kill(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/kill")
                        .bodyValue(id)
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }

    public Mono<ResponseEntity<Boolean>> isAlive(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/is_alive")
                        .bodyValue(id)
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e)));
    }

    public Mono<ResponseEntity<Void>> unpackServer(UnpackPayload payload) {
        System.err.println("unpacking version client start");
        return Ips.getIp(payload.id_mineserver)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/unpack_server")
                        .bodyValue(payload)
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }

    public Mono<ResponseEntity<Boolean>> mineserverInstalled(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/mineserver_installed/" + id)
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e)));
    }

    public Mono<ResponseEntity<Void>> deleteServer(int id) {
        return Ips.getIp(id)
                .flatMap(ip -> webClient.post()
                        .uri(ip + "/delete_server/" + id)
                        .retrieve()
                        .toEntity(Void.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }
}
