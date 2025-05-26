package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Server;
import com.cubes_and_mods.admin.jpa.repos.ServerRepos;
import com.cubes_and_mods.admin.security.CheckAdminService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    
    @Autowired
    private ServerRepos serverRepository;

    @Autowired
    private CheckAdminService checkAdminService;
    
    @GetMapping
    public List<Server> getAllServers(HttpServletRequest request) {
        checkAdminService.assertAllowed(request, admin -> admin.getCanServers());
        return serverRepository.findAll();
    }
    
    @PostMapping
    public Server createServer(@RequestBody Server server, HttpServletRequest request) {

        checkAdminService.assertAllowed(request, admin -> admin.getCanServers());

        // Устанавливаем свободные ресурсы при создании
        if (server.getCpuThreadsFree() == null) {
            server.setCpuThreadsFree(server.getCpuThreads());
        }
        if (server.getRamFree() == null) {
            server.setRamFree(server.getRam());
        }
        if (server.getMemoryFree() == null) {
            server.setMemoryFree(server.getMemory());
        }
        return serverRepository.save(server);
    }
    
    @PutMapping("/{id}")
    public Server updateServer(@PathVariable Integer id, @RequestBody Server serverDetails, HttpServletRequest request) {
        
        checkAdminService.assertAllowed(request, admin -> admin.getCanServers());
        
        Server server = serverRepository.findById(id).get();
        
        // Вычисляем разницу для свободных ресурсов
        var cpuDiff = serverDetails.getCpuThreads() - server.getCpuThreads();
        var ramDiff = serverDetails.getRam() - server.getRam();
        var memoryDiff = serverDetails.getMemory() - server.getMemory();
        
        // Обновляем только разрешенные поля
        server.setName(serverDetails.getName());
        server.setAddress(serverDetails.getAddress());
        server.setCpuName(serverDetails.getCpuName());
        server.setCpuThreads(serverDetails.getCpuThreads());
        server.setRam(serverDetails.getRam());
        server.setMemory(serverDetails.getMemory());
        
        // Обновляем свободные ресурсы
        server.setCpuThreadsFree((short) (server.getCpuThreadsFree() + cpuDiff));
        server.setRamFree((short) (server.getRamFree() + ramDiff));
        server.setMemoryFree(server.getMemoryFree() + memoryDiff);
        
        return serverRepository.save(server);
    }    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Integer id, HttpServletRequest request) {

        checkAdminService.assertAllowed(request, admin -> admin.getCanServers());

        Server server = serverRepository.findById(id).get();
        
        serverRepository.delete(server);
        return ResponseEntity.ok().build();
    }
}