package mineserver_process;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Every X minutes observers game process, updates about data in database
 * */
public class MinecraftServerObserver {

	private static final int MINUTES_RATE = 1;
	
	private IMinecraftHandler processHandler;
	private ScheduledExecutorService scheduler;
	
	
	public MinecraftServerObserver(IMinecraftHandler processHandler) {
		
		this.processHandler = processHandler;
		this.scheduler = Executors.newScheduledThreadPool(1);		
		
		scheduler.scheduleAtFixedRate(() -> {
            try {
            	
            } catch (Exception e) {        
                e.printStackTrace();
                System.out.println("ERROR. MinecraftServerObserver GOT AN ERROR");
            }
        }, 0, MINUTES_RATE, TimeUnit.MINUTES);
	}
	
	private void EveryTick() {
		// TODO: Make some logic: time, disk space
	}
}
