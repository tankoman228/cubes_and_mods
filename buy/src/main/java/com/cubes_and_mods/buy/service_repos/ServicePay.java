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

@Service
public class ServicePay {

	@Autowired
	private ReposMineservers mineservers;
	
	@Autowired 
	private ReposTariff tariffs;
	
	@Autowired 
	private ReposMachines machines;
	
	@Autowired
	private ClientToOthers res;
	

	public Order MakeOrder(Mineserver mine, Optional<Tariff> t) throws Exception {
		
		if (Strings.isEmpty(mine.getName())) {
			throw new Exception("Name is null or empty");
		}
		
		var tariff = tariffs.findById(mine.getIdTariff()).get();
		var machine = machines.findById(mine.getIdMachine()).get();
		var mineserver = mineservers.findById(mine.getId());
			
		if (mineserver.isPresent()) {	
			if (t.isPresent()) {	
				tariff = t.get();
				if( !res.can_update_tariff(mine.getId(), tariff.getId())) {
					throw new Exception("No enough resourses!");
				}
			}			
		}
		else {
			if (!res.can_handle(mine.getIdMachine(), t.get().getId())) {
				throw new Exception("No enough resourses!");
			}
		}
		
		return new Order(mine, tariff, machine, !mineserver.isPresent());
	}
	
	public void confirm(Order order) {
			
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
