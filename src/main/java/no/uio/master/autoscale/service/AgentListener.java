package no.uio.master.autoscale.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.host.CassandraHost;
import no.uio.master.autoscale.message.AgentMessage;
import no.uio.master.autoscale.message.BreachMessage;
import no.uio.master.autoscale.message.enumerator.AgentStatus;
import no.uio.master.autoscale.net.Communicator;
import no.uio.master.autoscale.util.CommunicatorObjectBundle;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen for messages coming from the slave(s)
 * 
 * @author andreas
 */
public class AgentListener {

	private static Logger LOG = LoggerFactory.getLogger(AgentListener.class);
	private Communicator communicator;

	/**
	 * Contains the current batch of breachMessages received.<br>
	 * The slaveListener always append breachMessages to this list.<br>
	 * <br>
	 * The algorithm should start counting n-seconds from the first message
	 * received, and after n-seconds, it should empty the batch, and start
	 * calculating which node(s) should be scaled up/down.<br>
	 * <br>
	 * The current date is also appended to the BreachMessage, and represents
	 * the date the breachMessage was received.
	 */

	private static List<BreachMessage<?>> batchedBreachMessages = new ArrayList<BreachMessage<?>>();

	public AgentListener() {
	}

	/**
	 * Listen for incoming messages
	 */
	public void listenForMessage() {
		LOG.debug("Listen for incomming messages...");
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
		CommunicatorObjectBundle obj = (CommunicatorObjectBundle) communicator.readMessage();
		communicator = null;

		try {
			AgentMessage msg = (AgentMessage) obj.getMessage();
			if(null == msg) {
				LOG.error("Failed to parse message");
				return;
			}
			
			msg.setSenderHost(obj.getSenderIp());
			LOG.debug("Read message: " + msg);
			
			switch (msg.getType()) {
			case BREACH_MESSAGE:
				storeMessage(msg);
				break;
			case STATUS:
				updateStatus(msg);
				break;
			default:
				LOG.warn("Status not implemented {}",msg.getType().toString());
				break;
			}
		} catch (Exception e) {
			LOG.error("Failed to read message ",e);
		}
	}

	/**
	 * Store received batch-messages in message-list<br>
	 * Also append received-date to message.
	 * 
	 * @param msg
	 */
	public void storeMessage(AgentMessage msg) {
		LOG.debug("Store breach-message");
		if (msg.getMap().isEmpty()) {
			LOG.debug("No messages stored.");
			return;
		}

		BreachMessage<?> breachMessage = (BreachMessage<?>) msg.getMap().entrySet().iterator().next().getValue();
		breachMessage.setSenderHost(msg.getSenderHost());
		
		LOG.debug("Message read: " + breachMessage);
		breachMessage.setDate(new Date());
		batchedBreachMessages.add(breachMessage);
	}

	/**
	 * Update status
	 * 
	 * @param msg
	 */
	@SuppressWarnings("unchecked")
	public void updateStatus(AgentMessage msg) {
		LOG.debug("Update status");
		if (msg.getMap().isEmpty()) {
			LOG.debug("No messages stored.");
			return;
		}
		Entry<String, Object> entry = msg.getMap().entrySet().iterator().next();

		AgentStatus status = AgentStatus.valueOf(entry.getKey());

		switch (status) {
		case LIVE_NODES:
			updateActiveInactiveHosts((List<String>) entry.getValue());
			break;

		default:
			LOG.warn("Status not found {}", status.toString());
			break;
		}
	}

	/**
	 * Retrieve the full list of received breach-messages
	 * 
	 * @return
	 */
	public List<BreachMessage<?>> getBatchedBreachMessages() {
		return batchedBreachMessages;
	}

	/**
	 * Empty breach-messages list
	 */
	public void emptyBatchBreachMessageList() {
		batchedBreachMessages = new ArrayList<BreachMessage<?>>();
	}

	/**
	 * Request Active- and inactive nodes list
	 */
	private void updateActiveInactiveHosts(List<String> newActiveHosts) {
		LOG.debug("Update active/inactive hosts");
		Set<CassandraHost> tempActiveNodes = Config.getActiveHosts();

		// Update active hosts
		Config.setActiveHosts(generateHostFromString(newActiveHosts));

		// Retain list of hosts which have turned inactive.
		Collection<?> newInactiveHosts = CollectionUtils.subtract(tempActiveNodes, Config.getActiveHosts());

		// Append new inactive nodes
		while (newInactiveHosts.iterator().hasNext()) {
			Config.getInactiveHosts().add((CassandraHost) newInactiveHosts.iterator().next());
		}

		// Update inactiveHosts
		Config.getInactiveHosts().removeAll(Config.getActiveHosts());
		
	}

	/**
	 * Generate CassandraHost-list from String-list of hosts
	 * 
	 * @param hosts
	 * @return
	 */
	private Set<CassandraHost> generateHostFromString(List<String> hosts) {
		Set<CassandraHost> set = new HashSet<CassandraHost>();
		for (String hostString : hosts) {
			CassandraHost host = new CassandraHost(hostString);
			if (Config.getInactiveHosts().contains(host)) {
				for (CassandraHost h : Config.getInactiveHosts()) {
					if (host.equals(h)) {
						host.setPort(h.getPort());
					}
				}
			}
			set.add(host);
		}

		return set;
	}

}
