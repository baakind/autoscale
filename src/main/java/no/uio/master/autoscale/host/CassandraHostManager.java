package no.uio.master.autoscale.host;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.JmxMonitor;
import no.uio.master.autoscale.service.HostManager;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraHostManager implements HostManager<CassandraHost> {
	private static Logger LOG = LoggerFactory.getLogger(CassandraHostManager.class);

	private Set<CassandraHost> inactiveHosts;
	private Set<CassandraHost> activeHosts;
	private HConnectionManager connectionManager;
	private static CassandraClientMonitor monitor;

	private CassandraHostCmd hostCmd;

	public CassandraHostManager(HConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
		inactiveHosts = new HashSet<CassandraHost>(0);
		activeHosts = new HashSet<CassandraHost>(0);
		updateActiveInactiveHosts();
	}

	@Override
	public Set<CassandraHost> getActiveHosts() {
		updateActiveInactiveHosts();
		return activeHosts;
	}

	@Override
	public Set<CassandraHost> getInactiveHosts() {
		updateActiveInactiveHosts();
		return inactiveHosts;
	}

	@Override
	public void addHostToCluster(CassandraHost host) {
		LOG.info("Adding " + host.getHost() + " to the cluster");
		hostCmd = new CassandraHostCmd(host.getHost(), host.getPort());
		try {
			hostCmd.addHostToCluster(hostCmd.generateNewToken());
			LOG.info("Host successfully added.");
		} catch (Exception e) {
			LOG.warn("Failed to add host to cluster - " + host.getHost());
		} finally {
			hostCmd.disconnect();
			updateActiveInactiveHosts();
		}
	}

	@Override
	public void removeHostFromCluster(CassandraHost host) {
		LOG.info("Removing " + host.getHost() + " from the cluster");
		hostCmd = new CassandraHostCmd(host.getHost(), host.getPort());

		try {
			hostCmd.removeHostFromCluster();
			LOG.info("Host successfully removed");
		} catch (Exception e) {
			LOG.warn("Failed while removing host from cluster - " + host.getHost());
		} finally {
			hostCmd.disconnect();
			updateActiveInactiveHosts();
		}
	}

	@Override
	public CassandraHost getActiveHost(String host) {
		for (CassandraHost cassandraHost : activeHosts) {
			if (host.equalsIgnoreCase(cassandraHost.getHost())) {
				return cassandraHost;
			}
		}

		return null;

	}

	/**
	 * Update Active- and inactive nodes list
	 */
	private void updateActiveInactiveHosts() {
		Set<CassandraHost> tempActiveNodes = activeHosts;

		// Update active nodes set
		activeHosts = connectionManager.getHosts();

		// Retain list of hosts which have turned inactive.
		Collection<?> newInactiveHosts = CollectionUtils.subtract(tempActiveNodes, activeHosts);

		// Append new inactive nodes
		while (newInactiveHosts.iterator().hasNext()) {
			inactiveHosts.add((CassandraHost) newInactiveHosts.iterator().next());
		}

		// Update inactiveNodesSet
		inactiveHosts.removeAll(activeHosts);
	}
}