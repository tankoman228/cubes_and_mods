package com.cubes_and_mods.auth.security;

import com.cubes_and_mods.auth.security.Logging.SuspiciousLevel;

public class SecurityChecker {

    private String endpoint;
    private AllowedOrigins allowedOrigins;
    private Logging logging;

    public SecurityChecker(String endpoint, AllowedOrigins allowedOrigins, Logging logging) {
        this.endpoint = endpoint;
        this.allowedOrigins = allowedOrigins;
        this.logging = logging;
    }

    public boolean checkSecurity(String alpha, String origin) {

        if (logging.fromClient()) {
            throw new RuntimeException("Logging without of client session is not allowed");
        }

        return true;
    }

    public boolean checkSecurity(String alpha, String origin, ClientSession clientSession) {

        return true;
    }
}
