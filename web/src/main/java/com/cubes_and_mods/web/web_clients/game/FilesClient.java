package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.Clients.model.FileInfo;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class FilesClient {
	
    @Value("${host-address}")
	private String MainUri;

    private WebClient webClient;
    
    /*@Autowired
    ServiceAddressKeeper Ips;
    
    public FilesClient() {
    	
        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
        		.build();
    }*/

    @PostConstruct
    private void INIT() {
    	
        MainUri += "files";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }
    
    public Mono<ResponseEntity<FileInfo>> files(int id_server) {
        System.out.println("/" + id_server);
        return webClient.post()
                .uri("/" + id_server)
                .bodyValue(new ProtectedRequest<Void>())
                .retrieve()
                .toEntity(FileInfo.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }
     
    public Mono<ResponseEntity<FileInfo>> file(int id_server, String path){
        System.out.println(MainUri + "/" + id_server + "/read");
        System.out.println("Путь: " + path);
        return webClient.post()
                .uri("/" + id_server + "/read")
                .bodyValue(new ProtectedRequest<String>(path))
                .retrieve()
                .toEntity(FileInfo.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }
    
    public Mono<ResponseEntity<Void>> upload(int id_server, FileInfo file){
        System.out.println(MainUri + "/" + id_server + "/upload");
        System.out.println(file == null);
        return webClient.post()
                .uri("/" + id_server + "/upload")
                .bodyValue(new ProtectedRequest<FileInfo>(file))
                .retrieve()
                .toEntity(Void.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }
    
    public Mono<ResponseEntity<Void>> delete(int id_server, String path){
        return webClient.post()
                .uri("/" + id_server + "/delete")
                .bodyValue(new ProtectedRequest<String>(path))
                .retrieve()
                .toEntity(Void.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }
}
