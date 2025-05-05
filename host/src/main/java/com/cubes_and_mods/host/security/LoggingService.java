package com.cubes_and_mods.host.security;

import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    public void log(SecurityChecker checker, Object request) {
        throw new RuntimeException("Logging is not implemented");
    }

    
}
