package no.uio.master.autoscale.node;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.config.ConfigurationException;

/**
 * Interface the specific commands for <tt>host</tt>.
 * @author andreas
 *
 */
public interface HostCmd {

	/**
	 * Connect to <tt>host:port</tt>.<br>
	 * Usually, the connect-method is initiated 
	 * within the constructor.<br>
	 * 
	 * @param host
	 * @param port
	 */
	public void connect(String host, int port);
	
	/**
	 * Retrieve token of host in ring.
	 * @return
	 */
	public String getToken();
	
	/**
	 * Move node to new location.
	 * @param newToken
	 */
	public void moveNode(String newToken) throws IOException, InterruptedException, ConfigurationException;
	
	/**
	 * Get current up-time in milliseconds of the node.<br>
	 * May be used when deciding which node should be scale down/up.
	 * @return
	 */
	public long getUptime();
	
	/**
	 * Prepare the node for going into inactive-mode.<br>
	 * &nbsp; - E.g., shut down communication.
	 */
	public void prepareInactive();
	
	/**
	 * Prpeare the node for going into active-mode.<br>
	 * &nbsp; - E.g., start communication.
	 */
	public void prepareActive();
	
	/**
	 * Generate new token.
	 * @return
	 */
	public String generateNewToken();
	
	/**
	 * A sorted list of entries, consisting of <tt>token (key)</tt> and <tt> host (value)</tt>.
	 * @return
	 */
	public List<Entry<String, String>> getRingTokensSorted();
	
	/**
	 * Get token for currentNode-1.<br>
	 * @param thisToken
	 * @return
	 */
	public String getPreviousToken(String thisToken);
}
