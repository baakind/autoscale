package org.uio.autoscale.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runnable instance of Autoscale, for external usage.
 * @author toraba
 *
 */

//@Singleton
public class AutoscaleDaemon implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);
    private static final AutoscaleDaemon instance = new AutoscaleDaemon();
    
    
	
	public AutoscaleDaemon() {
		LOG.debug("************** Autoscale Daemon Initialized **********");
		System.out.println("***************** AUTOSCALE *****************");
		Thread thread = new Thread(instance);
		thread.setDaemon(true);
		thread.setName("Autoscale daemon");
		thread.start();
	}

	@Override
	public void run() {
		LOG.debug("************** Autoscale Daemon Initialized RUN **********");
		System.out.println("***************** AUTOSCALE RUN *****************");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//LOG.debug("Run something here");
	}
	
	
	public void init(String[] arguments) throws IOException {
        setup();
    }
	protected void setup() throws IOException {
		LOG.debug("************** Autoscale Daemon Initialized SETUP **********");
		System.out.println("***************** AUTOSCALE SETUP *****************");
	}
}
