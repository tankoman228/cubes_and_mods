package com.cubes_and_mods.buy.service_repos;

import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.buy.db.Mineserver;
import com.cubes_and_mods.buy.db.Tariff;
import com.cubes_and_mods.buy.service_repos.repos.ReposMachines;
import com.cubes_and_mods.buy.service_repos.repos.ReposMineservers;
import com.cubes_and_mods.buy.service_repos.repos.ReposTariff;

/**
 * Used by PayController for validation and calling API in "res" microservice
 * */
@Service
public class ServicePay {

	@Autowired
	private ReposMineservers mineservers;
	
	@Autowired 
	private ReposTariff tariffs;
	
	@Autowired 
	private ReposMachines machines;
	
	@Autowired
	private ApiClientToRes res;
	

	public Order MakeOrder(Mineserver mine, Optional<Tariff> t) throws Exception {
		
		if (mine == null)
			throw new Exception("fdgh null mine");
		
		if (Strings.isEmpty(mine.getName())) {
			throw new Exception("Name is null or empty");
		}
		
		var tariff = tariffs.findById(mine.getIdTariff()).get();
		var machine = machines.findById(mine.getIdMachine()).get();
		var mineserver = mineservers.findById(mine.getId());
		// TODO: User check
			
		
		if (mineserver.isPresent()) {	
			if (t.isPresent()) {	
				// Update tariff
				
				var new_tariff_id = t.get().getId();
				if( !res.can_update_tariff(mine.getId(), new_tariff_id)) {
					throw new Exception("No enough resourses!");
				}
				
				res.free(mine.getId(), tariff);
				
				tariff = t.get();
				res.TryReserve(mine, tariff);
			}			
		}
		else {
			if (!res.can_handle(mine.getIdMachine(), mine.getIdTariff())) {
				throw new Exception("No enough resourses!");
			}
			res.TryReserve(mine, tariff);
		}
		
		
		return new Order(mine, tariff, machine, !mineserver.isPresent());
	}
	
	public void confirm(Order order) {
			
		if (order.mineserver.getMemoryUsed() == null)
			order.mineserver.setMemoryUsed((long)0);
		
		
		var mine = mineservers.save(order.mineserver);
		
		if (order.UpdateTariff || order.CreateNew) {
			mine.setSecondsWorking(0);
			mine.setIdTariff(order.tariff.getId());
		}
		else if (!order.CreateNew) {
			mine.setSecondsWorking(mine.getSecondsWorking() - order.tariff.getHoursWorkMax() * 3600);
		}
		mineservers.save(mine);
		
		order.IsAccepted = true;
	}
	
	public void decline(Order order) {
		
		res.free(order.mineserver.getIdMachine(), order.tariff);
	}
}
