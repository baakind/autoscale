package no.uio.master.autoscale.service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.slave.message.BreachMessage;
import no.uio.master.autoscale.slave.net.Communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen for messages coming from the slave(s)
 * @author andreas
 */
public class SlaveListener {

	private static Logger LOG = LoggerFactory.getLogger(SlaveListener.class);
	private static Communicator communicator;
	
	/**
	 * Contains the current batch of breachMessages received.<br>
	 * The slaveListener always append breachMessages to this map.<br>
	 * <br>
	 * The algorithm should start counting n-seconds from the first message 
	 * received, and after n-seconds, it should empty the batch, and start 
	 * calculating which node(s) should be scaled up/down.<br>
	 * <br>
	 * The Date (key) is the date.now of the master-server when the message was received.
	 */
	private Map<Date,BreachMessage<?>> batchedBreachMessages = new HashMap<Date,BreachMessage<?>>();
	
	public SlaveListener() {
		LOG.debug("Initialize Slave-listener");
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
	}
	
	/**
	 * Listen for incoming messages
	 */
	public void listenForMessage() {
		LOG.debug("Listen for incomming messages...");
		Object msg = communicator.readMessage();
		storeMessage(msg);
	}

	/**
	 * Store received message into a batch-map, where current date is key<br>
	 * Important! - The date is the current date locally for the master-server!
	 * @param msg
	 */
	public void storeMessage(Object msg) {
		BreachMessage<?> breachMessage = (BreachMessage<?>)msg;
		LOG.debug("Message read: " + breachMessage);
		batchedBreachMessages.put(new Date(), breachMessage);
	}
	
	/**
	 * Retrieve the full list of received breach-messages
	 * @return
	 */
	public Map<Date, BreachMessage<?>> getBatchedBreachMessages() {
		return this.batchedBreachMessages;
	}
	
	/**
	 * Empty Map of breach messages
	 */
	public void emptyBatchBreachMessageList() {
		this.batchedBreachMessages = new HashMap<Date,BreachMessage<?>>();
	}
	

}
