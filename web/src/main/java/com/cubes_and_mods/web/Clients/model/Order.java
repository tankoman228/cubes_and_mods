package com.cubes_and_mods.web.Clients.model;

import java.util.Calendar;
import java.util.Date;

import com.cubes_and_mods.web.DB.Machine;
import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.DB.Tariff;

public class Order {

	public Mineserver mineserver;
	public Tariff tariff;
	public Machine machine;
	
	public volatile boolean IsAccepted;
	public volatile boolean UpdateTariff;
	public volatile boolean CreateNew;
	
	private Calendar expiredAt;	
	private static final int MinutesBeforeExpired = 2;
	
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
