package com.cubes_and_mods.servers.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cubes_and_mods.servers.security.ClientSession;
import com.cubes_and_mods.servers.security.ProtectedRequest;

/**
 * Проверятель сессии пользователя, по умолчанию просто проверяет, что она вообще есть,
 * но можно задать свой валидатор, всегда помечать валидаторы как @Component
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserSession {

    Class<? extends SessionValidator> validator() default DefaultSessionValidator.class;

    public interface SessionValidator {
        boolean validate(ClientSession session, ProtectedRequest<?> request);
    }
}
