package com.cubes_and_mods.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Client;
import com.cubes_and_mods.admin.jpa.repos.ClientRepos;

@RestController
@RequestMapping("/api/admin/clients")
public class AdminClientController {

    @Autowired
    private ClientRepos clientRepository;

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @PostMapping("/{id}/toggle-ban")
    public ResponseEntity<?> toggleBan(@PathVariable Integer id) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setBanned(!client.getBanned());
                    clientRepository.save(client);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Integer id, 
                                        @RequestBody Map<String, String> body) {
        return clientRepository.findById(id)
                .map(client -> {
                    if (body.containsKey("additional_info")) {
                        client.setAdditionalInfo(body.get("additional_info"));
                        clientRepository.save(client);
                    }
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}