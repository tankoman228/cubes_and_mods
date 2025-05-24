package com.cubes_and_mods.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.admin.security.ClientConnectorForKey;
import com.cubes_and_mods.admin.security.MicroserviceInitializer.RegisterMsRequest;
import com.cubes_and_mods.admin.security.ProtectedRequest;

@Service
public class ServiceOrder {

    @Value("${order-address}")
    private String orderAddress;

    public void confirmOrder(String code) {
    	
        WebClient webClient = WebClient.builder()
                .baseUrl(orderAddress) 
                .clientConnector(ClientConnectorForKey.getForKey("order"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        
        webClient.put()
            .uri("/orders/confirm")
            .bodyValue(new ProtectedRequest<String>(code))
            .retrieve()
            .toEntity(Void.class)
            .doOnSuccess(response -> 
            {
                System.out.println("Заказ проведён: ");
            })
            .block();
    }

    public void rejectOrder(String code) {
    	
        WebClient webClient = WebClient.builder()
                .baseUrl(orderAddress) 
                .clientConnector(ClientConnectorForKey.getForKey("order"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        
        webClient.put()
            .uri("/orders/cancel")
            .bodyValue(new ProtectedRequest<String>(code))
            .retrieve()
            .toEntity(Void.class)
            .doOnSuccess(response -> 
            {
                System.out.println("Заказ отменён: ");
            })           
            .block();
    }
}
