package com.cubes_and_mods.servers.security;

import com.cubes_and_mods.servers.jpa.Client;

public class ClientSession {

    public Client client;

    public ClientSession(Client client) {
        this.client = client;
        this.client.setPassword(null);
    }
    public ClientSession() {}
}
