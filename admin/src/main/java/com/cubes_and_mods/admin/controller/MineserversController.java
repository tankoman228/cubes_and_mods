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

import com.cubes_and_mods.admin.ClientToOthers;
import com.cubes_and_mods.admin.db.Machine;
import com.cubes_and_mods.admin.db.Mineserver;

@RestController
public class MineserversController {

	@Autowired
    private ClientToOthers client;
    private final static String FILE_PATH = "statistics/mineservers/"; 
    
    @GetMapping("js/api/minesevers")
    public List<Mineserver> getAll() {
        return client.getAllMineservers();
    }
    @GetMapping("/api/minesevers")
    public List<Mineserver> getAllъ() {
        return client.getAllMineservers();
    }

    @GetMapping("/api/mineservers/{id}/stats")
    public List<Map<String, Object>> getStats(@PathVariable Integer id) {
    	
        List<Map<String, Object>> stats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH + id + ".csv"))) {
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
            //e.printStackTrace();
        }


        if (stats.isEmpty()) {
            return Collections.singletonList(createStatEntry());
        }
        
        return stats;
    }

    
    private Map<String, Object> createStatEntry() {
    	
        Map<String, Object> entry = new HashMap<>();
        entry.put("timestamp", System.currentTimeMillis());
        entry.put("secondsWorking", 0);
        entry.put("memoryUsed", 0);
        return entry;
    }
}
