package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class NetClient {

    private WebClient webClient;

    @Autowired
    ServiceAddressKeeper Ips;

    private volatile ConcurrentHashMap<Integer, Integer> mapOperationIdToUdMineserver; 

    @PostConstruct
    private void INIT() {
    	if (mapOperationIdToUdMineserver == null)
        	mapOperationIdToUdMineserver = new ConcurrentHashMap<Integer, Integer>();

        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }


    public Mono<ResponseEntity<String>> netConfig(int id, String token) {
        return Ips.getIp(id, token).flatMap(ip -> 
                    webClient.post()
                        .uri(ip + "/global_network_config/" + id)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e)));
    }
}
