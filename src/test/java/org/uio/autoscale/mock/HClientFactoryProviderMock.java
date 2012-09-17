package org.uio.autoscale.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.prettyprint.cassandra.connection.factory.HClientFactory;
import me.prettyprint.cassandra.connection.factory.HClientFactoryProvider;
import me.prettyprint.cassandra.connection.factory.HThriftClientFactoryImpl;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = HClientFactoryProvider.class)
public class HClientFactoryProviderMock {

	private static final Logger log = LoggerFactory.getLogger(HClientFactoryProviderMock.class);
	 @Mock
	 public static HClientFactory createFactory(CassandraHostConfigurator chc) {
		 	log.debug("HClientFactoryProvider mocked");
		    return new HThriftClientFactoryImpl();
	 }
}
