package no.uio.master.autoscale.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import no.uio.master.autoscale.slave.message.BreachMessage;
import no.uio.master.autoscale.slave.message.enumerator.BreachType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The actual scaler.<br>
 * It gathers all breach-messages from current batch, and 
 * determine if any nodes should be scaled up/down, whom etc
 * @author andreas
 */
public class Scaler implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(Scaler.class);
	private SlaveListener slaveListener;
	
	private static Map<Date, BreachMessage<?>> breachMessages;
	
	public Scaler(SlaveListener listener) {
		LOG.debug("Initialize scaler");
		slaveListener = listener;
	}

	@Override
	public void run() {
		LOG.debug("Scaler running");
		collectBreachMessages();
		performCalculation();
	}
	
	/**
	 * Collect and empty breach-messages from slave-listener
	 */
	private void collectBreachMessages() {
		breachMessages = slaveListener.getBatchedBreachMessages();
		LOG.debug("Batch received, number of messages:" + breachMessages.size());
		slaveListener.emptyBatchBreachMessageList();
	}

	/**
	 * Perform calculation upon collected breach-messages, and eventually other conditions 
	 * which may be of interest.
	 */
	private void performCalculation() {
		Map<BreachType, Integer> messageCount = countMessages();
		
		//TODO: Implement calculation and scaling-functionality here!
	}
	
	
	/**
	 * Count different BreachMessageTypes, and return a map containing how many message received of 
	 * different types.
	 * @return
	 */
	private Map<BreachType, Integer> countMessages() {
		Map<BreachType, Integer> map = new HashMap<BreachType, Integer>();
		
		for (Iterator<Entry<Date, BreachMessage<?>>> iterator = breachMessages.entrySet().iterator(); iterator.hasNext();) {
			Entry<Date, BreachMessage<?>> entry = iterator.next();
			Date reportDate = entry.getKey();
			BreachMessage<?> message = entry.getValue();
			
			// Map already contains key, increase counter
			if(map.containsKey(message.getType())) {
				Integer value = map.get(message.getType());
				map.put(message.getType(), ++value);
			}
			// New value
			else {
				map.put(message.getType(), 1);
			}
		}
		
		return map;
	}
}
