package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.admin.jpa.Host;
import com.cubes_and_mods.admin.jpa.repos.HostRepos;
import com.cubes_and_mods.admin.security.ClientConnectorForKey;
import com.cubes_and_mods.admin.security.MicroserviceInitializer.RegisterMsRequest;
import com.cubes_and_mods.admin.security.ProtectedRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/hosts")
public class HostController {

    @Autowired
    private HostRepos hostRepository;

    @GetMapping
    public List<Host> getAllHosts() {
        return hostRepository.findAll(); // используем кастомный запрос с JOIN FETCH
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHost(@PathVariable Integer id) {

        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host not found"));
       
        var uri = "https://" + host.getServerHost().getAddress() + "/remove_and_clear/" + host.getId();
        System.out.println("DELETE " + uri);

        WebClient webClient = WebClient.builder()
                .baseUrl(uri) 
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        
        var code = webClient.post()
            .uri(uri)
            .bodyValue(new ProtectedRequest<Void>(null))
            .retrieve()
            .toEntity(Void.class)
            .block()
            .getStatusCode();

        if (code.is2xxSuccessful()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(code).build();
        }
    }
}
