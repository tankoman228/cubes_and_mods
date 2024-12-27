package com.cubes_and_mods.game.service.mineserver_process;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;
import com.cubes_and_mods.game.service.ServiceMinecraftServerObserver;

@Service
public class ServiceHandlers {
	
	/**
	 * Handled mineservers with its' threads and processes for ALL SOCKETS
	 * */
	private static volatile Map<Integer, IMinecraftHandler> HANDLED = new HashMap<>(); 
	
	@Autowired
	ReposMineserver mines;
	
	@Autowired
	ReposTariff tariffs;
	
	@Autowired
	ServiceMinecraftServerObserver observe;
	
	public IMinecraftHandler get(int id_mineserver) {	
		
		if (HANDLED.containsKey(id_mineserver))
			return HANDLED.get(id_mineserver);
		
		var mine = mines.findById(id_mineserver).get();
		var t = tariffs.findById(mine.getIdTariff()).get();
		
		var h = new MinecraftHandler(mine, t);
		observe.StartObserving(h);
		
		return h;
	}
}
