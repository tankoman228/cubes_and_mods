package com.cubes_and_mods.game.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.db.Tariff;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;
import com.cubes_and_mods.game.service.mineserver_process.IMinecraftHandler;
import com.cubes_and_mods.game.service.mineserver_process.MinecraftServerObserver;

import jakarta.transaction.Transactional;

@Service
/**
 * Отвечает за управление MinecraftServerObserver и предоставление возможности работы с БД
 * */
public class ServiceMinecraftServerObserver {

	@Autowired
    private ReposTariff reposTariff;
	
	@Autowired
    private ReposMineserver reposMineserver;
	
	private static ConcurrentHashMap<Integer, Object> observed;
	
	@Transactional
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
		
		Tariff t = reposTariff.findById(idMine).get();
		
		System.out.print("Observer create");
		new MinecraftServerObserver(handler, t, mine -> {
			try {				
				reposMineserver.save(mine);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.print("MINESERVER OBSERVER CANNOT UPDATE");
			}
		});
	}
}
