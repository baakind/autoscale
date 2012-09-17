package org.uio.autoscale.mock;

import me.prettyprint.cassandra.connection.LoadBalancingPolicy;
import me.prettyprint.cassandra.connection.RoundRobinBalancingPolicy;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = CassandraHostConfigurator.class)
public class CassandraHostConfiguratorMock {
	
	@Mock
	public LoadBalancingPolicy getLoadBalancingPolicy() {
	    return new RoundRobinBalancingPolicy();
	  }

}
