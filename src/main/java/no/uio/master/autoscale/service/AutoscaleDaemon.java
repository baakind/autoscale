package no.uio.master.autoscale.service;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.Cluster;
import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.host.CassandraHostManager;
import no.uio.master.autoscale.message.SlaveMessage;
import no.uio.master.autoscale.message.enumerator.SlaveMessageType;
import no.uio.master.autoscale.net.Communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runnable instance of Autoscale, for external usage.
 * 
 * @author toraba
 * 
 */
public class AutoscaleDaemon implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(AutoscaleDaemon.class);
	private static HConnectionManager connectionManager;
	private static CassandraHostManager hostManager;
	private static Set<CassandraHost> slaves;

	private static SlaveListener slaveListener;
	private static Communicator communicator;
	private static SimpleCassandraScaler scaler;

	private static int hostUpdater = 0;
	private final int UPDATE_NODELIST_COUNTER = 5;

	/**
	 * Initialize autoscaler
	 * 
	 * @param clusterName
	 * @param host
	 */
	public AutoscaleDaemon(Cluster c) {
		init(c);
		LOG.debug("Daemon started");
	}

	private void init(Cluster c) {
		connectionManager = c.getConnectionManager();
		hostManager = new CassandraHostManager(connectionManager);
		initializeSlaves(hostManager.getActiveHosts());
		slaveListener = new SlaveListener();
		initializeScaler(slaveListener);
	}

	@Override
	public void run() {
		LOG.debug("Daemon running...");
		slaveListener.listenForMessage();

		// TODO: Should be moved to another class.
		// This daemon ony runs every time a message is received, which means
		// it will not detect new nodes at a set interval
		checkForNewHosts();
	}

	private void checkForNewHosts() {
		// Update hosts every n-th run
		if (hostUpdater < UPDATE_NODELIST_COUNTER) {
			hostUpdater++;
		} else {
			// Update slaves-list
			Set<CassandraHost> oldSlaves = slaves;
			slaves = hostManager.getActiveHosts();
			Set<CassandraHost> initSlaves = slaves;
			initSlaves.removeAll(oldSlaves);

			// Initialize any new slaves
			if (!initSlaves.isEmpty()) {
				initializeSlaves(initSlaves);
			}

			hostUpdater = 0;
		}
	}

	/**
	 * Send initialization-message to all slaves.
	 * 
	 * @param nodes
	 *            to initialize
	 */
	private void initializeSlaves(Set<CassandraHost> nodes) {

		// Construct slave-message
		SlaveMessage slaveMsg = new SlaveMessage(SlaveMessageType.INITIALIZATION);
		slaveMsg.put("intervall_timer", Config.intervall_timer_slave);
		slaveMsg.put("threshold_breach_limit", Config.threshold_breach_limit);
		slaveMsg.put("min_memory_usage", Config.min_memory_usage);
		slaveMsg.put("max_memory_usage", Config.max_memory_usage);
		slaveMsg.put("min_free_disk_space", Config.min_free_disk_space);
		slaveMsg.put("max_free_disk_space", Config.max_free_disk_space);
		slaveMsg.put("storage_location", Config.storage_location);

		// Send message to all slaves
		for (CassandraHost host : nodes) {
			communicator = new Communicator(Config.master_input_port, Config.master_output_port);
			communicator.sendMessage(host.getHost(), slaveMsg);
			communicator = null;
		}
	}

	/**
	 * Initialize the scaler, which will run alongside the autoscaler.<br>
	 * The scaler will collect received breach-messages from the SlaveListener
	 * at a given interval, and perform analysis upon messages, and eventually
	 * perform scaling of the cluster, at the desired locations.
	 * 
	 * @param listener
	 */
	private void initializeScaler(SlaveListener listener) {
		scaler = new SimpleCassandraScaler(listener, hostManager);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(scaler, 0, Config.intervall_timer_scaler, TimeUnit.SECONDS);
	}

}
