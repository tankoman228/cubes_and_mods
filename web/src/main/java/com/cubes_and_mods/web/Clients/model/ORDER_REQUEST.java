package com.cubes_and_mods.web.Clients.model;

import java.util.Optional;

import com.cubes_and_mods.web.DB.Mineserver;
import com.cubes_and_mods.web.DB.Tariff;

@Deprecated
public class ORDER_REQUEST {
	public Mineserver mineserver;
	public Optional<Tariff> newTariff;
}