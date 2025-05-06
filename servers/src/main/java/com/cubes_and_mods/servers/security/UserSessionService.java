package com.cubes_and_mods.servers.security;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
/**
 * Позволяет проверять сессиию пользователя, выполняет кэширование сессий. По факту простая логика
 */
public class UserSessionService {

    private static volatile ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();


    public ClientSession getSession(String token) {

        if (sessions.containsKey(token)) {
            return sessions.get(token);
        }

        return null; // TODO: веб клиент сделать, чтобы он сам запрашивал сессию
    }

    public boolean hasAccessToHost(String token, Integer hostId) {
        return false;
    }
}
