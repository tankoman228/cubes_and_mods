package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.RootClient;
import com.cubes_and_mods.web.Clients.model.UnpackPayload;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/root")
public class RootController {
	
	@Autowired
	RootClient rootClient;
	
	@PostMapping("/launch")
	public Mono<ResponseEntity<Void>> Launch(@RequestBody int id){
		return rootClient.Launch(id);
	}
	
	
	/*Не забыть переделать rootClient.isAlive(id) 
	[block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-http-epoll-2]
	
    @PostMapping("/launch")
    public Mono<ResponseEntity<Void>> launch(@RequestBody int id) {
        return rootClient.isAlive(id)
                .flatMap(response -> {
                    if (response.getBody() != null && response.getBody()) {
                        return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                    } else {
                        return Mono.fromCallable(() -> rootClient.Launch(id))
                                .onErrorReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }*/

	@PostMapping("/is_alive")
	public Mono<ResponseEntity<Boolean>> isAlive(@RequestBody int id){
		return rootClient.isAlive(id);
	}
	
	@PostMapping("/unpack")
	public Mono<ResponseEntity<Void>> unpack(@RequestBody UnpackPayload payload) {
		System.out.println("ID SERVER = " + payload.id_mineserver + "ID VER = " + payload.id_version);
		return rootClient.unpackServer(payload);
	}
	
	@PostMapping("/isInstalled")
	public Mono<ResponseEntity<Boolean>> mineserverInstalled(@RequestBody int id){
		return rootClient.mineserverInstalled(id);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Void>> deleteServer(@RequestBody int id){
		return rootClient.delete_server(id);
	}
	
	@PostMapping("/kill")
	public Mono<ResponseEntity<Void>> killServer(@RequestBody int id){
		return rootClient.kill(id);
	}
}
