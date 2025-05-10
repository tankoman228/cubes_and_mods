package com.cubes_and_mods.servers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.servers.jpa.Server;
import com.cubes_and_mods.servers.jpa.repos.ServerRepos;
import com.cubes_and_mods.servers.security.ProtectedRequest;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins.MService;

@RestController
@RequestMapping("/servers")
public class ControllerServers {

    @Autowired
    private ServerRepos serverRepos;

    @AllowedOrigins(MService.WEB)
    @GetMapping("/{id}")
    public ResponseEntity<Server> server(@RequestBody ProtectedRequest<Void> body, @PathVariable Integer id) { 
        
        var s = serverRepos.findById(id);
        if (s.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(s.get());
    }

    @AllowedOrigins(MService.WEB)
    @GetMapping("/all")
    public ResponseEntity<List<Server>> all(@RequestBody ProtectedRequest<Void> body) { 
        return ResponseEntity.ok(serverRepos.findAll());
    }
}
