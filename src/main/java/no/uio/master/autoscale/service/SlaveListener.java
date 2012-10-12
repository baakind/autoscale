package no.uio.master.autoscale.service;


import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.slave.message.BreachMessage;
import no.uio.master.autoscale.slave.net.Communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen for messages comming from the slave(s)
 * @author andreas
 */
public class SlaveListener {

	private static Logger LOG = LoggerFactory.getLogger(SlaveListener.class);
	
	private static Communicator communicator;
	
	public SlaveListener() {
		LOG.debug("Initialize Slave-listener");
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
	}
	
	/**
	 * Listen for incomming messages
	 */
	public void listenForMessage() {
		LOG.debug("Listen for incomming messages...");
		Object msg = communicator.readMessage();
		performAction(msg);
	}

	/**
	 * Perform action upon received object
	 * @param msg
	 */
	public void performAction(Object msg) {
		BreachMessage<?> breachMessage = (BreachMessage<?>)msg;
		LOG.debug("Message read: " + breachMessage);
	}
	

}
