package com.cubes_and_mods.admin.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.admin.jpa.repos.HostRepos;
import com.cubes_and_mods.admin.jpa.repos.ServerRepos;

@Service
public class ServiceStatsHosts {

    public static final String statsFolder = "hosts_stats/";

    @Autowired
    private HostRepos hostRepos;

    @Scheduled(fixedRateString = "${statistics.collect.intervalms}")
    public void collect() {

        new File(statsFolder).mkdirs();

        var hosts = hostRepos.findAll();
        
        for (var host: hosts) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFolder + host.getId() + ".csv", true))) {
                
            long timestamp = System.currentTimeMillis();
            writer.write(
            		String.format("%s,%d,%d\n",
                            new java.util.Date(timestamp),
                    host.getSecondsWorking(),
                    host.getMemoryUsed()
                    ));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
