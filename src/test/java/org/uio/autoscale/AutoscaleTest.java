package org.uio.autoscale;

import me.prettyprint.cassandra.service.CassandraHost;
import mockit.Mockit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uio.autoscale.mock.CassandraHostConfiguratorMock;
import org.uio.autoscale.mock.HClientFactoryProviderMock;
import org.uio.autoscale.mock.HConnectionManagerMock;
import org.uio.autoscale.mock.RoundRobinBalancingPolicyMock;


//@RunWith(JMockit.class)
public class AutoscaleTest {
	private static Autoscale autoscale;
	private static final String CLUSTER_NAME = "Test Cluster";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Mockit.setUpMocks(HConnectionManagerMock.class,HClientFactoryProviderMock.class, CassandraHostConfiguratorMock.class, RoundRobinBalancingPolicyMock.class);
		autoscale = new Autoscale(CLUSTER_NAME, "127.0.0.1","127.0.0.2");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		autoscale = null;
	}

	@Test
	public void testAutoscale() {
		//fail("Not yet implemented");
	}

	@Test
	public void testInitNewHost() {
		//fail("Not yet implemented");
	}

	@Test
	public void testAddHostToCluster() {
		String host = "127.0.0.1";
		int port = 9160;
		boolean result;
		CassandraHost node = autoscale.initNewHost(host, port);
		
		result = autoscale.addHostToCluster(node);
		Assert.assertTrue(result);
	}

	@Test
	public void testRemoveHost() {
		String host = "127.0.0.1";
		boolean result;
		CassandraHost node = autoscale.getActiveHost(host);
		Assert.assertNotNull(node);
		
		result = autoscale.removeHostFromCluster(node);
		Assert.assertTrue(result);
	}

}
