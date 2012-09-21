package no.uio.master.autoscale.service;

import java.util.HashSet;
import java.util.Properties;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.JmxMonitor;
import me.prettyprint.hector.api.Cluster;
import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.node.NodeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runnable instance of Autoscale, for external usage.
 * @author toraba
 *
 */
public class AutoscaleDaemon implements Runnable{
	private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);
	private static HConnectionManager connectionManager;
	private static CassandraClientMonitor monitor;
	private static Config config = new Config();
	private static NodeManager nodeManager;
	
	/**
	 * Initialize autoscaler
	 * @param clusterName
	 * @param host
	 */
	public AutoscaleDaemon(Cluster c) {
		init(c);
		LOG.debug("Daemon started");
	}
	
	private void init(Cluster c) {
		connectionManager = c.getConnectionManager();
		nodeManager = new NodeManager(connectionManager);
		nodeManager.setInactiveNodes(new HashSet<CassandraHost>(0));
		nodeManager.setActiveNodes(connectionManager.getHosts());
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
	}
	
	public Config getConfig() {
		return config;
	}
	
	@Override
	public void run() {
		String msg = "InactiveNodes: " + nodeManager.getNumberOfInactiveHosts() + ", ActiveNodes: " + nodeManager.getNumberOfActiveHosts();
		LOG.debug(msg);
	}
}
