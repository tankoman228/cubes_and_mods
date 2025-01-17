package com.cubes_and_mods.web.Controllers;

import java.util.HashMap;
import java.util.Map;

import com.cubes_and_mods.web.DB.User;

public class RequestKeeper {
	public static Map<String, User> authRequests = new HashMap<String, User>();
}
