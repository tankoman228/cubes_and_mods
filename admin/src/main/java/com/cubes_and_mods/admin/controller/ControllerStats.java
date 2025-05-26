package com.cubes_and_mods.admin.controller;

import java.util.*;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.admin.jpa.Host;
import com.cubes_and_mods.admin.jpa.Server;
import com.cubes_and_mods.admin.jpa.repos.HostRepos;
import com.cubes_and_mods.admin.jpa.repos.ServerRepos;
import com.cubes_and_mods.admin.security.CheckAdminService;
import com.cubes_and_mods.admin.service.ServiceStatsHosts;
import com.cubes_and_mods.admin.service.ServiceStatsServers;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.*;


@RestController
@RequestMapping("/api/stats")
public class ControllerStats {

    private static final String HOST_FOLDER   = ServiceStatsHosts.statsFolder;
    private static final String SERVER_FOLDER = ServiceStatsServers.statsFolder;
    
    @Autowired
    private ServerRepos serverRepos;

    @Autowired
    private HostRepos hostRepos;

    @Autowired
    private CheckAdminService checkAdminService;


    public static class StatsDto {

        public Object target; // Либо server, либо host
        public List<Map<String, Object>> stats; // Сам ряд значений из таблицы
    }


    @GetMapping("/server")
    public List<StatsDto> getMachineStats(HttpServletRequest request) {
    	
        checkAdminService.assertAllowed(request, admin -> admin.getCanViewStats());
        System.out.println("ControllerStats.getMachineStats()");
        List<StatsDto> stats = new ArrayList<>();
        for (Server server : serverRepos.findAll()) {
            var dto = new StatsDto();
            dto.target = server;
            dto.stats = makeEntriesForServer(server);
            stats.add(dto);
        }
        return stats;
    }
    

    @GetMapping("/host")
    public List<StatsDto> getHostStats(HttpServletRequest request) {
    	
        checkAdminService.assertAllowed(request, admin -> admin.getCanViewStats());
        List<StatsDto> stats = new ArrayList<>();
        for (Host host : hostRepos.findAll()) {
            var dto = new StatsDto();
            dto.target = host;
            dto.stats = makeEntriesForHost(host);
            stats.add(dto);
        }
        return stats;
    }
    
    private List<Map<String, Object>> makeEntriesForServer(Server server) {

        List<Map<String, Object>> stats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVER_FOLDER  + server.getId() + ".csv"))) {         
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                Map<String, Object> entry = new HashMap<>();
                entry.put("timestamp", parts[0]);
                entry.put("ramFree", parts[1]);
                entry.put("memoryFree", parts[2]);
                entry.put("cpuThreadsFree", parts[3]);
                stats.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private List<Map<String, Object>> makeEntriesForHost(Host host) {

        List<Map<String, Object>> stats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOST_FOLDER  + host.getId() + ".csv"))) {         
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                Map<String, Object> entry = new HashMap<>();
                entry.put("timestamp", parts[0]);
                entry.put("seconds_working", parts[1]);
                entry.put("memory_used", parts[2]);
                stats.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;
    }
}