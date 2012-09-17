package org.uio.autoscale.service;

import java.io.IOException;

/**
 * The runnable instance of Autoscale, for external usage.
 * @author toraba
 *
 */

//@Singleton
public class AutoscaleDaemon implements Runnable {
	//private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);

    private static final AutoscaleDaemon instance = new AutoscaleDaemon();
    
    
	
	public AutoscaleDaemon() {
		Thread thread = new Thread(instance);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("TEST");
		//LOG.debug("Run something here");
	}
	
	
	public void init(String[] arguments) throws IOException {
        setup();
    }
	protected void setup() throws IOException {
		
	}
}
