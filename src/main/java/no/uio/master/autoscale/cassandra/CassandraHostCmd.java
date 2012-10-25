package no.uio.master.autoscale.cassandra;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.uio.master.autoscale.node.HostCmd;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.tools.NodeCmd;
import org.apache.cassandra.tools.NodeProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraHostCmd implements HostCmd {
	private static Logger LOG = LoggerFactory.getLogger(CassandraHostManager.class);

	private static NodeCmd nodeCmd;
	private static NodeProbe nodeProbe;
	
	private String host;
	private int port;
	
	public CassandraHostCmd(String host, int port) {
		this.host = host;
		this.port = port;
		
		
		connect(host, port);
		
	}
	
	@Override
	public void connect(String host, int port) {
		try {
			nodeProbe = new NodeProbe(host, port);
			nodeCmd = new NodeCmd(nodeProbe);
		} catch (Exception e) {
			LOG.error("Failed to initialise nodeCmd - " + host +":"+port);
		}
	}
	
	@Override
	public String getToken() {
		return nodeProbe.getToken();
	}

	@Override
	public void moveNode(String newToken) throws IOException, InterruptedException, ConfigurationException {
		LOG.debug("Move "+host+" from token: " + nodeProbe.getToken() + " -> " + newToken);
		nodeProbe.move(newToken);
	}

	@Override
	public long getUptime() {
		return nodeProbe.getUptime();
	}

	@Override
	public void prepareInactive() {
		LOG.debug("Shutting down gossip and thrift-server.");
		nodeProbe.stopGossiping();
		nodeProbe.stopThriftServer();
	}

	@Override
	public void prepareActive() {
		LOG.debug("Starting gossip and thrift-server.");
		nodeProbe.startThriftServer();
		nodeProbe.startGossiping();
	}

	
	
	@Override
	public String generateNewToken() {
		String thisToken = getToken();
		String previousToken = getPreviousToken(thisToken);
		BigInteger newToken = new AbsoluteCenterTokenGenerator().generateToken(new BigInteger(previousToken), new BigInteger(thisToken));
		return newToken.toString();
	}

	@Override
	public List<Entry<String, String>> getRingTokensSorted() {
		return sortMap(nodeProbe.getTokenToEndpointMap());
	}
	
	@Override
	public String getPreviousToken(String thisToken) {
		String previousToken = "";
		List<Entry<String, String>> sortedMap = getRingTokensSorted();
		int number = 1;
		for (Entry<String, String> entry : sortedMap) {
			String token = entry.getKey();
			// First occurrence
			if(previousToken.isEmpty()) {
				previousToken = token;
			}
			
			if(thisToken.equals(token)) {
				
				// If first occurrence, should return last element 
				if(number == 1) {
					return sortedMap.get(sortedMap.size()-1).getKey();
				}
				
				return previousToken;
			}
			previousToken = token;
			number++;
		}
		
		return previousToken;
	}
	
	/**
	 * Sort <tt>map</tt>.
	 * @param map
	 * @return
	 */
	private List<Entry<String, String>> sortMap(Map<String, String> map) {
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>();
		Iterator<Entry<String, String>> itr = map.entrySet().iterator();
		while(itr.hasNext()) {
			list.add(itr.next());
		}
		
		Collections.sort(list, new BigIntegerTokenComparator());
		return list;
	}
}
