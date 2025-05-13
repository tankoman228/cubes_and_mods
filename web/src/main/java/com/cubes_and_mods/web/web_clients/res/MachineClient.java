package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MachineClient {
	
    //@Autowired
    //ProxyConfig ProxyConfig;
	
    private WebClient webClient;

	@Value("${servers-address}")
    private String MainUri;
    
    @PostConstruct
    private void INIT() {
    	
    	MainUri += "/servers";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
				.clientConnector(ClientConnectorForKey.getForKey("servers"))
        		.build();
    }
    
    
    public Flux<Server> getAllMachines(){ 
    	return webClient.post()
	    		.uri("/all")
				.bodyValue(new ProtectedRequest<Void>())
	            .retrieve()
	            .bodyToFlux(Server.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Server>> getMachineById(int id){
    	return webClient.post()
	    		.uri("/" + id)
				.bodyValue(new ProtectedRequest<Void>())
	            .retrieve()
	            .toEntity(Server.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
    
    /*public Flux<Machine> getWhichCanMachines(Tariff tariff){ 
    	return webClient.post()
	    		.uri("/which_can")
	    		.bodyValue(tariff)
	            .retrieve()
	            .bodyToFlux(Machine.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Void>> reserve(int id, Tariff tariff){
    	return webClient.post()
	    		.uri("/reserve/" + id)
	    		.bodyValue(tariff)
	            .retrieve()
	            .toEntity(Void.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
    
    public Mono<ResponseEntity<Void>> free(int id, Tariff tariff){
    	return webClient.post()
	    		.uri("/free/" + id)
	    		.bodyValue(tariff)
	            .retrieve()
	            .toEntity(Void.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
    
    public Mono<ResponseEntity<Void>> recount(int id){
    	return webClient.post()
	    		.uri("/recount")
	    		.bodyValue(id)
	            .retrieve()
	            .toEntity(Void.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
    
    public Mono<ResponseEntity<Boolean>> canHandle(int id_machine, int id_tariff){
    	return webClient.post()
	    		.uri("/can_handle/" + id_machine + "/" + id_tariff)
	            .retrieve()
	            .toEntity(Boolean.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleErrorBool(e);
	            });
    }
    
    public Mono<ResponseEntity<Boolean>> canHandleWithNewTariff(int id_mineserver, int id_tariff){
    	return webClient.post()
	    		.uri("/can_update_tariff/" + id_mineserver + "/" + id_tariff)
	            .retrieve()
	            .toEntity(Boolean.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleErrorBool(e);
	            });
    }*/
}
