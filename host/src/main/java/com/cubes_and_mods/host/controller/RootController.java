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
import com.cubes_and_mods.host.jpa.repos.VersionRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
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

	@PostMapping("/remove_and_clear/{id_host}")
	@AllowedOrigins({})
	public ResponseEntity<Void> remove_and_clear(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id_host) { 
		
		try {
			var c = serviceContainersHandlers.getContainer(id_host, body);
			if (c.containerManager.containerCreated()) {
				c.containerManager.killContainer();
				c.containerManager.deleteContainer();
			}
			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PostMapping("/global_network_config/{id_host}")
	@AllowedOrigins(MService.WEB)
	public ResponseEntity<Map<String, String>> global_network_config(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id_host) { 
		try {
			var c = serviceContainersHandlers.getContainer(id_host, body);
			var r = c.containerManager.getSSHandSFTPinfo();

			return ResponseEntity.ok(r);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PostMapping("/system_journal/{id_host}")
	@AllowedOrigins({})
	public ResponseEntity<Void> system_journal(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id_host) { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}

	@PostMapping("/ms/log")
	@AllowedOrigins({})
	public ResponseEntity<Void> log() { 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); 
	}


	// TODO: убрать, когда появится нормальный способ архивировать версии

	@Autowired
	private VersionRepos versionRepos;

	@GetMapping("/make_version_for_test_only")
	@AllowedOrigins({}) 
	public ResponseEntity<Void> makeversion() { 

		try {
			File f = new File("/home/serg/MCVers/1.21.5.zip");
			var v = new Version();
	
			v.setIdGame(5);
			v.setName("[Forge] 1.21.5 Vanila");
			v.setDescription("Ванильная версия Майнкрафт 1.21.5 на ядре Forge");
			v.setArchive(Files.readAllBytes(f.toPath()));
	
			versionRepos.save(v);
			versionRepos.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build(); 
	}
}
