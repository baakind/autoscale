package org.uio.autoscale.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import me.prettyprint.cassandra.connection.HClientPool;
import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.connection.HOpTimer;
import me.prettyprint.cassandra.connection.factory.HClientFactory;
import me.prettyprint.cassandra.connection.factory.HClientFactoryProvider;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import mockit.Mock;
import mockit.MockClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MockClass(realClass = HConnectionManager.class)
public class HConnectionManagerMock {
	
	private static final Logger log = LoggerFactory.getLogger(HConnectionManagerMock.class);
	
	private static ConcurrentMap<CassandraHost,HClientPool> hostPools;
	private static ConcurrentMap<CassandraHost,HClientPool> suspendedHostPools;
	private HClientFactory clientFactory;
	private HClientPool pool;

	@Mock
	public void $init(String clusterName, CassandraHostConfigurator cassandraHostConfigurator) {
		log.debug("HConnectionManager mocked");
		clientFactory = HClientFactoryProvider.createFactory(cassandraHostConfigurator);
		CassandraHost cassandraHost = new CassandraHost("127.0.0.1",9160);
		
		pool = cassandraHostConfigurator.getLoadBalancingPolicy().createConnection(clientFactory, cassandraHost);
		
		hostPools.put(new CassandraHost("127.0.0.2", 9160), pool);
		
		hostPools = new ConcurrentHashMap<CassandraHost, HClientPool>();
	    suspendedHostPools = new ConcurrentHashMap<CassandraHost, HClientPool>();
	}
	
	@Mock
	public boolean addCassandraHost(CassandraHost cassandraHost) {
		return true;
	}
	
	@Mock
	public boolean removeCassandraHost(CassandraHost cassandraHost) {
		return true;
	}
	
	@Mock
	public boolean suspendCassandraHost(CassandraHost cassandraHost) {
		return true;
	}
	
	@Mock
	public boolean unsuspendCassandraHost(CassandraHost cassandraHost) {
		return true;
	}
	
	@Mock
	public Set<CassandraHost> getSuspendedCassandraHosts() {
	    return suspendedHostPools.keySet();
	  }

	@Mock
	public Set<CassandraHost> getHosts() {
	    return Collections.unmodifiableSet(hostPools.keySet());
	}
	
	@Mock
	public List<String> getStatusPerPool() {
		List<String> stats = new ArrayList<String>();
    	stats.add("Mocked");
    	return stats;
	}
}
