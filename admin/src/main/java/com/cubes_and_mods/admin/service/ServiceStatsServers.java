package com.cubes_and_mods.admin.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.admin.jpa.repos.ServerRepos;

@Service
public class ServiceStatsServers {

    public static final String statsFolder = "servers_stats/";

    @Autowired
    private ServerRepos serverRepos;

    @Scheduled(fixedRateString = "${statistics.collect.intervalms}")
    public void collect() {

        new File(statsFolder).mkdirs();

        var servers = serverRepos.findAll();
        
        for (var server: servers) {
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFolder + server.getId() + ".csv", true))) {
                
                long timestamp = System.currentTimeMillis();

                DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT; 
                String isoDate = Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC).format(formatter);

                writer.write(
                        String.format("%s,%d,%d,%d\n",
                        isoDate,
                        server.getRamFree(),
                        server.getMemoryFree(),
                        server.getCpuThreadsFree()));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
