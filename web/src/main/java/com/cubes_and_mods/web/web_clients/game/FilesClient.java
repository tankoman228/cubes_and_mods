package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.FileInfo;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import reactor.core.publisher.Mono;

@Service
public class FilesClient {
	
    private WebClient webClient;
    
    @Autowired
    ProxyConfig ProxyConfig;
    
    @Autowired
    ServiceAddressKeeper Ips;
    
    public FilesClient() {
    	
        this.webClient = WebClient.builder()
                .clientConnector(ClientConnectorForKey.getForKey("host"))
        		.build();
    }
    
    public Mono<ResponseEntity<FileInfo>> files(int id_server) {
        return Ips.getIp(id_server)
                .flatMap(ip -> {
                    System.out.println(ip + "/files/" + id_server);
                    return webClient.post()
                            .uri(ip + "/files/" + id_server)
                            .bodyValue(new ProtectedRequest<Void>())
                            .retrieve()
                            .toEntity(FileInfo.class)
                            .onErrorResume(e -> {
                                return ErrorHandler.handleError(e);
                            });
                });
    }
     
    public Mono<ResponseEntity<FileInfo>> file(int id_server, String path){
    	
    	 return Ips.getIp(id_server)
                 .flatMap(ip -> {
			        return webClient.post()
			                .uri(ip + "/files/{id_server}/read/", id_server)
			                .bodyValue(new ProtectedRequest<String>(path))
			                .retrieve()
			                .toEntity(FileInfo.class)
			                .onErrorResume(e -> {
			                	return ErrorHandler.handleError(e);
			                });
          });
    }
    
    public Mono<ResponseEntity<Void>> upload(int id_server, FileInfo file){
    	
    	 return Ips.getIp(id_server)
                 .flatMap(ip -> {
			        return webClient.post()
			                .uri(ip + "/files/{id_server}/upload/", id_server)
			                .bodyValue(new ProtectedRequest<FileInfo>(file))
			                .retrieve()
			                .toEntity(Void.class)
			                .onErrorResume(e -> {
			                	return ErrorHandler.handleError(e);
			                });
                 });
    }
    
    public Mono<ResponseEntity<Void>> delete(int id_server, String path){
    	
    	return Ips.getIp(id_server)
                .flatMap(ip -> {
                    return webClient.post()
                            .uri(ip + "/files/{id_server}/delete/", id_server)
                            .bodyValue(new ProtectedRequest<String>(path))
                            .retrieve()
                            .toEntity(Void.class)
                            .onErrorResume(e -> {
                                return ErrorHandler.handleError(e);
                            });
        });
    }
}
