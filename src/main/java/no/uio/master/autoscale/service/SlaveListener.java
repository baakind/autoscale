package no.uio.master.autoscale.service;


import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.slave.message.SlaveMessage;
import no.uio.master.autoscale.slave.net.Communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen for messages comming from the slave(s)
 * @author andreas
 */
public class SlaveListener implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(SlaveListener.class);
	
	private static Communicator communicator;
	
	public SlaveListener() {
		LOG.debug("Initialize Slave-listener");
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
	}
	
	@Override
	public void run() {
		LOG.debug("Listen for incomming slave-messages...");
		//SlaveMessage msg = (SlaveMessage)communicator.readMessage();
		Object msg = communicator.readMessage();
		performAction(msg);
	}

	public void performAction(Object msg) {
		LOG.debug("Message read: " + (String)msg);
	}
	

}
