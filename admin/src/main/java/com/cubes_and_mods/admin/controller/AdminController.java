package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Admin;
import com.cubes_and_mods.admin.jpa.repos.AdminRepos;
import com.cubes_and_mods.admin.security.CheckAdminService;
import com.cubes_and_mods.admin.service.PasswordHash;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/admins")
public class AdminController {
    
    @Autowired
    private AdminRepos adminRepository;

    @Autowired
    private PasswordHash passwordHash;

    @Autowired
    private CheckAdminService checkAdminService;

    @GetMapping
    public List<Admin> getAllAdmins(HttpServletRequest request) {

        checkAdminService.assertAllowed(request, admin -> admin.getCanAdmins()); 

        var list = adminRepository.findAll();
        list.forEach(admin -> {
            admin.setPasswordHash(null);
        });
        return list;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> put(@RequestBody Admin entity, @PathVariable Integer id, HttpServletRequest request) {

        checkAdminService.assertAllowed(request, admin -> admin.getCanAdmins()); 
        
        var admin = adminRepository.findById(id).get();

        admin.setUsername(admin.getUsername());
        if (admin.getPasswordHash() != "" && admin.getPasswordHash() != null) {
            admin.setPasswordHash(passwordHash.hash(entity.getPasswordHash(), id));
        }

        admin.setCanViewStats(entity.getCanViewStats());
        admin.setCanViewLogs(entity.getCanViewLogs());
        admin.setCanClients(entity.getCanClients());
        admin.setCanHosts(entity.getCanHosts());
        admin.setCanOrders(entity.getCanOrders());
        admin.setCanServers(entity.getCanServers());
        admin.setCanMonitorSrv(entity.getCanMonitorSrv());
        admin.setCanTechSupport(entity.getCanTechSupport());
        admin.setCanTariffs(entity.getCanTariffs());
        admin.setCanAdmins(entity.getCanAdmins());

        adminRepository.saveAndFlush(admin);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity<Void> post(@RequestBody Admin entity, HttpServletRequest request) {

        checkAdminService.assertAllowed(request, admin -> admin.getCanAdmins()); 

        var admin = new Admin();
        admin.setUsername(entity.getUsername());
    
        admin.setCanViewStats(entity.getCanViewStats());
        admin.setCanViewLogs(entity.getCanViewLogs());
        admin.setCanClients(entity.getCanClients());
        admin.setCanHosts(entity.getCanHosts());
        admin.setCanOrders(entity.getCanOrders());
        admin.setCanServers(entity.getCanServers());
        admin.setCanMonitorSrv(entity.getCanMonitorSrv());
        admin.setCanTechSupport(entity.getCanTechSupport());
        admin.setCanTariffs(entity.getCanTariffs());
        admin.setCanAdmins(entity.getCanAdmins());

        adminRepository.saveAndFlush(admin);
        admin.setPasswordHash(passwordHash.hash(entity.getPasswordHash(), admin.getId()));

        adminRepository.saveAndFlush(admin);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
}
