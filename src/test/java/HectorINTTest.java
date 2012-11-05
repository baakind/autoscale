import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class HectorINTTest {
	private static Cluster c;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		c = HFactory.getOrCreateCluster("KatanooCluster", "127.0.0.1");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		c = null;
	}

	@Test
	public void test() {
		//TODO: Fungerer i commandolinja, men ingenting skjer med serveren...
		c.addHost(new CassandraHost("127.0.0.1", 8001), false);
		c.addHost(new CassandraHost("127.0.0.2", 8002), false);
		c.addHost(new CassandraHost("127.0.0.3", 8003), false);
		HConnectionManager connectionManager = c.getConnectionManager();
		
		for(CassandraHost host : connectionManager.getHosts()) {
			System.out.println(host.getHost()+":"+host.getPort());
		}
		connectionManager.removeCassandraHost(new CassandraHost("127.0.0.1",9160));
		
		connectionManager.removeCassandraHost(new CassandraHost("127.0.0.2", 8002));
		
		
		connectionManager.addCassandraHost(new CassandraHost("127.0.0.2",8002));
	}

}
