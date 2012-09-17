package org.uio.autoscale.mock;

import me.prettyprint.cassandra.connection.ConcurrentHClientPool;
import me.prettyprint.cassandra.connection.HClientPool;
import me.prettyprint.cassandra.connection.RoundRobinBalancingPolicy;
import me.prettyprint.cassandra.connection.factory.HClientFactory;
import me.prettyprint.cassandra.service.CassandraHost;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = RoundRobinBalancingPolicy.class)
public class RoundRobinBalancingPolicyMock {
	
	@Mock
	public HClientPool createConnection(HClientFactory clientFactory, CassandraHost host) {
	  	return null;
	}
}
