package no.uio.master.autoscale;

import java.io.IOException;
import java.io.InputStream;
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

public class AutoscaleINTTest {
	
	private static Integer cassandraPID;
	private static Integer agentPID;
	private static Autoscale scaler;
	private static HostManager<CassandraHost> hostManager;
	
	@BeforeClass
	public static void setUp() throws InterruptedException, IOException {
		// Make sure proper loopback is initialized for 127.0.0.2
		
		// Startup Cassandra-instance #2
		//Runtime.getRuntime().exec("/Users/andreas/UiO/cassandra-runtime/2/apache-cassandra-1.1.5/bin/cassandra -f");
		//Thread.sleep(1000);
		//
		//cassandraPID = getPID("/Users/andreas/UiO/cassandra-runtime/2/apache-cassandra-1.1.5");
		
		// Startup Autoscale-agent for Cassandra-instance #2
		Runtime.getRuntime().exec("/Users/andreas/UiO/gitrepos/autoscale-agent/bin/autoscale");
		agentPID = getPID("/Users/andreas/UiO/gitrepos/autoscale-agent");
		
	}
	
	@AfterClass
	public static void tearDown() throws InterruptedException, IOException {
		if(null != cassandraPID) {
			Runtime.getRuntime().exec(String.format("kill %d", cassandraPID.intValue()));
		}
		
		if(null != agentPID) {
			Runtime.getRuntime().exec(String.format("kill %d", agentPID.intValue()));
		}
		
		if(null != scaler) {
			scaler.shutdownAutoscaler();
		}
		scaler = null;
		cassandraPID = null;
		agentPID = null;
	}

	@Test
	public void testAutoscale() throws InterruptedException {
		Integer intervallTimerAgent = 1;
		Integer intervallTimerScaler = 10;
		Integer thresholdBreachLimit = 5;
		Integer minNumberOfNodes = 1;
		Double minMemoryUsage = 10.0;
		Double maxMemoryUsage = 90.0;
		Long minDiskSpace = 1000L;
		Long maxDiskSpace = 200000L;
		String storageLocation = "/";
		String initHost = "127.0.0.2";
		Integer initPort = 8002;

		scaler = new Autoscale(intervallTimerAgent, intervallTimerScaler, thresholdBreachLimit,
				minNumberOfNodes, minMemoryUsage, maxMemoryUsage, minDiskSpace, maxDiskSpace, storageLocation,
				initHost, initPort);
		
		Thread.sleep(10000L);
	}
	
	@Test
	public void testGetActiveNodes() throws InterruptedException {
		AgentMessage msg = new AgentMessage(AgentMessageType.STATUS);
		msg.put("status", AgentStatus.LIVE_NODES);
		
		hostManager = new CassandraHostManager();
		hostManager.updateActiveAndInactiveHosts();
		Thread.sleep(10000L);
		Set<CassandraHost> activeHosts = hostManager.getActiveHosts();
		Assert.assertEquals(3, activeHosts.size());
	}
	
	private static Integer getPID(String str) {
		Integer pid = null;
		String[] args = new String[3];
		args[0] = "pgrep";
		args[1] = "-f";
		args[2] = str;
		
		try {
			Process p = Runtime.getRuntime().exec(args);
			
			InputStream iStream = p.getInputStream();

			int n = 0;
			String tempString = "0";
			while((n = iStream.read()) > 0) {
				char ch = (char)n;
				tempString += ch;
			}

			String[] splitString = tempString.split("\\r?\\n");
			
			pid = Integer.valueOf(splitString[splitString.length-1].trim());
		} catch (Exception e) {
			Assert.fail("Failed to retrieve PID - " + e.getMessage());
		}
		
		return pid;
	}
	
}
