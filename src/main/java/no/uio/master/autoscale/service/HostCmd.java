
package no.uio.master.autoscale.service;

import java.io.IOException;


/**
 * Host (node) command-specific interface<br>
 * Should connect to the host (node) in the constructor
 * @author andreas
 */
public interface HostCmd {

	/**
	 * Disconnect from the current host
	 */
	public void disconnect();
	
	/**
	 * Get token for current host
	 * @return
	 */
	public String getToken();
	
	/**
	 * Get uptime in milliseconds
	 * @return
	 */
	public long getUptime();
	
	/**
	 * Remove host from cluster
	 * @throws InterruptedException
	 */
	public void removeHostFromCluster() throws InterruptedException;
	
	/**
	 * Add host to cluster at <tt>newToken</tt> location
	 * @param newToken
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void addHostToCluster(String newToken)  throws IOException, InterruptedException;
	
	/**
	 * Generate new token
	 * @return
	 */
	public String generateNewToken();
	
	/**
	 * Removes token from cluster.
	 * @param token
	 */
	public void removeToken(String token);
}
