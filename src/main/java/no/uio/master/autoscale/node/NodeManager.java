package no.uio.master.autoscale.node;

import java.util.Iterator;
import java.util.Set;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.JmxMonitor;

public class NodeManager {

	private Set<CassandraHost> inactiveNodes;
	private Set<CassandraHost> activeNodes;
	private HConnectionManager connectionManager;
	private static CassandraClientMonitor monitor;
	
	public NodeManager(HConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
	}
	/**
	 * Initialize new Cassandra-host object in memory
	 * @param host
	 * @param port
	 * @return
	 */
	public CassandraHost initNewHost(String host, int port) {
		CassandraHost node = new CassandraHost(host, port);
		return node;
	}
	
	/**
	 * Add a host to cluster. <br>
	 * Removes host from inactiveHosts, and append to activeHosts
	 * @param node
	 * @return
	 */
	public boolean addHostToCluster(HConnectionManager connectionManager, CassandraHost node) {
		boolean result = connectionManager.addCassandraHost(node);
		
		if(result) {
			activeNodes = connectionManager.getHosts();
			for (Iterator<CassandraHost> iterator = inactiveNodes.iterator(); iterator.hasNext();) {
				CassandraHost host = iterator.next();
				
				// If ip and port are equal
				if(node.equals(host)) {
					iterator.remove();
					monitor.updateKnownHosts();
				}
			}
		}
		
		return result;
	}

	/**
	 * Remove host from cluster.<br>
	 * Remove host from activeHosts, and append to inactiveHosts
	 * @param node
	 * @return
	 */
	public boolean removeHostFromCluster(CassandraHost node) {
		boolean result = connectionManager.removeCassandraHost(node);
		
		if(result) {
			activeNodes = connectionManager.getHosts();
			inactiveNodes.add(node);
			monitor.updateKnownHosts();
		}
		
		return result;
	}
	
	public Set<CassandraHost> getInactiveNodes() {
		return inactiveNodes;
	}

	public void setInactiveNodes(Set<CassandraHost> inactiveNodes) {
		this.inactiveNodes = inactiveNodes;
	}

	public Set<CassandraHost> getActiveNodes() {
		return activeNodes;
	}

	public void setActiveNodes(Set<CassandraHost> activeNodes) {
		this.activeNodes = activeNodes;
	}
	
	
	public int getNumberOfInactiveHosts() {
		return inactiveNodes.size();
	}
	
	public int getNumberOfActiveHosts() {
		return activeNodes.size();
	}
	
	
	public CassandraHost getActiveHost(String host) {
		for (Iterator<CassandraHost> iterator = activeNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if(node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}
	
	public CassandraHost getInactiveHost(String host) {
		for (Iterator<CassandraHost> iterator = inactiveNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if(node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}
}
