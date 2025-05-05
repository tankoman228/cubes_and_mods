package com.cubes_and_mods.host.security;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.Logging;

public class SecurityChecker {

    public AllowedOrigins allowedOrigins;
    public Logging logging;

    public SecurityChecker(AllowedOrigins allowedOrigins, Logging logging) {
        this.allowedOrigins = allowedOrigins;
        this.logging = logging;
    }
}
