package no.uio.master.autoscale;

import org.junit.Test;

/**
 * Run test to verify connection to Linode-cluster
 * @author andreas
 *
 */
public class AutoscaleLinodeINTTest {

	@Test
	public void testStartAutoscaler() throws InterruptedException {
		Integer intervallTimerAgent = 1;
		Integer intervallTimerScaler = 10;
		Integer thresholdBreachLimit = 5;
		Integer minNumberOfNodes = 1;
		Double minMemoryUsage = 10.0;
		Double maxMemoryUsage = 90.0;
		Long minDiskSpace = 200L;
		Long maxDiskSpace = 200000L;

		// Init Node 1
		new Autoscale(intervallTimerAgent, intervallTimerScaler, thresholdBreachLimit,
				minNumberOfNodes, minMemoryUsage, maxMemoryUsage, minDiskSpace, maxDiskSpace,
				"97.107.133.122", 8002);
		
		// Init Node 2
		new Autoscale(intervallTimerAgent, intervallTimerScaler, thresholdBreachLimit,
				minNumberOfNodes, minMemoryUsage, maxMemoryUsage, minDiskSpace, maxDiskSpace,
				"109.74.200.57", 8002);
		
		// As long as the test runs
		Long minutes = 15L;
		Long sleepTime = minutes * 60000L;
		Thread.sleep(sleepTime);
		
		
	}

}
