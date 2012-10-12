package no.uio.master.autoscale.service;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.JmxMonitor;
import me.prettyprint.hector.api.Cluster;
import no.uio.master.autoscale.cassandra.CassandraHostManager;
import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.node.NodeMonitor;
import no.uio.master.autoscale.slave.message.SlaveMessage;
import no.uio.master.autoscale.slave.message.enumerator.SlaveMessageType;
import no.uio.master.autoscale.slave.net.Communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runnable instance of Autoscale, for external usage.
 * @author toraba
 * 
 */
public class AutoscaleDaemon implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);
	private static HConnectionManager connectionManager;
	private static CassandraClientMonitor monitor;
	private static Config config = new Config();
	private static CassandraHostManager nodeManager;
	private static NodeMonitor nodeMonitor;
	
	private static Communicator communicator;
	
	private static SlaveListener slaveListener;
	private static ScheduledExecutorService executor;
	
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
		nodeManager = new CassandraHostManager(connectionManager);
		nodeManager.setInactiveNodes(new HashSet<CassandraHost>(0));
		nodeManager.setActiveNodes(connectionManager.getHosts());
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
		nodeMonitor = new NodeMonitor(nodeManager.getActiveNodes());
		initializeSlaves();
		slaveListener = new SlaveListener();
	}
	
	@Override
	public void run() {
			LOG.debug("Daemon running...");
			slaveListener.listenForMessage();
	}
	
	/**
	 * Send initialization-message to all slaves.
	 */
	public void initializeSlaves() {
		
		// Construct message
		SlaveMessage slaveMsg = new SlaveMessage(SlaveMessageType.INITIALIZATION);
		slaveMsg.put("intervall_timer", Config.intervall_timer);
		slaveMsg.put("threshold_breach_limit", Config.threshold_breach_limit);
		slaveMsg.put("min_memory_usage", Config.min_memory_usage);
		slaveMsg.put("max_memory_usage", Config.max_memory_usage);
		slaveMsg.put("min_free_disk_space",Config.min_free_disk_space);
		slaveMsg.put("max_free_disk_space",Config.max_free_disk_space);
		slaveMsg.put("storage_location", Config.storage_location);

		
		// Send message to all slaves
		for(CassandraHost host : nodeManager.getActiveNodes()) {
			communicator = new Communicator(Config.master_input_port, Config.master_output_port);
			communicator.sendMessage(host.getHost(), slaveMsg);
			communicator = null;
		}
	}

	
}
