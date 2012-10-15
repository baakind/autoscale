package no.uio.master.autoscale.node;

import java.util.Set;

/**
 * Interface the host-manager for the cluster-nodes.
 * @author andreas
 * @param H Node-object
 */
public interface HostManager<H> {

	/**
	 * Initialize a new node.<br>
	 * The node should startup, and in some cases the node 
	 * will not connect to the cluster automatically, or you 
	 * may not want it to connect because it should startup as 
	 * an available node
	 * @param host
	 * @param port
	 * @return
	 */
	public H initNewNode(String host, int port);
	
	/**
	 * Should initialize an update against the cluster to update the 
	 * local list of active nodes. It should also match the current 
	 * list against the new list, to see if any nodes are missing in 
	 * the new list, which exists in the old. This probably means that 
	 * the nodes are taken down / shutdown, and should be moved to the 
	 * list of inactive nodes.
	 */
	public void updateActiveNodes();
	
	/**
	 * Should perform actions upon adding the provided node to the cluster, 
	 * as well as appending it to the list of active nodes. Remove from inactive 
	 * nodes if exists.
	 * @param node
	 * @return true i the node was successfully added to the cluster
	 */
	public boolean addNodeToCluster(H node);
	
	/**
	 * Should perform actions upon removing the provided node from the cluster,
	 * as well as appending it to the list of inactive nodes, as well as removing 
	 * it from active nodes.
	 * @param host
	 * @return true if the node was successfully removed from the cluster
	 */
	public boolean removeNodeFromCluster(H host);
	
	/* Inactive nodes */
	
	/**
	 * Return local set of inactive nodes.
	 * @return
	 */
	public Set<H> getInactiveNodes();
	
	/**
	 * Retrieve the node (if exists) from inactiveHosts-list
	 * @param host
	 * @return Node upon success, null if not found
	 */
	public H getInactiveNode(String host);
	
	/**
	 * Update inactive-nodes set to the provided set.
	 * @param inactiveNodes
	 */
	public void setInactiveNodes(Set<H> inactiveNodes);
	
	/**
	 * Add provided node to set of inactive nodes.
	 * @param inactiveNode
	 */
	public void addInactiveNode(H inactiveNode);
	
	/**
	 * Remove inactive node from set of inactive nodes.
	 * @param inactiveNode
	 */
	public void removeInactiveNode(H inactiveNode);
	
	/**
	 * Number of inactive nodes currently registered.
	 * @return
	 */
	public int getNumberOfInactiveNodes();

	/* Active nodes */
	
	/**
	 * Return set of current registered active nodes.
	 * @return
	 */
	public Set<H> getActiveNodes();
	
	/**
	 * Get active node from activeNodes-list 
	 * @param host
	 * @return Node upon success, null if not found
	 */
	public H getActiveNode(String host);
	
	/**
	 * Update active-nodes set to the provided set.
	 * @param activeNodes
	 */
	public void setActiveNodes(Set<H> activeNodes);
	
	/**
	 * Add provided node to the active node set.
	 * @param activeNode
	 */
	public void addActiveNode(H activeNode);
	
	/**
	 * Remove node from active-node set.
	 * @param activeNode
	 */
	public void removeActiveNode(H activeNode);
	
	/**
	 * Number of currently registered active nodes.
	 * @return
	 */
	public int getNumberOfActiveNodes();

}
