package no.uio.master.autoscale.cassandra;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import no.uio.master.autoscale.node.HostCmd;

import org.apache.cassandra.config.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraHostCmdINTTest {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8001;
	
	private static HostCmd hostCmd;

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
	}

	@Test
	public void testMoveNode() throws IOException, InterruptedException, ConfigurationException {
		int token = (int)(123467890 * Math.random());
		
		final String oldToken = hostCmd.getToken();
		hostCmd.moveNode(String.valueOf(token));
		
		
		final String newToken = hostCmd.getToken();
		Assert.assertNotNull(oldToken);
		Assert.assertNotNull(newToken);
		Assert.assertNotSame(oldToken, newToken);
	}

	@Test
	public void testGetUptime() {
		Long uptime = hostCmd.getUptime();
		Assert.assertNotNull(uptime);
	}

	@Test
	public void testPrepareInactive() {
		hostCmd.prepareInactive();
	}

	@Test
	public void testPrepareActive() {
		hostCmd.prepareActive();
	}
	
	@Test
	public void testGetRingTokens() {
		List<Entry<String, String>> tokenHosts = hostCmd.getRingTokensSorted();
		Assert.assertNotNull(tokenHosts);
		Assert.assertNotSame(0, tokenHosts.size());
	}
	
	@Test
	public void testGenerateNewToken() {
		String newToken = hostCmd.generateNewToken();
		Assert.assertNotNull(newToken);
	}

}
