package com.cubes_and_mods.order.security;

import org.springframework.stereotype.Service;

import com.cubes_and_mods.order.security.annotations.Logging.SuspiciousLevel;

@Service
public class LoggingService {

    public void log(SecurityChecker checker, Object request) {
        throw new RuntimeException("Logging is not implemented");
    }

    
}
