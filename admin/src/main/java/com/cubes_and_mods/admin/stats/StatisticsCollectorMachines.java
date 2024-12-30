package com.cubes_and_mods.admin.stats;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.cubes_and_mods.admin.ClientToOthers;
import com.cubes_and_mods.admin.db.Machine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class StatisticsCollectorMachines {

    private final static String FILE_PATH = "statistics/machines/"; 
    private final ClientToOthers client = new ClientToOthers();

    @Value("${statistics.collect.intervalms}")
    private long collectIntervalms;

    @Scheduled(fixedRateString = "${statistics.collect.intervalms}")
    public void collectStatistics() {
    	
    	new File(FILE_PATH).mkdirs();
    	System.out.print("COLLECTING OF STATS");
    	
        List<Machine> machines = client.getAllMachines();
        
        for (var machine: machines) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + machine.getId() + ".csv", true))) {
                
            long timestamp = System.currentTimeMillis();
            writer.write(
            		String.format("%s,%d,%d,%d\n",
                    new java.util.Date(timestamp),
                    machine.getRamFree(),
                    machine.getMemoryFree(),
                    machine.getCpuThreadsFree()));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
