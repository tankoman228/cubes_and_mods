package com.cubes_and_mods.auth.service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.auth.jpa.Client;
import com.cubes_and_mods.auth.security.ClientSession;

/**
 * Хранит сессии клиентов (пользователей)
 */
@Service
public class ServiceClientSessions {

    private static volatile ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private PasswordHash passwordHash;

    public String startSessionGetCode(Client client) {

        SecureRandom random = new SecureRandom();
        var token = passwordHash.hash(client.getPassword(), random.nextInt(1388));

        ClientSession session = new ClientSession(client);
        sessions.put(token, session);

        return token; 
    }

    public ClientSession getSession(String token) {
        if (sessions.containsKey(token)) {
            return sessions.get(token);
        }
        return null;
    }

    public void deleteSession(String token) {
        sessions.remove(token);
    }
}
