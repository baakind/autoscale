package org.uio.autoscale;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AutoscaleTest {
	private static Autoscale autoscale;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		autoscale = new Autoscale("Test Cluster");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		autoscale = null;
	}

	@Test
	public void testAutoscale() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitNewHost() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddHostToCluster() {
		String host = "127.0.0.1";
		int port = 9160;
		boolean result;
		
		result = autoscale.addHostToCluster(host, port);
		Assert.assertTrue(result);
	}

	@Test
	public void testRemoveHost() {
		String host = "127.0.0.1";
		int port = 9160;
		boolean result;
		
		result = autoscale.removeHostFromCluster(host, port);
		Assert.assertTrue(result);
	}

}
