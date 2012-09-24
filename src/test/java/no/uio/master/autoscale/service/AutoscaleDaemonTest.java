package no.uio.master.autoscale.service;

import me.prettyprint.cassandra.service.template.CassandraClusterFactory;
import me.prettyprint.hector.api.Cluster;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Requires a running instance of Cassandra with HOST,PORT and CLUSTER_NAME 
 * configured correctly
 * 
 * @author toraba
 */
public class AutoscaleDaemonTest {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 7199;
	private static String CLUSTER_NAME = "KatanooCluster";
	private static Cluster cluster;
	private static AutoscaleDaemon daemon;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cluster = CassandraClusterFactory.getInstance(CLUSTER_NAME, HOST, PORT);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cluster = null;
	}

	@Test
	public void testAutoscaleDaemon() {
		daemon = new AutoscaleDaemon(cluster);
		Assert.assertNotNull(daemon);
	}

}
