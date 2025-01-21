package com.cubes_and_mods.web.web_clients.game;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.web.web_clients.res.MineserverClient;
import com.cubes_and_mods.web.web_clients.res.MachineClient;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * Хранит адреса игровых серверов внутри ЛОКАЛЬНОЙ (внутренней) сети
 * */
@Service
public class ServiceAddressKeeper {
	
	private static volatile ConcurrentHashMap<Integer, String> addresses;
	
	@Autowired
	MineserverClient MineserverClient;
	
	@Autowired
	MachineClient MachineClient;
	
	
	@PostConstruct
	private void INIT() {
		if (addresses == null)
			addresses = new ConcurrentHashMap<Integer, String>();
	}
	
	public Mono<String> getIp(int id_mineserver) {
		
	    if (addresses.containsKey(id_mineserver)) {
	        return Mono.just(addresses.get(id_mineserver));
	    }

	    return MineserverClient.getByIdMineserver(id_mineserver).flatMap(r -> {    	
	    	return MachineClient.getMachineById(r.getBody().getIdMachine()).flatMap(rr -> {
	    		var ip = "http://" + rr.getBody().getAddress();
	    		addresses.put(id_mineserver, ip);
	    		 System.err.println(ip);
	    		return Mono.just(ip);
	    	});
	    });
	}
}