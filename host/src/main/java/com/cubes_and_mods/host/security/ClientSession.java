package com.cubes_and_mods.host.security;

import com.cubes_and_mods.host.jpa.Client;

public class ClientSession {

    public Client client;

    public ClientSession(Client client) {
        this.client = client;
        this.client.setPassword(null);
    }
}
