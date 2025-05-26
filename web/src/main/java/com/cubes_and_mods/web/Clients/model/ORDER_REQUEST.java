package com.cubes_and_mods.web.Clients.model;

import java.util.Optional;

import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.DB.Tariff;

public class ORDER_REQUEST {
	public Mineserver mineserver; // MUST BE ALWAYS
	public Optional<Tariff> newTariff; // Contents null if not changing tariff of already existing server
}