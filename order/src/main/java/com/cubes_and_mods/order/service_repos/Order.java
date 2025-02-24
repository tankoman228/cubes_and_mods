package com.cubes_and_mods.order.service_repos;

import java.util.Calendar;

import com.cubes_and_mods.order.jpa.*;


/**
 * Used in pay controller.
 * 
 * Stores order and it's status. Used in ServicePay and OrdersController
 * */
public class Order {

	public Host mineserver;
	public Tariff tariff;
	public Server machine;
	
	public volatile boolean IsAccepted;
	public volatile boolean UpdateTariff;
	public volatile boolean CreateNew;
	
	private Calendar expiredAt;	
	private static final int MinutesBeforeExpired = 60;
	
	public Order(Host mineserver, Tariff tariff, Server machine, boolean CreateNew) {
		
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
