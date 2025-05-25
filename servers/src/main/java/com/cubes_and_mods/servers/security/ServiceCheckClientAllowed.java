package com.cubes_and_mods.servers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.servers.jpa.repos.ClientRepos;

@Service
public class ServiceCheckClientAllowed {

    @Autowired
    private ClientRepos clientRepos;

    @Autowired
    private ServiceClientSession sessionClient;

    public void checkHostAllowed(ProtectedRequest<?> request, Integer hostId) {

        var client = sessionClient.getSession(request.userSession).client;
        client = clientRepos.findById(client.getId()).get();

        boolean allowed = false;
        for (var host : client.getHosts()) {
            if (host.getId().equals(hostId)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            for (var hostShare: client.getHost_sharings() ) {
                if (hostShare.getHostHostSharing().getId().equals(hostId)) {
                    allowed = true;
                    break;
                }
            }
        }

        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Host not allowed");
    }
}
