package com.cubes_and_mods.game.service.mineserver_process;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Tariff;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;

import jakarta.transaction.Transactional;

/**
 * Every X minutes observers game process, updates about data in database
 * */
import java.time.Instant;

/**
 * Every X seconds observers game process, updates about data in database
 * */
public class MinecraftServerObserver {

    private static final int SECONDS_RATE = 6;
    
    private IMinecraftHandler processHandler;
    private ScheduledExecutorService scheduler;
    private Mineserver mineserver;
    private Tariff tariff;
    private Instant serverStartTime; // Field to track the server run time
    MineserverUpdater updaterInDb;

    public MinecraftServerObserver(IMinecraftHandler processHandler, Tariff tariff, MineserverUpdater updaterInDb) {
    	
        this.processHandler = processHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);        
        this.mineserver = processHandler.getMineserver();
        this.tariff = tariff;
        this.serverStartTime = Instant.now(); // Record the start time when observer is initialized
        this.updaterInDb = updaterInDb;
             
        EveryTick();
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                EveryTick();
            } catch (Exception e) {        
                e.printStackTrace();
                System.out.println("ERROR. MinecraftServerObserver GOT AN ERROR " + e.getMessage());
            }
        }, SECONDS_RATE, SECONDS_RATE, TimeUnit.SECONDS);
    }
    
    @Transactional
    private void EveryTick() {
        if (processHandler.isLaunched())
        	if (!CheckMemoryLimit() || !CheckTimeWorkingLimit())
        		processHandler.killProcess();   
    }
    
    private boolean CheckMemoryLimit() {
    	
        long memoryUsedKB = processHandler.GetFilesTree().getTotalSpace() / 1024; 
        int memoryLimit = tariff.getMemoryLimit(); 
        
        mineserver.setMemoryUsed((int) memoryUsedKB);
        
        updaterInDb.update(mineserver); 
        
        //TODO: учесть бекапы при рассчёте занятого места
        
        return memoryUsedKB < memoryLimit;
    }
    
    private boolean CheckTimeWorkingLimit() {
    	
        long elapsedSeconds = Instant.now().getEpochSecond() - serverStartTime.getEpochSecond();
        int maxWorkSeconds = tariff.getHoursWorkMax() * 3600; 
        
        mineserver.setSecondsWorking((int) elapsedSeconds + 1488);
        updaterInDb.update(mineserver); 
        
        return elapsedSeconds < maxWorkSeconds;    
    }
    
    
    @FunctionalInterface
    public interface MineserverUpdater {
        void update(Mineserver mineserver);
    }
}

