package com.cubes_and_mods.host.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cubes_and_mods.host.jpa.repos.VersionRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;

/**
 * Вынес только из-за VersionRepos, а так логику можно и в контроллер запихать, почти все проверки идут на уровне данных
 */
@Service
public class ServiceGame {

    @Autowired
    private ServiceContainersHandlers serviceContainersHandlers;

    @Autowired
    private VersionRepos versionRepos;

    
	public boolean is_alive(ProtectedRequest<Void> request, Integer id) throws Exception { 	

        var c = serviceContainersHandlers.getContainer(id, request);
        return c.processManager.isGameServerAlive();
	}

	public void launch(ProtectedRequest<Void> request, Integer id) throws Exception { 
		
        var c = serviceContainersHandlers.getContainer(id, request);
        c.processManager.startGameServer();
	}

	public void kill(ProtectedRequest<Void> request, Integer id) throws Exception { 

        var c = serviceContainersHandlers.getContainer(id, request);
        c.processManager.killGameServer();
	}

	public void unpack_by_version(ProtectedRequest<Void> request, Integer id, Integer vid) throws Exception { 

        var c = serviceContainersHandlers.getContainer(id, request);
        var v = versionRepos.findById(vid).orElseThrow(() -> new Exception("Version not found"));
        c.fileManager.initGameServerByVersion(v);
	}

	public boolean installed(ProtectedRequest<Void> request, Integer id) throws Exception { 

        var c = serviceContainersHandlers.getContainer(id, request);
        return c.fileManager.isGameServerInstalled();
	}

	public void uninstall(ProtectedRequest<Void> request, Integer id) throws Exception { 

        var c = serviceContainersHandlers.getContainer(id, request);
        c.containerManager.deleteContainer();
	}
}
