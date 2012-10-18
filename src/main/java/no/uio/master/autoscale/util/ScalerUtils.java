package no.uio.master.autoscale.util;

import java.util.HashMap;
import java.util.Map;

import no.uio.master.autoscale.slave.message.enumerator.BreachType;

/**
 * Extended functionality for the Scaler-class.
 * @author andreas
 */
public class ScalerUtils {

	private static Map<BreachType, Integer> priorities;
	
	public ScalerUtils() {
		priorities = new HashMap<BreachType, Integer>();

		// Positive integers = scale-up
		priorities.put(BreachType.MAX_DISK_USAGE, 	2);
		priorities.put(BreachType.MAX_MEMORY_USAGE, 1);

		// Negative integers = scale-down
		priorities.put(BreachType.MIN_MEMORY_USAGE, -1);
		priorities.put(BreachType.MIN_DISK_USAGE, 	-2);
		
	}
	/**
	 * Retrieve a map of BreachTypes and the priority of each.<br>
	 * &nbsp; - Lowest priority: 0<br>
	 * &nbsp; - Highest priority: highest-integer
	 */
	public Map<BreachType, Integer> getBreachMessagePriorities() {
		return priorities;
	}
	
	/**
	 * Get priority of BreachType. If not found, return 0, which 
	 * represent a stable node (either up or down)
	 * @param type
	 * @return
	 */
	public Integer getPriorityOfBreachType(BreachType type) {
		Integer pri = 0;
		
		if(priorities.containsKey(type)) {
			pri = priorities.get(type);
		}
		
		return pri;
	}
}
