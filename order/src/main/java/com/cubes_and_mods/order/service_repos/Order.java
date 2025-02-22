package com.cubes_and_mods.order.service_repos;

import java.util.Calendar;

import com.cubes_and_mods.order.db.Machine;
import com.cubes_and_mods.order.db.Mineserver;
import com.cubes_and_mods.order.db.Tariff;

/**
 * Used in pay controller.
 * 
 * Stores order and it's status. Used in ServicePay and OrdersController
 * */
public class Order {

	public Mineserver mineserver;
	public Tariff tariff;
	public Machine machine;
	
	public volatile boolean IsAccepted;
	public volatile boolean UpdateTariff;
	public volatile boolean CreateNew;
	
	private Calendar expiredAt;	
	private static final int MinutesBeforeExpired = 60;
	
	public Order(Mineserver mineserver, Tariff tariff, Machine machine, boolean CreateNew) {
		
		this.mineserver = mineserver;
		this.tariff = tariff;
		this.machine = machine;
		
		this.IsAccepted = false;
		this.UpdateTariff = mineserver.getIdTariff() != tariff.getId();
		this.CreateNew = CreateNew;
		
		expiredAt = Calendar.getInstance();
		expiredAt.add(Calendar.MINUTE, MinutesBeforeExpired);
	}
	
	public boolean IsExpired() {	
		return expiredAt.before(Calendar.getInstance());
	}
}
