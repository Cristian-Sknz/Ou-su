package me.skiincraft.discord.ousu.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OusuScheduler {

	public void runTask(String threadname, Runnable runnable) {
		Thread thread = new Thread(runnable, threadname);
		thread.start();
	}
	
	/** 
	 * @param 15L == 1 second  
	 * 
	 */
	public void runDelayedTask(Runnable runnable, long delay) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.schedule(runnable, delay*50, TimeUnit.MILLISECONDS);
	}
	
	
	/** 
	 * @param 15L == 1 second  
	 * 
	 */
	public void runRepetitiveTask(Runnable runnable, long start, long delay) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(runnable, start*50, delay*50, TimeUnit.MILLISECONDS);
	}
	
	
}
