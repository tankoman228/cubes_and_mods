package com.cubes_and_mods.web.Clients;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.DB.Tariff;
import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Machine;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MachineClient {
    private WebClient webClient;

    private String MainUri = ProxyConfig.getRes() + "/machines";
    
    public MachineClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<Machine> getAllMachines(){ 
    	return webClient.get()
	    		.uri("")
	            .retrieve()
	            .bodyToFlux(Machine.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
    
    public Mono<ResponseEntity<Machine>> getMachineById(int id){
    	return webClient.get()
	    		.uri("/" + id)
	            .retrieve()
	            .toEntity(Machine.class)
	            .onErrorResume(e -> {
	            	return ErrorHandler.handleError(e);
	            });
    }
    
    public Flux<Machine> getWhichCanMachines(Tariff tariff){ 
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
    }
}
