package com.cubes_and_mods.host.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.host.service.mineserver_process.IMinecraftHandler;
import com.cubes_and_mods.host.service.mineserver_process.MinecraftHandler;

/**
 * Contains every MinecraftHanler, they are autocreated here
 * 
 * Autostarts mineserver observer task
 * 
 * Look more at package mineserver_process
 * */
@Service
public class ServiceHandlers {
	
	/**
	 * Handled mineservers with its' threads and processes for ALL SOCKETS
	 * */
	private static volatile ConcurrentHashMap<Integer, IMinecraftHandler> HANDLED = new ConcurrentHashMap<>(); 
	/*
	@Autowired
	ReposMineserver mines;
	
	@Autowired
	ReposTariff tariffs;
	
	@Autowired
	ServiceMinecraftServerObserver observe;
	
	/**
	 * Creates if not exists or just returns minecraft server handler.
	 * Autostarts mineserver observer task
	 * */
	
	
	/*public IMinecraftHandler get(int id_mineserver) {	
		
		if (HANDLED.containsKey(id_mineserver))
			return HANDLED.get(id_mineserver);
		
		var mine = mines.findById(id_mineserver).get();
		var t = tariffs.findById(mine.getIdTariff()).get();
		
		var h = new MinecraftHandler(mine, t);
		observe.StartObserving(h);
		HANDLED.put(id_mineserver, h);
		
		return h;
	}*/
	
	/**
	 * Kills minecraft server
	 * */
	/*public void KillProcess(int id_mineserver) {	
		
		if (!HANDLED.containsKey(id_mineserver))
			return;
		
		var handler = HANDLED.get(id_mineserver);
		
		handler.killProcess();
		
		//HANDLED.remove(id_mineserver);
	}*/
}
