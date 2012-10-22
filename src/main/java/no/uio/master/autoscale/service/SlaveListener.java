package no.uio.master.autoscale.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.message.BreachMessage;
import no.uio.master.autoscale.net.Communicator;

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
	 * The slaveListener always append breachMessages to this list.<br>
	 * <br>
	 * The algorithm should start counting n-seconds from the first message 
	 * received, and after n-seconds, it should empty the batch, and start 
	 * calculating which node(s) should be scaled up/down.<br>
	 * <br>
	 * The current date is also appended to the BreachMessage, and represents the date 
	 * the breachMessage was received.
	 */
	
	private static List<BreachMessage<?>> batchedBreachMessages = new ArrayList<BreachMessage<?>>();
	
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
	 * Store received batch-messages in message-list<br>
	 * Also append received-date to message.
	 * @param msg
	 */
	public void storeMessage(Object msg) {
		BreachMessage<?> breachMessage = (BreachMessage<?>)msg;
		LOG.debug("Message read: " + breachMessage);
		breachMessage.setDate(new Date());
		batchedBreachMessages.add(breachMessage);
	}
	
	/**
	 * Retrieve the full list of received breach-messages
	 * @return
	 */
	public List<BreachMessage<?>> getBatchedBreachMessages() {
		return this.batchedBreachMessages;
	}
	
	/**
	 * Empty breach-messages list
	 */
	public void emptyBatchBreachMessageList() {
		this.batchedBreachMessages = new ArrayList<BreachMessage<?>>();
	}
	

}
