package org.uio.autoscale;

import java.util.HashSet;
import java.util.Set;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.JmxMonitor;
/**
 * Autoscale for cassandra cluster
 * @author toraba
 */
public class Autoscale {
	
	private static String CLUSTER_NAME;
	private CassandraClientMonitor monitor;
	private Set<CassandraHost> inactiveHosts;
	
	public Autoscale(String clusterName) {
		CLUSTER_NAME = clusterName;
		
		CassandraHostConfigurator configurator = new CassandraHostConfigurator();
		HConnectionManager connectionManager = new HConnectionManager(clusterName, configurator);
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
		
		//CassandraHost host = new CassandraHost("127.0.0.1", 9160);
		inactiveHosts = new HashSet<CassandraHost>(0);
	}

	
	/**
	 * Initialize new Cassandra-host object in memory
	 * @param host
	 * @param port
	 * @return
	 */
	public CassandraHost initNewHost(String host, int port) {
		return new CassandraHost(host, port);
	}
	
	/**
	 * Add a host tto cluster
	 * @param host
	 * @param port
	 * @return
	 */
	public boolean addHostToCluster(String host, int port) {
		String hostStr = host + ":" + String.valueOf(port);
		return monitor.addCassandraHost(hostStr); 
	}
	
	/**
	 * Remove host from cluster
	 * @param host
	 * @param port
	 * @return
	 */
	public boolean removeHostFromCluster(String host, int port) {
		String hostStr = host + ":" + String.valueOf(port);
		return monitor.removeCassandraHost(hostStr);
	}

}
