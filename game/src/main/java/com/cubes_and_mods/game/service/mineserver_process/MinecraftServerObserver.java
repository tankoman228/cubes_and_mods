package com.cubes_and_mods.game.service.mineserver_process;

import java.io.File;
/**
 * Every X minutes observers game process, updates about data in database
 * */
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Tariff;

/**
 * Every X seconds observers game process, updates in database about runtime and used disk space
 * */
public class MinecraftServerObserver {

    private static final int SECONDS_RATE = 60;
    
    private IMinecraftHandler processHandler;
    private ScheduledExecutorService scheduler;
    private Mineserver mineserver;
    private Tariff tariff;
    private Instant observerStartTime; // Field to track the server run time
    
    private MineserverUpdater updaterInDb;
    private BackupLengthGetter backupsSize;
    
    public MinecraftServerObserver(
    		IMinecraftHandler processHandler, 
    		Tariff tariff, 
    		MineserverUpdater updaterInDb, 
    		BackupLengthGetter backupsSize) {
    	
        this.processHandler = processHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);        
        this.mineserver = processHandler.getMineserver();
        this.tariff = tariff;
        this.observerStartTime = Instant.now(); // Record the start time when observer is initialized
        this.updaterInDb = updaterInDb;
        this.backupsSize = backupsSize;     
        
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
    
    /**
     * Called every minute to check runtime and used memory
     * */
    private void EveryTick() {
    	
		System.out.println("Every tick (observer)");
        
		boolean bad = false;
		
    	if (!CheckMemoryLimit()) bad = true;
		if (processHandler.isLaunched() && !CheckTimeWorkingLimit()) bad = true;
		
		if (bad) {
			processHandler.killProcess();   
			System.out.println("KILLING MINESERVER");	
		}
    }
    
    private boolean CheckMemoryLimit() {
    	
    	File all = processHandler.GetFilesTree();
        long memoryUsedKB = getDirSize(all) / 1024 + backupsSize.get(mineserver.getId()); 
        long memoryLimit = tariff.getMemoryLimit(); 
        
        mineserver.setMemoryUsed(memoryUsedKB);
        updaterInDb.update(mineserver);   
        
        return memoryUsedKB < memoryLimit;
    }
    
    private boolean CheckTimeWorkingLimit() {
    	
        long elapsedSeconds = Instant.now().getEpochSecond() - observerStartTime.getEpochSecond();
        int maxWorkSeconds = tariff.getHoursWorkMax() * 3600; 
        
        mineserver.setSecondsWorking((int) elapsedSeconds + 1488);
        updaterInDb.update(mineserver); 
        
		System.out.println("Seconds " + elapsedSeconds);
        
        return elapsedSeconds < maxWorkSeconds;    
    }
    
    // Used for calculating size of minecraft server directory size
    long getDirSize(File dir) {
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();
            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        }
        return size;
    }
    
    /**
     * For callback TO SAVE INFO IN DB
     * (difficult lifecycle of spring repositories make me to do update in the higher layer)
     * */
    @FunctionalInterface
    public interface MineserverUpdater {
        void update(Mineserver mineserver);
    }
    
    /**
     * For getting SUM OF BACKUPS SIZE (for calculating memory usage)
     * (difficult lifecycle of spring repositories make me to do update in the higher layer)
     * */
    @FunctionalInterface
    public interface BackupLengthGetter {
        long get(int id_mineserver);
    }
}

