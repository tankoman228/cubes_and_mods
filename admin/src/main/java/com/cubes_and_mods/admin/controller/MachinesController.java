package com.cubes_and_mods.admin.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.ApiClient;
import com.cubes_and_mods.admin.db.Machine;


/**
 * API for GETTING stats about machines
*/
@RestController
public class MachinesController {
	
	private final static String FILE_PATH = "statistics/machines/"; 
	
	@Autowired
    private ApiClient client;

    @GetMapping("/api/machines")
    public List<Machine> getAllMachines() {
        return client.getAllMachines();
    }

    @GetMapping("/api/machines/{id}/stats")
    public List<Map<String, Object>> getMachineStats(@PathVariable Integer id) {
    	
        List<Map<String, Object>> stats = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH + id + ".csv"))) {
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
        } catch (IOException e) {}

        if (stats.isEmpty()) {
            return Collections.singletonList(createStatEntry(System.currentTimeMillis(), 0L, 0, 0));
        }
        
        return stats;
    }

    
    private Map<String, Object> createStatEntry(long timestamp, long memoryUsed, int freeCpuThreads, int ramFree) {
    	
        Map<String, Object> entry = new HashMap<>();
        entry.put("timestamp", timestamp);
        entry.put("memoryFree", memoryUsed);
        entry.put("cpuThreadsFree", freeCpuThreads);
        entry.put("ramFree", ramFree);
        return entry;
    }
}
