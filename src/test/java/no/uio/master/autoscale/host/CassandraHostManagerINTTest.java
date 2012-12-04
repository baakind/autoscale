package no.uio.master.autoscale.host;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import no.uio.master.autoscale.config.Config;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraHostManagerINTTest {
	private static HostManager<CassandraHost> hostManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Config.getActiveHosts().add(new CassandraHost("127.0.0.2", 8002));
		hostManager = new CassandraHostManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		hostManager = null;
		Config.setActiveHosts(new HashSet<CassandraHost>());
	}

	@Test
	public void testGetActiveHosts() {
		Set<CassandraHost> activeHosts = hostManager.getActiveHosts();
		
	}

	@Test
	public void testGetInactiveHosts() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddHostToCluster() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveHostFromCluster() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActiveHost() {
		fail("Not yet implemented");
	}

}
