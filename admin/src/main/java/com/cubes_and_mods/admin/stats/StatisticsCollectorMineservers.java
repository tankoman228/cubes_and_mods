package com.cubes_and_mods.admin.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cubes_and_mods.admin.ClientToOthers;
import com.cubes_and_mods.admin.db.Machine;

@Component
public class StatisticsCollectorMineservers {

    private final static String FILE_PATH = "statistics/mineservers/"; 
	@Autowired
    private ClientToOthers client;

    @Value("${statistics.collect.intervalms}")
    private long collectIntervalms;

    @Scheduled(fixedRateString = "${statistics.collect.intervalms}")
    public void collectStatistics2() {
    	
    	new File(FILE_PATH).mkdirs();
    	System.out.print("COLLECTING OF STATS (mine)");	
    	
        var mineservers = client.getAllMineservers();
        
        for (var m: mineservers) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + m.getId() + ".csv", true))) {
                
            long timestamp = System.currentTimeMillis();
            writer.write(
            		String.format("%s,%d,%d\n",
                            new java.util.Date(timestamp),
                    m.getSecondsWorking(),
                    m.getMemoryUsed()
                    ));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
