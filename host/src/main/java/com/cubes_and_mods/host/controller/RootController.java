package com.cubes_and_mods.host.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.jpa.Version;
import com.cubes_and_mods.host.jpa.repos.HostRepos;
import com.cubes_and_mods.host.jpa.repos.ServerRepos;
import com.cubes_and_mods.host.jpa.repos.VersionRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.ServiceCheckClientAllowed;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.CheckUserSession;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins.MService;
import com.cubes_and_mods.host.service.ServiceDockerContainersHandlers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/")
public class RootController {
	
	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	

        if (ProtectedRequest.c != null) return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Чтоюы после регистрации ЭЦП не менялась никогда и никем

		var answ = new ResponseEntity<>(new VerifyWebResponce(r.a + r.b), HttpStatus.OK); 
        ProtectedRequest.c = String.valueOf(r.a + r.b);
        return answ;
	}
	
    public static class VerifyWebRequest { 
        public int a, b;
    }

    public static class VerifyWebResponce {
        public int c;
        @JsonCreator
        public VerifyWebResponce (@JsonProperty("c") int c) {
            this.c = c;
        }
        public VerifyWebResponce () {}
    }

	@Autowired
	private ServiceDockerContainersHandlers serviceContainersHandlers;

	@Autowired
	private HostRepos hostRepos;

	@Autowired
	private ServerRepos serverRepos;


	@PostMapping("/remove_and_clear/{id_host}")
	@AllowedOrigins({})
	public ResponseEntity<Void> remove_and_clear(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id_host) { 
		
		try {
			System.out.println("remove_and_clear " + id_host);

			System.out.println("killing container");
			var c = serviceContainersHandlers.getContainer(id_host, body);
			if (c.containerManager.containerCreated()) {
				c.containerManager.killContainer();
				c.containerManager.deleteContainer();
			}
			var host = hostRepos.findById(id_host).get();

			System.out.println("freeing resources");
			var server = host.getServerHost();
			server.setCpuThreadsFree((short) (server.getCpuThreadsFree() + host.getTariffHost().getCpuThreads()));
			server.setRamFree((short) (server.getRamFree() + host.getTariffHost().getRam()));
			server.setMemoryFree(server.getMemoryFree() + host.getTariffHost().getMemoryLimit());
			serverRepos.save(server);

			try {
				System.out.println("deleting host");
				hostRepos.delete(host);
				serverRepos.flush();
				hostRepos.flush();
			}
			catch (Exception e) {
				System.out.println("Error while deleting host " + e.getClass().getName());
				if (hostRepos.findById(id_host).isPresent()) return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
			}

			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}


	@Autowired
    private ServiceCheckClientAllowed serviceCheckClientAllowed;

	@PostMapping("/global_network_config/{id_host}")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Map<String, String>> global_network_config(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id_host) { 
		
		serviceCheckClientAllowed.checkHostAllowed(body, id_host);
		try {
			var c = serviceContainersHandlers.getContainer(id_host, body);
			var r = c.containerManager.getSSHandSFTPinfo();

			return ResponseEntity.ok(r);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PostMapping("/microservice_log")
	@AllowedOrigins({})
	public ResponseEntity<String> microservice_log(@RequestBody ProtectedRequest<Void> body) {
		return ResponseEntity.ok("dddd");
	}
}
