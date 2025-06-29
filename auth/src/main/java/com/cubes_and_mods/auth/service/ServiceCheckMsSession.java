package com.cubes_and_mods.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.auth.security.ProtectedRequest;

@Service
public class ServiceCheckMsSession {

    @Autowired
    private ServiceMicroserviceSession serviceMicroserviceSession;

    @Value("#{new Boolean('${ignore-source-checking}')}")
    private Boolean IgnoreSourceChecking = false;

    public void check(ProtectedRequest<?> request, String microserviceAllowed) {

        if (IgnoreSourceChecking) return;

        var session = serviceMicroserviceSession.FindMicroserviceSession(request);
        if (session == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (!session.getServiceType().equals(microserviceAllowed)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
