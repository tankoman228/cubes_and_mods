package com.cubes_and_mods.host.security.annotations;

import org.springframework.stereotype.Component;

import com.cubes_and_mods.host.security.ClientSession;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.annotations.CheckUserSession.SessionValidator;

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