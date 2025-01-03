package com.cubes_and_mods.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

import com.cubes_and_mods.admin.db.Machine;
import com.cubes_and_mods.admin.db.Mineserver;


/**
 * HTTP client to other microservices
 * */
@Component
public class ApiClient {
		
		private final WebClient webClient;
		
		@Value("${redirect.url}") 
		private String clientBaseUrl;
		
		public ApiClient(WebClient.Builder webClientBuilder, @Value("${redirect.url}") String clientBaseUrl) {
		    this.clientBaseUrl = clientBaseUrl;
		    this.webClient = webClientBuilder.baseUrl(clientBaseUrl).build();
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
	  
	  public List<Mineserver> getAllMineservers() {
		  
		  var response = webClient
			        .post()
			        .uri("res/mineservers/all")
			        .retrieve()
			        .toEntityList(Mineserver.class)
			        .block();
		  
		  if (!response.getStatusCode().is2xxSuccessful()) {
			  System.err.print("HELP ME I AM TIRED");
			  System.out.println(response.getStatusCode().toString());
		  }
		  
		  return response.getBody();
	  }
}
