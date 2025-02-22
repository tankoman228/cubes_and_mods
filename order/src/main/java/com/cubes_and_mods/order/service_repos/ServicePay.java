package com.cubes_and_mods.order.service_repos;

import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.order.db.Mineserver;
import com.cubes_and_mods.order.db.Tariff;
import com.cubes_and_mods.order.service_repos.repos.ReposMachines;
import com.cubes_and_mods.order.service_repos.repos.ReposMineservers;
import com.cubes_and_mods.order.service_repos.repos.ReposTariff;
import com.cubes_and_mods.order.service_repos.repos.ReposUsers;

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
	private ReposUsers users;
	
	@Autowired
	private ApiClientToRes res;
	

	/**
	 * Checks data and returns order object that contains everything to proceed logic after payment (confirm)
	 * */
	public Order MakeOrder(Mineserver mine, Tariff newTariff) throws Exception {
		
		// Data check
		if (mine == null)
			throw new Exception("fdgh null mine");
		if (Strings.isEmpty(mine.getName())) {
			throw new Exception("Name is null or empty");
		}
		
		// Check foreign keys, exception if can't get
		var tariff = tariffs.findById(mine.getIdTariff()).get();
		var machine = machines.findById(mine.getIdMachine()).get();
		var mineserver = mineservers.findById(mine.getId()); // Not present if creating new mineserver
		var user = users.findById(mine.getIdUser()).get();
		
		// Stop operation in some cases
		if (Strings.isEmpty(mine.getName())) {
			throw new Exception("Name is null or empty");
		}
		if (user.getBanned()) {
			throw new Exception("User is banned!");
		}
		
		// Update tariff or extend runtime
		if (mineserver.isPresent()) {	
			if (newTariff != null) {	
				// Update tariff
				
				var new_tariff_id = newTariff.getId();
				if( !res.can_update_tariff(mine.getId(), new_tariff_id)) {
					throw new Exception("No enough resourses!");
				}
				
				res.free(mine.getIdMachine(), tariff);
				
				tariff = newTariff;
				res.TryReserve(mine, tariff);
			}			
			// else no need to reserve anything
		}
		
		// Create new mineserver, just reserve and wait for confirm (that order is payed)
		else {
			if (!res.can_handle(mine.getIdMachine(), mine.getIdTariff())) {
				throw new Exception("No enough resourses!");
			}
			res.TryReserve(mine, tariff);
		}
		
		
		return new Order(mine, tariff, machine, !mineserver.isPresent());
	}
	
	/**
	 * Give it Order that you get from MakeOrder. Called after order is not expired and payed
	 * */
	public void confirm(Order order) {
			
		if (order.mineserver.getMemoryUsed() == null)
			order.mineserver.setMemoryUsed((long)0);
		
		// Insert operation (first order for user)
		if (order.CreateNew) {
			
			order.mineserver.setSecondsWorking(0);
			order.mineserver.setMemoryUsed(0L);
			mineservers.save(order.mineserver);		
		}
		// Update operation (extend time or change tariff)
		else {
			var mine = mineservers.getReferenceById(order.mineserver.getId());
			
			if (order.UpdateTariff) {
				mine.setSecondsWorking(0);
				mine.setIdTariff(order.tariff.getId());
			}
			else {
				var t = tariffs.getReferenceById(mine.getIdTariff());
				mine.setSecondsWorking(mine.getSecondsWorking() - t.getHoursWorkMax() * 3600);
			}
			mineservers.save(mine);
		}
		
		order.IsAccepted = true;
	}
	
	public void decline(Order order) {
		
		res.free(order.mineserver.getIdMachine(), order.tariff);
	}
}
