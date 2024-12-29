package com.cubes_and_mods.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.admin.db.Machine;

public class ClientToOthers {

	private WebClient webClient;
	
	  public ClientToOthers() {
		  
	      this.webClient = WebClient.builder()
	        		.baseUrl("http://localhost:8089")
	        		.build();
	  }
	  
	  public List<Machine> getAllMachines() {
		  
		  var response = webClient
			        .get()
			        .uri("res/machines")
			        .retrieve()
			        .toEntityList(Machine.class)
			        .block();
		  
		  return response.getBody();
	  }
}
