package no.uio.master.autoscale.host;

import java.util.Set;

import no.uio.master.autoscale.config.Config;
import no.uio.master.autoscale.message.AgentMessage;
import no.uio.master.autoscale.message.enumerator.AgentMessageType;
import no.uio.master.autoscale.message.enumerator.AgentStatus;
import no.uio.master.autoscale.net.Communicator;
import no.uio.master.autoscale.util.AgentUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraHostManager implements HostManager {
	private static Logger LOG = LoggerFactory.getLogger(CassandraHostManager.class);

	private Communicator communicator;

	public CassandraHostManager() {
	}

	@Override
	public Set<Host> getActiveHosts() {
		LOG.debug("Active hosts: " + Config.getActiveHosts().size());
		return Config.getActiveHosts();
	}

	@Override
	public Set<Host> getInactiveHosts() {
		LOG.debug("Inactive hosts:" + Config.getInactiveHosts().size());
		return Config.getInactiveHosts();
	}

	@Override
	public void addHostToCluster(Host host) {
		LOG.info("Adding " + host.getHost() + " to the cluster");
		
		AgentMessage msg = new AgentMessage(AgentMessageType.STARTUP_NODE);
		AgentUtils.appendConfigurationToMessage(msg);
		
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
		communicator.sendMessage(host.getHost(), msg);
		communicator = null;
		
		Config.getActiveHosts().add(host);
		Config.getInactiveHosts().remove(host);
	}

	@Override
	public void removeHostFromCluster(Host host) {
		LOG.info("Removing " + host.getHost() + " from the cluster");
		
		AgentMessage msg = new AgentMessage(AgentMessageType.SHUTDOWN_NODE);
		
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
		communicator.sendMessage(host.getHost(), msg);
		communicator = null;
		
		Config.getInactiveHosts().add(host);
		Config.getActiveHosts().remove(host);
	}

	@Override
	public Host getActiveHost(String host) {
		for (Host cassandraHost : Config.getActiveHosts()) {
			if (host.equalsIgnoreCase(cassandraHost.getHost())) {
				return cassandraHost;
			}
		}

		return null;

	}
	
	@Override
	public Host getInactiveHost(String host) {
		for (Host cassandraHost : Config.getInactiveHosts()) {
			if (host.equalsIgnoreCase(cassandraHost.getHost())) {
				return cassandraHost;
			}
		}

		return null;

	}
	
	@Override
	public void updateActiveAndInactiveHosts() {
		
		AgentMessage msg = new AgentMessage(AgentMessageType.STATUS);
		msg.put("status", AgentStatus.LIVE_NODES);
		
		communicator = new Communicator(Config.master_input_port, Config.master_output_port);
		communicator.sendMessage(Config.getActiveHosts().iterator().next().getHost(), msg);
		communicator = null;
	}
}