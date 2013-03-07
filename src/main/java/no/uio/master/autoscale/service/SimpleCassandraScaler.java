package no.uio.master.autoscale.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.host.CassandraHost;
import no.uio.master.autoscale.host.Host;
import no.uio.master.autoscale.host.HostManager;
import no.uio.master.autoscale.message.BreachMessage;
import no.uio.master.autoscale.message.enumerator.BreachType;
import no.uio.master.autoscale.util.HostWeight;
import no.uio.master.autoscale.util.Scale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCassandraScaler implements Scaler {
	private static Logger LOG = LoggerFactory.getLogger(SimpleCassandraScaler.class);
	private static AgentListener slaveListener;
	private static HostManager hostManager;

	private static List<BreachMessage<?>> breachMessages;
	private static Map<BreachType, Integer> priorities = new HashMap<BreachType, Integer>();

	static {
		// Positive integers = scale-up
		priorities.put(BreachType.MAX_DISK_USAGE, 2);
		priorities.put(BreachType.MAX_MEMORY_USAGE, 1);

		// Negative integers = scale-down
		priorities.put(BreachType.MIN_MEMORY_USAGE, -1);
		priorities.put(BreachType.MIN_DISK_USAGE, -2);
	}

	public SimpleCassandraScaler(AgentListener listener, HostManager hManager) {
		LOG.debug("Initialize scaler");
		slaveListener = listener;
		hostManager = hManager;
	}

	@Override
	public void run() {
		LOG.debug("Scaler running");
		collectBreachMessages();
		performScaleCalculation();
	}

	@Override
	public void collectBreachMessages() {
		breachMessages = slaveListener.getBatchedBreachMessages();
		LOG.debug("Batch received, number of messages:" + breachMessages.size());
		slaveListener.emptyBatchBreachMessageList();
	}

	@Override
	public void performScaleCalculation() {
		LOG.debug("Perform calculation...");
		List<HostWeight> weightedResults = hostWeights();
		if (weightedResults.isEmpty()) {
			LOG.debug("No messages found.");
			return;
		}
		List<HostWeight> scaleEntities = selectScaleEntities(weightedResults);

		// Perform scaling
		for (HostWeight hostWeight : scaleEntities) {
			if (hostWeight.getScale() != null) {

				if (hostWeight.getScale() == Scale.UP) {

					if (!hostManager.getInactiveHosts().isEmpty()) {
						Host host = hostManager.getInactiveHosts().iterator().next();
						LOG.info("Adding {} to the cluster because {} is overloaded!",host.getHost(), hostWeight.getHost());
						hostManager.addHostToCluster(host);
						hostManager.updateActiveAndInactiveHosts();
					} else {
						LOG.info("No available nodes for scaling!");
					}

				} else if (hostWeight.getScale() == Scale.DOWN) {
					if (hostManager.getActiveHosts().size() > Config.min_number_of_nodes) {
						LOG.info("Removing {} from the cluster", hostWeight.getHost());
						Host host = hostManager.getActiveHost(hostWeight.getHost());
						hostManager.removeHostFromCluster(host);
						hostManager.updateActiveAndInactiveHosts();
					} else {
						LOG.info("Already scaled down to minimum number of nodes, scaling aborted!");
					}
				}
			}
		}

	}

	@Override
	public List<HostWeight> selectScaleEntities(List<HostWeight> weightedResults) {
		List<HostWeight> scale = new ArrayList<HostWeight>();

		// Split results in scale up and scale down results
		List<HostWeight> scaleUp = new ArrayList<HostWeight>();
		List<HostWeight> scaleDown = new ArrayList<HostWeight>();
		for (HostWeight host : weightedResults) {
			if (host.getScore() > 0) {
				host.setScale(Scale.UP);
				scaleUp.add(host);
			} else if (host.getScore() < 0) {
				host.setScale(Scale.DOWN);
				scaleDown.add(host);
			}
		}

		// Sort lists
		sortHostWeights(scaleUp);
		sortHostWeights(scaleDown);

		Integer scaleDownWeightAbs = 0;
		if (!scaleDown.isEmpty()) {
			scaleDownWeightAbs = scaleDown.get(0).getScore() == 0 ? scaleDown.get(0).getScore() : -scaleDown.get(0)
					.getScore();
		}

		Integer scaleUpWeight = 0;
		if (!scaleUp.isEmpty()) {
			scaleUpWeight = scaleUp.get(scaleUp.size() - 1).getScore();
		}
		/*
		 * Scale up if: scaleUp > scaleDown, or scaleUp == scaleDown Scale down
		 * else if: scaleUp < scaleDown, or scaleUp == scaleDown
		 */
		if (scaleUpWeight >= scaleDownWeightAbs && !scaleUp.isEmpty()) {
			scale.add(scaleUp.get(scaleUp.size() - 1));
		} else if (scaleUpWeight <= scaleDownWeightAbs && !scaleDown.isEmpty()) {
			scale.add(scaleDown.get(0));
		}
		return scale;
	}

	@Override
	public void sortHostWeights(List<HostWeight> weights) {
		if (weights.isEmpty()) {
			return;
		}

		Collections.sort(weights);
	}

	@Override
	public List<HostWeight> hostWeights() {
		List<HostWeight> weights = new ArrayList<HostWeight>();

		// Iterate all breach-messages
		Iterator<BreachMessage<?>> iterator = breachMessages.iterator();
		while (iterator.hasNext()) {
			BreachMessage<?> msg = iterator.next();
			String senderHost = msg.getSenderHost();

			Integer value = getPriorityOfBreachType(msg.getType());
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

	/**
	 * Get priority of BreachType. If not found, return 0, which represent a
	 * stable node (either up or down)
	 * 
	 * @param type
	 * @return
	 */
	private Integer getPriorityOfBreachType(BreachType type) {
		Integer pri = 0;

		if (priorities.containsKey(type)) {
			pri = priorities.get(type);
		}

		return pri;
	}

}
