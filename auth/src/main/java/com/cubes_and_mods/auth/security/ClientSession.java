package com.cubes_and_mods.auth.security;

import com.cubes_and_mods.auth.jpa.Client;

public class ClientSession {

    public Client client;

    public ClientSession(Client client) {
        this.client = client;
        this.client.setPassword(null);
    }
    public ClientSession() {}
}
