package org.uio.autoscale;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.JmxMonitor;

import org.uio.autoscale.util.TextUtils;
/**
 * Autoscale for cassandra cluster
 * @author toraba
 */
public class Autoscale {
	
	private static String CLUSTER_NAME;
	private static String DEFAULT_HOST = "127.0.0.1";
	private CassandraClientMonitor monitor;
	private HConnectionManager connectionManager;
	private Set<CassandraHost> inactiveHosts;
	private Set<CassandraHost> activeHosts;
	
	public Autoscale(String clusterName) {
		init(clusterName, DEFAULT_HOST);
	}
	
	public Autoscale(String clusterName, String... defaultHosts) {
		init(clusterName, defaultHosts);
	}
	
	
	private void init(String clusterName, String... defaultHosts) {
		String hosts = TextUtils.generateHostsList(defaultHosts);
		
		CLUSTER_NAME = clusterName;
		
		CassandraHostConfigurator configurator = new CassandraHostConfigurator(hosts);
		configurator.setAutoDiscoverHosts(false);
		
		connectionManager = new HConnectionManager(clusterName, configurator);
		activeHosts = connectionManager.getHosts();

		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
		
		inactiveHosts = new HashSet<CassandraHost>(0);
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
	public boolean addHostToCluster(CassandraHost node) {
		boolean result = connectionManager.addCassandraHost(node);
		
		if(result) {
			activeHosts = connectionManager.getHosts();
			for (Iterator<CassandraHost> iterator = inactiveHosts.iterator(); iterator.hasNext();) {
				CassandraHost host = iterator.next();
				// If ip and port are equal
				if(node.equals(host)) {
					iterator.remove();
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
			activeHosts = connectionManager.getHosts();
			inactiveHosts.add(node);
		}
		
		return result;
	}
	
	
	public Iterator<CassandraHost> getInactiveHostsIterator() {
		return inactiveHosts.iterator();
	}
	
	public Iterator<CassandraHost> getActiveHostsIterator() {
		return activeHosts.iterator();
	}
	
	public int getNumberOfInactiveHosts() {
		return inactiveHosts.size();
	}
	
	public int getNumberOfActiveHosts() {
		return activeHosts.size();
	}
	
	public CassandraHost getActiveHost(String host) {
		for (Iterator<CassandraHost> iterator = activeHosts.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if(node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}
	
	public CassandraHost getInactiveHost(String host) {
		for (Iterator<CassandraHost> iterator = inactiveHosts.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if(node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}
}
