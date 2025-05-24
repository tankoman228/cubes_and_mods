package com.cubes_and_mods.admin.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.admin.jpa.MicroserviceSession;
import com.cubes_and_mods.admin.jpa.repos.MicroserviceSessionRepos;
import com.cubes_and_mods.admin.security.ClientConnectorForKey;
import com.cubes_and_mods.admin.security.ProtectedRequest;


@RestController
@RequestMapping("/api/admin/sessions")
public class MonitoringController {

    @Autowired
    private MicroserviceSessionRepos repo;

    @GetMapping
    public List<MicroserviceSession> all() {
        return repo.findAll().stream().sorted((a, b) -> a.getIpPort().compareTo(b.getIpPort())).toList();    
    }

    @GetMapping("/check/{ipPort}")
    public ResponseEntity<Void> checkAlive(@PathVariable String ipPort) {
        // Будет ошыбка 500 - значит не живой, иначе живой

        System.out.println("checking " + ipPort);
        MicroserviceSession session = repo.findById(ipPort).orElse(null);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var uri = "https://" + session.getIpPort() + "/verify_ssl";
        WebClient webClient = WebClient.builder()
                .baseUrl(uri) 
                .clientConnector(ClientConnectorForKey.getForKey(session.getServiceType()))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        try {
            var code = webClient.post()
                .uri(uri)
                .bodyValue(new ProtectedRequest<Void>(null))
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getStatusCode();

            if (code == HttpStatus.FORBIDDEN) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(code).build();
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("403")) {
                return ResponseEntity.ok().build();
            }
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/alarm/{ipPort}")
    public ResponseEntity<MicroserviceSession> toggleAlarm(@PathVariable String ipPort) {
        return repo.findById(ipPort)
            .map(s -> {
                s.setAlarm(!Boolean.TRUE.equals(s.getAlarm()));
                repo.save(s);
                return ResponseEntity.ok(s);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/banned/{ipPort}")
    public ResponseEntity<MicroserviceSession> toggleBanned(@PathVariable String ipPort) {
        return repo.findById(ipPort)
            .map(s -> {
                s.setBanned(!Boolean.TRUE.equals(s.getBanned()));
                repo.save(s);
                return ResponseEntity.ok(s);
            })
            .orElse(ResponseEntity.notFound().build());
    }


    @Value("${auth-address}")
    private String authAddress;

    @PostMapping("/logs/{ipPort}")
    public ResponseEntity<String> getLogs(@PathVariable String ipPort) {

        MicroserviceSession session;
        if ("central".equals(ipPort)) {
            session = new MicroserviceSession();
            session.setIpPort(authAddress.replace("https://", ""));
            session.setServiceType("auth");
        } else {
            session = repo.findById(ipPort).orElse(null);
        }

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        var uri = "https://" + session.getIpPort() + "/microservice_logs";

        System.out.println(uri);

        WebClient webClient = WebClient.builder()
                .baseUrl(uri)
                .clientConnector(ClientConnectorForKey.getForKey(session.getServiceType()))
                .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(1024 * 1024 * 1024) // 1 ГБ
            )
                        .build())
                .build();

        try {
            var response = webClient.post()
                    .uri(uri)
                    .bodyValue(new ProtectedRequest<Void>(null))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getClass().getName() + " " + e.getMessage());
        }
    }
}