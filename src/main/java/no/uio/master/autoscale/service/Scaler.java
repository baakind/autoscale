package no.uio.master.autoscale.service;

import java.util.List;

import no.uio.master.autoscale.util.HostWeight;

/**
 * Interface for the actual scaler-implementation.<br>
 * It gathers all breach-messages from current batch, and determine if 
 * any nodes should be scaled up and/or down.
 * 
 * @author andreas
 */
public interface Scaler extends Runnable  {

	/**
	 * Collect and empty breach-messages from listeners list.
	 */
	public void collectBreachMessages();
	
	/**
	 * Perform calculation upon collected breach-messages.
	 */
	public void performScaleCalculation();
	
	/**
	 * Select which node(s) to be scale up or down.<br>
	 * @param weightedResults
	 * @return
	 */
	public List<HostWeight> selectScaleEntities(List<HostWeight> weightedResults);

	/**
	 * Sort <tt>weights</tt> list based on the results.
	 * @param weights
	 */
	public void sortHostWeights(List<HostWeight> weights);
	
	/**
	 * Perform weighting of messages.<br>
	 * Results in a map containing <tt>node</tt> and <tt>score</tt><br>
	 * A positive score means the node needs to be scaled up.<br>
	 * A negative score means the node needs to be scaled down.<br>
	 * 
	 * @return
	 */
	public List<HostWeight> hostWeights();
}
