package org.uio.autoscale.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uio.autoscale.Daemon;

/**
 * The runnable instance of Autoscale, for external usage.
 * @author toraba
 *
 */

//@Singleton
public class AutoscaleDaemon {
	private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);
   // private static final AutoscaleDaemon instance = new AutoscaleDaemon();
    private ScheduledExecutorService executor;
    private static final Daemon daemon = new Daemon();
	
	public AutoscaleDaemon() {
		LOG.debug("************** Autoscale Daemon Initialized **********");
		System.out.println("***************** AUTOSCALE *****************");
		
		executor = Executors.newSingleThreadScheduledExecutor();
		
		executor.scheduleAtFixedRate(daemon, 0, 1, TimeUnit.SECONDS);
//		Thread thread = new Thread(instance);
//		thread.setDaemon(true);
//		thread.setName("Autoscale daemon");
//		thread.start();
	}
}
