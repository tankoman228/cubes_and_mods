package com.cubes_and_mods.game.service.mineserver_process;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Tariff;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;

/**
 * Every X minutes observers game process, updates about data in database
 * */
import java.time.Instant;

/**
 * Every X seconds observers game process, updates about data in database
 * */
public class MinecraftServerObserver {

    private static final int SECONDS_RATE = 60;
    
    private IMinecraftHandler processHandler;
    private ScheduledExecutorService scheduler;
    private Mineserver mineserver;
    private Tariff tariff;
    private Instant serverStartTime; // Field to track the server run time
    
    private static ReposTariff reposTariff;
    private static ReposMineserver reposMineserver;


    public MinecraftServerObserver(IMinecraftHandler processHandler, ReposTariff reposTariff_, ReposMineserver reposMineserver_) {
    	
        reposTariff = reposTariff_;
        reposMineserver = reposMineserver_;
    	
        this.processHandler = processHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);        
        this.mineserver = processHandler.getMineserver();
        this.tariff = reposTariff.getReferenceById(mineserver.getIdTariff());
        this.serverStartTime = Instant.now(); // Record the start time when observer is initialized
             
        EveryTick();
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                EveryTick();
            } catch (Exception e) {        
                e.printStackTrace();
                System.out.println("ERROR. MinecraftServerObserver GOT AN ERROR");
            }
        }, SECONDS_RATE, SECONDS_RATE, TimeUnit.SECONDS);
    }
    
    private void EveryTick() {
    	
        if (processHandler.isLaunched())
        	if (!CheckMemoryLimit() || !CheckTimeWorkingLimit())
        		processHandler.killProcess();   
    }
    
    private boolean CheckMemoryLimit() {
    	
        long memoryUsedKB = processHandler.GetFilesTree().getTotalSpace() / 1024; 
        int memoryLimit = tariff.getMemoryLimit(); 
        
        mineserver.setMemoryUsed((int) memoryUsedKB);
        reposMineserver.save(mineserver);
        
        //TODO: учесть бекапы при рассчёте занятого места
        
        return memoryUsedKB < memoryLimit;
    }
    
    private boolean CheckTimeWorkingLimit() {
    	
        long elapsedSeconds = Instant.now().getEpochSecond() - serverStartTime.getEpochSecond();
        int maxWorkSeconds = tariff.getHoursWorkMax() * 3600; 
        
        mineserver.setSecondsWorking((int) elapsedSeconds);
        reposMineserver.save(mineserver);
        
        return elapsedSeconds < maxWorkSeconds;    
    }
}

