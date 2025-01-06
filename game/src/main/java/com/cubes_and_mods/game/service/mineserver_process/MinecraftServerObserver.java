package com.cubes_and_mods.game.service.mineserver_process;

import java.io.File;
import java.nio.file.FileSystemNotFoundException;
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
    private Instant gameStartTime; // Field to track the server run time
    
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
        this.gameStartTime = Instant.now(); // Record the start time when observer is initialized
        this.updaterInDb = updaterInDb;
        this.backupsSize = backupsSize;     
        
        EveryTick();
        
        scheduler.scheduleAtFixedRate(() -> EveryTick(), SECONDS_RATE, SECONDS_RATE, TimeUnit.SECONDS);
    }
    
    /**
     * Called every minute to check runtime and used memory
     * */
    private void EveryTick() {
    	
    	System.out.println("Observing of mineserver");
    	try {
			boolean needStop = false; 
			
			// Check disk space (always)
	    	if (!CheckMemoryLimit()) needStop = true;
	    	
			if (processHandler.isLaunched()) {
						
		    	System.out.println("Process launched, checking time");
				
				// Check runtime timeout
				if (!CheckTimeWorkingLimit()) needStop = true;
				
				// Kill if alive and if something wrong
				if (needStop) {
					processHandler.killProcess();   
					System.out.println("KILLING MINESERVER");	
				}
			}
			else {
				// Да, будет погрешность в минуту. 
				// Но иначе считаться время рантайма после остановки будет неверно
				this.gameStartTime = Instant.now(); 
				
		    	System.out.println("Process not launched. Skipping time checking");
			}
		
        } catch (Exception e) {        
            e.printStackTrace();
            System.err.println("MinecraftServerObserver GOT AN ERROR " + e.getMessage());
        }
    }
    
    /**
     * Проверяет и обновляет в базе, сколько места занимают файлы игры и бекапов.
     * 
     * Если их размер превышает допустимый (см. тариф), вернёт false
     * */
    private boolean CheckMemoryLimit() {
    	
    	long backupsLen = 0L;
    	try {
    		backupsLen = backupsSize.get(mineserver.getId());
    	}
    	catch (Exception e) {
    		System.err.println("Backups not found");
    	}
    	
    	File all = processHandler.GetFilesTree();
        long memoryUsedKB = getDirSize(all) / 1024L + backupsLen; 
        long memoryLimit = tariff.getMemoryLimit(); 
        
        mineserver.setMemoryUsed(memoryUsedKB);
        updaterInDb.update(mineserver);   
        
        return memoryUsedKB < memoryLimit;
    }
    
    /**
     * Проверяет и обновляет в базе данных время рантайма. 
     * 
     * Вернёт false, если время истекло
     * */
    private boolean CheckTimeWorkingLimit() {
    	
        long elapsedSeconds = Instant.now().getEpochSecond() - gameStartTime.getEpochSecond();
        int maxWorkSeconds = tariff.getHoursWorkMax() * 3600; 
        
        mineserver.setSecondsWorking((int) elapsedSeconds + mineserver.getSecondsWorking());
        updaterInDb.update(mineserver); 
           
		System.out.println("Seconds " + elapsedSeconds);
        this.gameStartTime = Instant.now(); 
        
        return elapsedSeconds < maxWorkSeconds;    
    }
    
    // Used for calculating size of minecraft server directory size
    long getDirSize(File dir) {
    	
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();       
            if (subFiles == null || subFiles.length == 0) {
            	return 0;
            }
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

