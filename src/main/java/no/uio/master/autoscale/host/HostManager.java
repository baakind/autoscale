package no.uio.master.autoscale.host;

import java.util.Set;

/**
 * Manager responsible for managing all the hosts in the cluster, 
 * and keep record of which is currently active and inactive.
 * 
 * @author andreas
 *
 * @param <H>
 */
public interface HostManager<H> {

	/**
	 * Retrieve all active hosts
	 * @return
	 */
	public Set<H> getActiveHosts();
	
	/**
	 * Get all inactive hosts
	 * @return
	 */
	public Set<H> getInactiveHosts();
	
	/**
	 * Add <tt>host</tt> to the cluster
	 * @param host
	 */
	public void addHostToCluster(H host);
	
	/**
	 * Remove <tt>host</tt> from the cluster
	 * @param host
	 */
	public void removeHostFromCluster(H host);
	
	/**
	 * Get <tt>host</tt> from active-hosts. 
	 * Return null if not found.
	 * @param host
	 * @return
	 */
	public H getActiveHost(String host);
	
	/**
	 * Get <tt>host</tt> from inactive-hosts.
	 * Return null if not found.
	 * @param host
	 * @return
	 */
	public H getInactiveHost(String host);
	
	/**
	 * Initialize process of updating active- and inactive hosts.
	 */
	public void updateActiveAndInactiveHosts();

}