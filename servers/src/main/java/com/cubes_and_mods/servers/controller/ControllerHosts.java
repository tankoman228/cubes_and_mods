package com.cubes_and_mods.servers.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.servers.jpa.Client;
import com.cubes_and_mods.servers.jpa.Host;
import com.cubes_and_mods.servers.jpa.HostSharing;
import com.cubes_and_mods.servers.jpa.repos.ClientRepos;
import com.cubes_and_mods.servers.jpa.repos.HostRepos;
import com.cubes_and_mods.servers.jpa.repos.HostSharingRepos;
import com.cubes_and_mods.servers.security.ProtectedRequest;
import com.cubes_and_mods.servers.security.ServiceCheckClientAllowed;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.cubes_and_mods.servers.security.annotations.CheckUserSession;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins.MService;

/**
 * Getter of mineservers for one user, for all users, getting mineserver obj from DB by ID
 * */
@RestController
@RequestMapping("/hosts")
public class ControllerHosts {
	
	@Autowired
	private HostRepos hostRepos;

	@Autowired
	private ClientRepos clientRepos;

	@Autowired
	private HostSharingRepos hostSharingRepos;

	@Autowired
	private ServiceCheckClientAllowed serviceCheckClientAllowed;


	@PostMapping("/of_user/{id}")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<List<Host>> of_user(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id) { 

		Client user = clientRepos.findById(id).get();
		
		var sharings = user.getHost_sharings();

		List<Host> res = new ArrayList<>();
		res.addAll(user.getHosts());
		for (var sh : sharings) {
			if (res.stream().anyMatch(h -> h.getId().equals((sh.getHostHostSharing().getId())))) continue;
			res.add(sh.getHostHostSharing());
		}

		return ResponseEntity.ok(hostRepos.findAll());
	}
	
	@PostMapping("/{id}")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Host> id(@RequestBody ProtectedRequest<Void> request, @PathVariable Integer id){ 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		Host host = hostRepos.findById(id).get();

		Hibernate.initialize(host.getTariffHost());
		Hibernate.initialize(host.getClientHost());
		Hibernate.initialize(host.getServerHost());

		return ResponseEntity.ok(host);
	}
	
	@PutMapping("/{id}/edit")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Void> edit(@RequestBody ProtectedRequest<Host> request, @PathVariable Integer id){ 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);

		var hosto = hostRepos.findById(id);
		if (hosto.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		var host = hosto.get();
		
		host.setName(request.data.getName());
		host.setDescription(request.data.getDescription());

		hostRepos.save(host);
		hostRepos.flush();

		return ResponseEntity.status(HttpStatus.OK).build(); 
	}
	
	@PostMapping("/{id}/share")
	@AllowedOrigins(MService.WEB)
	@CheckUserSession
	public ResponseEntity<Void> share(@RequestBody ProtectedRequest<String> request, @PathVariable Integer id){ 
		
		serviceCheckClientAllowed.checkHostAllowed(request, id);
		var targetClientOpt = clientRepos.findAll().stream().filter(x -> x.getEmail().equals(request.data)).findFirst();
		if (targetClientOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		var targetClient = targetClientOpt.get();

		var sharing = hostSharingRepos.findAll().stream().filter(x -> x.getClientHostSharing().getId().equals(targetClient.getId()) && x.getHostHostSharing().getId().equals(id)).findFirst();

		// Если такое уже есть, удаляем, если нет, создаём
		if (sharing.isPresent()) {
			hostSharingRepos.delete(sharing.get());
			hostSharingRepos.flush();
		}
		else {
			HostSharing hs = new HostSharing();
			hs.setClientHostSharing(targetClient);
			hs.setHostHostSharing(hostRepos.findById(id).get());
			hostSharingRepos.save(hs);
			hostSharingRepos.flush();
		}
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
