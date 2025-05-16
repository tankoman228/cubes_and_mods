package com.cubes_and_mods.host.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.host.docker.DockerContainerHandler;
import com.cubes_and_mods.host.jpa.repos.HostRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;

@Service
public class ServiceDockerContainersHandlers {

    @Autowired
    private HostRepos hostRepos;

    public static volatile ConcurrentHashMap<Integer, DockerContainerHandler> handlers = new ConcurrentHashMap<>();

    public DockerContainerHandler getContainer(Integer id_host, ProtectedRequest<?> request) throws Exception {

        var handler = findContainer(id_host);
        return handler;
    }

    private DockerContainerHandler findContainer(Integer id_host) throws Exception {

        if (handlers.containsKey(id_host)) {
            return handlers.get(id_host);
        }

        var h = hostRepos.findById(id_host).orElseThrow(() -> new Exception("Host not found"));
        if (h.getServerHost().getId() != 1) { //TODO: вынести куда нибудь
            throw new Exception("Wrong host destination");
        }

        var handler = new DockerContainerHandler(h);
        handlers.put(id_host, handler);
        return handler;
    }
}
