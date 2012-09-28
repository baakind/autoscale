package no.uio.master.autoscale.node;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.prettyprint.cassandra.service.CassandraHost;

/**
 * Holds data about currently active nodes
 * @author toraba
 *
 */
public class NodeMonitor {
	private static Logger LOG = LoggerFactory.getLogger(NodeMonitor.class);
	
	private static HashMap<String, NodeData> data;
	
	
	public NodeMonitor(Set<CassandraHost> nodes) {
		data = new HashMap<String, NodeData>();
		
		try {
			for (Iterator<CassandraHost> iterator = nodes.iterator(); iterator.hasNext();) {
				CassandraHost cassandraHost = iterator.next();
				data.put(cassandraHost.getHost(), new NodeData(cassandraHost.getIp()));//cassandraHost.getPort()));
			}
		} catch (IOException e) {
			LOG.error("Failed to initialize Nodemonitor");
			throw new RuntimeException(e);
		}
	
	}
	
	/**
	 * Get current percentage of heap memory used for given host/ip-address
	 * @param ipAdr
	 * @return
	 * @throws IOException 
	 */
	public Double getHeapMemoryUsage(String ipAdr) {
		try {
			if(data.containsKey(ipAdr)) {
				return data.get(ipAdr).heapMemoryUsed();
			}
		} catch (IOException e) {
			LOG.error("Failed to get heap memory usage for host: " + ipAdr,e);
			throw new RuntimeException(e);
		} 
		return null;
	}
	
	/**
	 * Get current percentage of non-heap memory used for given host/ip-address
	 * @param ipAdr
	 * @return
	 * @throws IOException 
	 */
	public Double getNonHeapMemoryUsage(String ipAdr) {
		try {
			if(data.containsKey(ipAdr)) {
				return data.get(ipAdr).nonHeapMemoryUsed();
			}
		} catch (IOException e) {
			LOG.error("Failed to get non-heap memory usage for host: " + ipAdr);
			throw new RuntimeException(e);
		}
		return null;
	}
	
	/**
	 * Retrieve collection with node data for complete cluster
	 * @return
	 */
	public Collection<NodeData> getClusterNodeData() {
		return data.values();
	}
}
