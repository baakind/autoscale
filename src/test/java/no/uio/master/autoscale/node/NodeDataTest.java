package no.uio.master.autoscale.node;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeDataTest {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 7199;

	@Test
	public void testNodeData2() throws IOException {
		NodeData node = new NodeData(HOST, PORT);
		Assert.assertNotNull(node);
	}

	@Test
	public void testNodeData() throws IOException {
		NodeData node = new NodeData(HOST);
		Assert.assertNotNull(node);
	}

	@Test
	public void testHeapMemoryUsed() throws IOException {
		NodeData node = new NodeData(HOST);
		Assert.assertNotNull(node);
		Assert.assertNotSame(0.0, node.heapMemoryUsed());
	}

	@Test
	public void testNonHeapMemoryUsed() throws IOException {
		NodeData node = new NodeData(HOST);
		Assert.assertNotNull(node);
		Assert.assertNotSame(0.0, node.nonHeapMemoryUsed());
	}

	@Test
	public void testGetHost() throws IOException {
		NodeData node = new NodeData(HOST);
		Assert.assertNotNull(node);
		Assert.assertEquals(HOST, node.getHost());
	}

	@Test
	public void testGetPort() throws IOException {
		NodeData node = new NodeData(HOST);
		Assert.assertNotNull(node);
		Assert.assertEquals(PORT, node.getPort().intValue());
	}

}
