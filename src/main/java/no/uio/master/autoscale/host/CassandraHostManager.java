package no.uio.master.autoscale.host;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraClientMonitor;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.JmxMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraHostManager implements HostManager<CassandraHost> {
	private static Logger LOG = LoggerFactory.getLogger(CassandraHostManager.class);

	private Set<CassandraHost> inactiveNodes;
	private Set<CassandraHost> activeNodes;
	private HConnectionManager connectionManager;
	private static CassandraClientMonitor monitor;
	

	public CassandraHostManager(HConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		monitor = JmxMonitor.getInstance().getCassandraMonitor(connectionManager);
		inactiveNodes = new HashSet<CassandraHost>(0);
		activeNodes = new HashSet<CassandraHost>(0);
		updateActiveNodes();
	}

	@Override
	public CassandraHost initNewNode(String host, int port) {
		return new CassandraHost(host, port);
	}

	@Override
	public void updateActiveNodes() {
		activeNodes = connectionManager.getHosts();
	}

	@Override
	public boolean addNodeToCluster(CassandraHost node) {
		boolean result = connectionManager.addCassandraHost(node);

		if (result) {
			updateActiveNodes();
			for (Iterator<CassandraHost> iterator = inactiveNodes.iterator(); iterator.hasNext();) {
				CassandraHost host = iterator.next();

				// IP and PORT are equal
				if (node.equals(host)) {
					iterator.remove();
					updateActiveNodes();
				}
			}
		}

		return result;
	}

	@Override
	public boolean removeNodeFromCluster(CassandraHost host) {
		 boolean result = connectionManager.removeCassandraHost(host);

		if (result) {
			updateActiveNodes();
			inactiveNodes.add(host);
		}

		return result;
	}

	@Override
	public Set<CassandraHost> getInactiveNodes() {
		return inactiveNodes;
	}

	@Override
	public void setInactiveNodes(Set<CassandraHost> inactiveNodes) {
		this.inactiveNodes = inactiveNodes;
	}

	@Override
	public void addInactiveNode(CassandraHost inactiveNode) {
		inactiveNodes.add(inactiveNode);
	}

	@Override
	public void removeInactiveNode(CassandraHost inactiveNode) {
		for (Iterator<CassandraHost> iterator = inactiveNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			
			if(node.equals(inactiveNode)) {
				iterator.remove();
			}
		}
	}

	@Override
	public int getNumberOfInactiveNodes() {
		return inactiveNodes.size();
	}

	@Override
	public Set<CassandraHost> getActiveNodes() {
		return activeNodes;
	}

	@Override
	public void setActiveNodes(Set<CassandraHost> activeNodes) {
		this.activeNodes = activeNodes;
	}

	@Override
	public void addActiveNode(CassandraHost activeNode) {
		activeNodes.add(activeNode);
	}

	@Override
	public void removeActiveNode(CassandraHost activeNode) {
		for (Iterator<CassandraHost> iterator = activeNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			
			if(node.equals(activeNode)) {
				iterator.remove();
			}
		}
	}

	@Override
	public int getNumberOfActiveNodes() {
		updateActiveNodes();
		return activeNodes.size();
	}

	@Override
	public CassandraHost getInactiveNode(String host) {
		for (Iterator<CassandraHost> iterator = inactiveNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if (node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}

	@Override
	public CassandraHost getActiveNode(String host) {
		for (Iterator<CassandraHost> iterator = activeNodes.iterator(); iterator.hasNext();) {
			CassandraHost node = iterator.next();
			if (node.getHost().equals(host)) {
				return node;
			}
		}
		return null;
	}
}
