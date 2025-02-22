package com.cubes_and_mods.host.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.cubes_and_mods.host.service.mineserver_process.IMinecraftHandler;
import com.cubes_and_mods.host.service.mineserver_process.MinecraftServerObserver;

import jakarta.transaction.Transactional;


/**
 * Отвечает за управление MinecraftServerObserver и предоставление возможности работы с БД
 * 
 * Observer каждые N секунд проверяет директорию игрового сервера, считает время рантайма
 * */
@Service
public class ServiceMinecraftServerObserver {
/*
	@Autowired
    private ReposTariff tariffs;
	
	@Autowired
    private ReposMineserver mineservers;
	
	@Autowired
    private ReposBackup backups;
	
	private static ConcurrentHashMap<Integer, Object> observed;
	
	public void StartObserving(IMinecraftHandler handler) {
		
		if (observed == null) {
			observed = new ConcurrentHashMap<Integer, Object>();
		}
		System.out.print("Observer start");
		
		var idMine = handler.getMineserver().getId();
		if (observed.containsKey(idMine)) {
			return;
		}
		observed.put(idMine, new Object());
		
		Tariff t = tariffs.findById(handler.getMineserver().getIdTariff()).get();
		
		System.out.print("Observer create");
		new MinecraftServerObserver(handler, t, 
		
		// save in db callback (update data about seconds)
		(mine, s) -> { 
			mineservers.addSeconds(mine.getId(), s);		
			var newm = mineservers.findById(mine.getId()).get();
			mine.setSecondsWorking(newm.getSecondsWorking());
		}, 
		
		// save in db callback (update data about memory)
		mine -> { 
			var m = mineservers.findById(mine.getId()).get();
			m.setMemoryUsed(mine.getMemoryUsed());
			mineservers.save(m);
		},
		
		// Backup count callback
		id_minjeserver -> { 				
			return backups.getSumSizeForMineserver(id_minjeserver);
		});
	}*/
}
