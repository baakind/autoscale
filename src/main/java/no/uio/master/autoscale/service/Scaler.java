package no.uio.master.autoscale.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import no.uio.master.autoscale.slave.message.BreachMessage;
import no.uio.master.autoscale.slave.message.enumerator.BreachType;
import no.uio.master.autoscale.util.ScalerUtils;

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
	private static ScalerUtils scalerUtils = new ScalerUtils();
	
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
		// Use this implementation?
		//Map<String,Map<BreachType, Integer>> messageCountPerHost = countMessagesPerHost();
		
		//Use this scoring-implementation?
		Map<String, Integer> scoredResults = performScoring();
		//TODO: Implement calculation and scaling-functionality here!
	}
	
	/**
	 * Perform weighting of the messages.<br>
	 * Results in a map containing <tt>node</tt> and <tt>score</tt><br>
	 * A positive score means the node needs to be scaled up.<br>
	 * A negative score means the node needs ot be scaled down.<br>
	 * @return
	 */
	private static Map<String, Integer> performScoring() {
		Map<String, Integer> map = new HashMap<String, Integer>(0);
		
		// Iterate all breach-messages
		for (Iterator<Entry<Date, BreachMessage<?>>> iterator = breachMessages.entrySet().iterator(); iterator.hasNext();) {
			Entry<Date, BreachMessage<?>> entry = iterator.next();
			Date reportedTimeDate = entry.getKey();
			BreachMessage<?> message = entry.getValue();
			String senderHost = message.getSenderHost();
			
			Integer value = scalerUtils.getPriorityOfBreachType(message.getType());
			
			// Sender host doesn't exist, create
			if(!map.containsKey(senderHost)) {
				map.put(senderHost, value);
			} else {
				value += map.get(senderHost);
				map.put(senderHost, value);
			}
		}
		
		return map;
	}
	
	/**
	 * Count messages per host. Map structure:<br>
	 * &nbsp; <tt>Host.BreachType.Count</tt><br>
	 * <br>
	 * Important: The reported time is a timestamp of when the message was recorded locally,
	 * not when the message was sent from slave.
	 * @return
	 */
	private static Map<String, Map<BreachType, Integer>> countMessagesPerHost() {
		Map<String, Map<BreachType, Integer>> map = new HashMap<String, Map<BreachType, Integer>>();
		
		// Iterate all breach-messages
		for (Iterator<Entry<Date, BreachMessage<?>>> iterator = breachMessages.entrySet().iterator(); iterator.hasNext();) {
			Entry<Date, BreachMessage<?>> entry = iterator.next();
			Date reportedTimeDate = entry.getKey();
			BreachMessage<?> message = entry.getValue();
			
			// Create mapping for senderHost if it doesn't exist
			String senderHost = message.getSenderHost();
			if(!map.containsKey(senderHost)) {
				map.put(senderHost, new HashMap<BreachType, Integer>());
			}
			
			// Map contains record of current breach-message, increase counter
			if(map.get(senderHost).containsKey(message.getType())) {
				Integer value = map.get(senderHost).get(message.getType());
				map.get(senderHost).put(message.getType(), ++value);
			} else { // Map does not contain record of current breach-message, initiate
				map.get(senderHost).put(message.getType(), 1);
			}
			
		}
		
		return map;
	}

}
