package no.uio.master.autoscale.node;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.prettyprint.cassandra.service.CassandraHost;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeMonitorTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 7199;
	private static Set<CassandraHost> nodes;
	
	@BeforeClass
	public static void setUp() {
		nodes = new HashSet<CassandraHost>();
		nodes.add(new CassandraHost(HOST, PORT));
	}
	
	@AfterClass
	public static void tearDown() {
		nodes = null;
	}
	
	@Test
	public void testNodeMonitor() throws IOException {
		NodeMonitor monitor = new NodeMonitor(nodes);
		Assert.assertNotNull(monitor);
	}

	@Test
	public void testGetHeapMemoryUsage() throws IOException {
		NodeMonitor monitor = new NodeMonitor(nodes);
		Assert.assertNotNull(monitor.getHeapMemoryUsage(HOST));
		Assert.assertNotSame(0.0, monitor.getHeapMemoryUsage(HOST));
		
		Assert.assertNull(monitor.getHeapMemoryUsage("111.222.333.444"));
	}

	@Test
	public void testGetNonHeapMemoryUsage() throws IOException {
		NodeMonitor monitor = new NodeMonitor(nodes);
		Assert.assertNotNull(monitor.getNonHeapMemoryUsage(HOST));
		Assert.assertNotSame(0.0, monitor.getNonHeapMemoryUsage(HOST));
		
		Assert.assertNull(monitor.getNonHeapMemoryUsage("111.222.333.444"));
	}

	@Test
	public void testGetClusterNodeData() throws IOException {
		NodeMonitor monitor = new NodeMonitor(nodes);
		Assert.assertNotNull(monitor);
		Collection<NodeData> data = monitor.getClusterNodeData();
		Assert.assertEquals(1, data.size());
	}

}
