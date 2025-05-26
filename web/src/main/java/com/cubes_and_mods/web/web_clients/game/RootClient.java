package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.Clients.model.UnpackPayload;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class RootClient {

    private WebClient webClient;

    @Value("${host-address}")
	private String MainUri;

    /*@Autowired
    ServiceAddressKeeper Ips;

    public RootClient() {

        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
        		.build();
    }*/

    @PostConstruct
    private void INIT() {
    	
        MainUri += "game";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }

    public Mono<ResponseEntity<String>> launch(int id, String token) {

        return webClient.post()
                        .uri("/" + id + "/launch")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
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
                        });
    }

    public Mono<ResponseEntity<String>> kill(int id, String token) {
        return webClient.post()
                        .uri("/" + id + "/kill")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e));
    }

    public Mono<ResponseEntity<Boolean>> isAlive(int id, String token) {
        return  webClient.post()
                        .uri("/" + id + "/is_alive")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e));
    }

    public Mono<ResponseEntity<String>> unpackServer(UnpackPayload payload, String token) {
        System.err.println("unpacking version client start");
        return webClient.post()
                        .uri("/" + payload.id_mineserver + "/unpack_by_version/" + payload.id_version)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e));
    }

    //TODO: нужно передавать отдельно ID сервера и хоста, сейчас передается только хост
    public Mono<ResponseEntity<Boolean>> mineserverInstalled(int id, String token) {
        System.out.println(MainUri + "/" + id + "/installed");
        return webClient.post()
                        .uri("/" + id + "/installed")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(Boolean.class)
                        .onErrorResume(e -> ErrorHandler.handleErrorBool(e));
    }

    public Mono<ResponseEntity<String>> deleteServer(int id, String token) {
        System.out.println(MainUri + "/" + id + "/uninstall");
        return webClient.post()
                        .uri("/" + id + "/uninstall")
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e));
    }
}

