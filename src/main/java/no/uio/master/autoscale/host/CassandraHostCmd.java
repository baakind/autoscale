package no.uio.master.autoscale.host;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.uio.master.autoscale.service.HostCmd;
import no.uio.master.autoscale.token.AbsoluteCenterTokenGenerator;
import no.uio.master.autoscale.token.BigIntegerTokenComparator;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.net.MessagingService;
import org.apache.cassandra.tools.NodeProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author andreas
 *
 */
public class CassandraHostCmd implements HostCmd {
	private static Logger LOG = LoggerFactory.getLogger(CassandraHostManager.class);

	private NodeProbe nodeProbe;
	
	private String host;
	private int port;
	
	
	public CassandraHostCmd(String host, int port) {
		this.host = host;
		this.port = port;
		
		connect(host, port);
		
	}
	
	private void connect(String host, int port) {
		try {
			nodeProbe = new NodeProbe(host, port);
			LOG.debug("Initialized Cassandra HostCommand: "+host+":"+port);
		} catch (Exception e) {
			LOG.error("Failed to initialise Cassandra HostCommand: " + host +":"+port);
		}
	}
	
	@Override
	public void disconnect() {
		try {
			nodeProbe.close();
		} catch (IOException e) {
			LOG.error("Failed while closing connection to Cassandra HostCommand: " + host);
		}
	}
	
	@Override
	public String getToken() {
		String token = nodeProbe.getToken();
		LOG.debug("Retrieve token: " + token);
		return token;
	}

	@Override
	public long getUptime() {
		long uptime = nodeProbe.getUptime();
		LOG.debug("Uptime: "+uptime+"ms");
		return uptime;
	}

	@Override
	public void removeHostFromCluster() throws InterruptedException {
		LOG.debug("Removing host from cluster - {}...",host);
		nodeProbe.decommission();
		nodeProbe.stopGossiping();
		//nodeProbe.stopThriftServer();
		//TODO: Wipe data from disk-location to make the node a fresh node
		//TODO: Keep threadPool, or somehow keep reference or how to re-gain.
		LOG.debug("Finnished removing host from cluster");
	}
	
	@Override
	public void addHostToCluster(String newToken) throws IOException, InterruptedException {
		try {
			//TODO: Howto enable threadPool again
			//TODO: Start MessagingService here somehow!
			//InetAddress adr = InetAddress.getByAddress(new byte[]{127,0,0,2});
			//MessagingService.instance().listen(adr);
			//TODO: Restart MessagingService somehow
			
			LOG.debug("Adding host to cluster - {}...",host);
			
			if(!nodeProbe.isJoined()) {
				LOG.debug("Join ring");
				nodeProbe.joinRing();
			}
			
			if(!nodeProbe.isThriftServerRunning()) {
				LOG.debug("Starting thrift-client");
				nodeProbe.startThriftServer();
			}
			
			if(!nodeProbe.isInitialized()) {
				LOG.debug("Starting gossip-protocol");
				nodeProbe.startGossiping();
			}

			nodeProbe.rebuild(null);//nodeProbe.getDataCenter());
			//nodeProbe.resetLocalSchema();
			
			nodeProbe.move(newToken);

			LOG.debug("Finnished adding host to cluster");
		} catch (ConfigurationException e) {
			LOG.error("Failed while moving node to new location");
		}
	}

	@Override
	public String generateNewToken() {
		String thisToken = nodeProbe.getToken();
		String previousToken = getPreviousToken(thisToken);
		BigInteger newToken = new AbsoluteCenterTokenGenerator().generateToken(new BigInteger(previousToken), new BigInteger(thisToken));
		LOG.debug("New token generated for host ["+host+"]: " + thisToken + " -> " + newToken.toString());
		return newToken.toString();
	}

	private String getPreviousToken(String thisToken) {
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
	
	private List<Entry<String, String>> getRingTokensSorted() {
		return sortMap(nodeProbe.getTokenToEndpointMap());
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

	@Override
	public void removeToken(String token) {
		LOG.debug("Remove token: " + token);
		nodeProbe.removeToken(token);
	}
}
