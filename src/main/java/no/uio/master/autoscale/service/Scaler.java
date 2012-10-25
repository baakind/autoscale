package no.uio.master.autoscale.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import no.uio.master.autoscale.cassandra.CassandraHostManager;
import no.uio.master.autoscale.message.BreachMessage;
import no.uio.master.autoscale.node.HostManager;
import no.uio.master.autoscale.util.HostWeight;
import no.uio.master.autoscale.util.Scale;
import no.uio.master.autoscale.util.ScalerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The actual scaler.<br>
 * It gathers all breach-messages from current batch, and determine if any nodes
 * should be scaled up/down, whom etc
 * 
 * @author andreas
 */
public class Scaler implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(Scaler.class);
	private static SlaveListener slaveListener;
	private static ScalerUtils scalerUtils = new ScalerUtils();
	private static HostManager<?> nodeManager;

	private static List<BreachMessage<?>> breachMessages;

	public Scaler(SlaveListener listener, HostManager<?> hManager) {
		LOG.debug("Initialize scaler");
		slaveListener = listener;
		nodeManager = (CassandraHostManager) hManager;
	}

	@Override
	public void run() {
		LOG.debug("Scaler running");
		collectBreachMessages();
		performScaleCalculation();
	}

	/**
	 * Collect and empty breach-messages from slave-listener
	 */
	protected void collectBreachMessages() {
		breachMessages = slaveListener.getBatchedBreachMessages();
		LOG.debug("Batch received, number of messages:" + breachMessages.size());
		slaveListener.emptyBatchBreachMessageList();
	}

	/**
	 * Perform calculation upon collected breach-messages, and eventually other
	 * conditions which may be of interest.
	 */
	protected void performScaleCalculation() {
		List<HostWeight> weightedResults = hostWeights();
		List<HostWeight> scaleEntities = selectScaleEntities(weightedResults);
		
		// Perform scaling
		for (HostWeight hostWeight : scaleEntities) {
			if(hostWeight.getScale() != null) {
				
				if(hostWeight.getScale() == Scale.UP) {
					LOG.info("Scaling up extra node. " + hostWeight.getHost() + " is overloaded!");
					
					if(!nodeManager.getInactiveNodes().isEmpty()) {
						CassandraHost host = (CassandraHost) nodeManager.getInactiveNodes().iterator().next();
						
					} else {
						LOG.info("No available nodes for scaling!");
					}
					
				} else if(hostWeight.getScale() == Scale.DOWN) {
					LOG.info("Scaling down node " + hostWeight.getHost());
					//TOOD: Make sure we dont go below Config.min_number_of_active_nodes!
				}
			}
		}
		
	}
	
	protected static List<HostWeight> selectScaleEntities(List<HostWeight> weightedResults) {
		List<HostWeight> scale = new ArrayList<HostWeight>();
		
		// Split results in scale up and scale down results
		List<HostWeight> scaleUp = new ArrayList<HostWeight>();
		List<HostWeight> scaleDown = new ArrayList<HostWeight>();
		for (HostWeight host : weightedResults) {
			if(host.getScore() > 0) {
				host.setScale(Scale.UP);
				scaleUp.add(host);
			} else if(host.getScore() < 0) {
				host.setScale(Scale.DOWN);
				scaleDown.add(host);
			}
		}
		
		// Sort lists
		sortHostWeights(scaleUp);
		sortHostWeights(scaleDown);
		
		
		Integer absScaleDownWeight = Math.abs(scaleDown.get(0).getScore());
		Integer scaleUpWeight = scaleUp.get(scaleUp.size()-1).getScore();
		
		/*
		 * Scale up if:  scaleUp > scaleDown, or scaleUp == scaleDown
		 * Scale down if: scaleUp < scaleDown, or scaleUp == scaleDown
		 */
		if(scaleUpWeight >= absScaleDownWeight) {
			scale.add(scaleUp.get(scaleUp.size()-1));
		}
		if(scaleUpWeight <= absScaleDownWeight) {
			scale.add(scaleDown.get(0));
		}
		
		return scale;
	}

	/**
	 * Sort <tt>weights<tt> list based on the score.
	 * @param weights
	 */
	protected static void sortHostWeights(List<HostWeight> weights) {
		if(weights.isEmpty()) {
			return;
		}
		
		Collections.sort(weights);
	}

	/**
	 * Perform weighting of the messages.<br>
	 * Results in a map containing <tt>node</tt> and <tt>score</tt><br>
	 * A positive score means the node needs to be scaled up.<br>
	 * A negative score means the node needs to be scaled down.<br>
	 * 
	 * @return
	 */
	protected List<HostWeight> hostWeights() {
		List<HostWeight> weights = new ArrayList<HostWeight>();

		// Iterate all breach-messages
		Iterator<BreachMessage<?>> iterator = breachMessages.iterator();
		while(iterator.hasNext()) {
			BreachMessage<?> msg = iterator.next();
			String senderHost = msg.getSenderHost();

			Integer value = scalerUtils.getPriorityOfBreachType(msg.getType());
			HostWeight host = new HostWeight(senderHost, value);

			// Sender host doesn't exist, create
			if (!weights.contains(host)) {
				weights.add(host);
			} else {
				int idx = weights.indexOf(host);
				host = weights.get(idx);
				value += host.getScore();
				host.setScore(value);
				weights.set(idx, host);
			}
		}

		return weights;
	}

}
