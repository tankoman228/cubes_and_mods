package com.cubes_and_mods.servers.security.annotations;

import org.springframework.stereotype.Component;

import com.cubes_and_mods.servers.security.annotations.CheckUserSession.SessionValidator;
import com.cubes_and_mods.servers.security.ClientSession;
import com.cubes_and_mods.servers.security.ProtectedRequest;

/**
 * Пустой валидатор, проверяет только наличие сессии, значение по умолчанию для @CheckUserSession
 */
@Component
public class DefaultSessionValidator implements SessionValidator {

    @Override
    public boolean validate(ClientSession session, ProtectedRequest<?> request) {
        return session != null;
    }
}