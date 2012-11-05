package no.uio.master.autoscale.service;

import static org.junit.Assert.fail;

import java.io.IOException;

import no.uio.master.autoscale.host.CassandraHostCmd;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is an internal test!<br>
 * Require a Cassandra-cluster running, where one 
 * of the nodes are running at <tt>HOST:PORT</tt> and <tt>OTHER_HOST:OTHER_PORT</tt>
 * @author andreas
 *
 */
public class CassandraHostCmdINTTest {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8001;
	private static final String OTHER_HOST = "127.0.0.1";
	private static final int OTHER_PORT = 8003;
	
	private static HostCmd hostCmd;
	private static HostCmd otherHostCmd;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		hostCmd = new CassandraHostCmd(HOST, PORT);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		hostCmd = null;
	}

	@Test
	public void testGetToken() {
		String token = hostCmd.getToken();
		Assert.assertNotNull(token);
		Assert.assertNotSame("", token);
	}

	@Test
	public void testGetUptime() {
		long uptime = hostCmd.getUptime();
		Assert.assertNotNull(uptime);
		Assert.assertNotSame(0L, uptime);
	}

	@Test
	public void testGenerateNewToken() {
		String newToken = hostCmd.generateNewToken();
		Assert.assertNotNull(newToken);
	}

	@Test
	public void testRemoveHostFromCluster() throws InterruptedException {
		hostCmd.removeHostFromCluster();
	}

	@Test
	public void testAddHostToCluster() throws IOException, InterruptedException {
		// Retrieve previous token, as it has to be removed from another host
		String previousToken = hostCmd.getToken();
		hostCmd.addHostToCluster(hostCmd.generateNewToken());
		
		// Remove previous token of node from another node
//		otherHostCmd = new CassandraHostCmd(OTHER_HOST, OTHER_PORT);
//		otherHostCmd.removeToken(previousToken);
//		otherHostCmd.disconnect();
//		otherHostCmd = null;
	}


	@Test
	public void testDisconnect() {
		hostCmd.disconnect();
	}

}
