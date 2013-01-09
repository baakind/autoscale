package no.uio.master.autoscale;

import java.util.Set;

import no.uio.master.autoscale.host.CassandraHost;
import no.uio.master.autoscale.host.CassandraHostManager;
import no.uio.master.autoscale.host.HostManager;
import no.uio.master.autoscale.message.AgentMessage;
import no.uio.master.autoscale.message.enumerator.AgentMessageType;
import no.uio.master.autoscale.message.enumerator.AgentStatus;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Run test to verify connection to Linode-cluster
 * @author andreas
 *
 */
public class AutoscaleLinodeINTTest {
	private static Autoscale scaler;
	
	@AfterClass
	public static void tearDown() throws InterruptedException {
		Thread.sleep(20000);
		scaler = null;
	}


	@Test
	public void testStartAutoscaler() throws InterruptedException {
		Integer intervallTimerAgent = 1;
		Integer intervallTimerScaler = 10;
		Integer thresholdBreachLimit = 5;
		Integer minNumberOfNodes = 1;
		Double minMemoryUsage = 10.0;
		Double maxMemoryUsage = 90.0;
		Long minDiskSpace = 100L;
		Long maxDiskSpace = 200000L;
		String initHost = "97.107.133.122"; //109.74.200.57
		Integer initPort = 8002;

		scaler = new Autoscale(intervallTimerAgent, intervallTimerScaler, thresholdBreachLimit,
				minNumberOfNodes, minMemoryUsage, maxMemoryUsage, minDiskSpace, maxDiskSpace,
				initHost, initPort);
		
		Thread.sleep(15000L);
		
		
	}
	
	@Test
	public void testGetActiveNodes() throws InterruptedException {
		AgentMessage msg = new AgentMessage(AgentMessageType.STATUS);
		msg.put("status", AgentStatus.LIVE_NODES);
		
		HostManager<CassandraHost> hostManager = new CassandraHostManager();
		Set<CassandraHost> activeHosts;
		hostManager.updateActiveAndInactiveHosts();

		Thread.sleep(10000L); // Host will be removed
		activeHosts = hostManager.getActiveHosts();
		Assert.assertEquals(1, activeHosts.size());
	}

}
