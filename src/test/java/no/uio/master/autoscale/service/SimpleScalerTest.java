package no.uio.master.autoscale.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;
import no.uio.master.autoscale.host.CassandraHostManager;
import no.uio.master.autoscale.host.HostManager;
import no.uio.master.autoscale.message.BreachMessage;
import no.uio.master.autoscale.message.enumerator.BreachType;
import no.uio.master.autoscale.util.HostWeight;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleScalerTest {

	private static SlaveListener slaveListener;
	private static SimpleCassandraScaler scaler;
	private static HConnectionManager connectionManager;
	private static HostManager<CassandraHost> nodeManager;
	private static Cluster c;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		c = HFactory.getOrCreateCluster("KatanooCluster", "127.0.0.1");
		connectionManager = c.getConnectionManager();
		nodeManager = new CassandraHostManager(connectionManager);
		slaveListener = new SlaveListener();
		scaler = new SimpleCassandraScaler(slaveListener, nodeManager);
		generateBreachMessages();
		scaler.collectBreachMessages();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		slaveListener = null;
		scaler = null;
		nodeManager = null;
		connectionManager = null;
		c = null;
	}

	@Test
	public void testHostWeights() {
		List<HostWeight> weights = scaler.hostWeights();
		Assert.assertEquals(3, weights.size());

		int value = weights.get(weights.indexOf(new HostWeight("127.0.0.1"))).getScore();
		Assert.assertEquals(-4, value);

		value = weights.get(weights.indexOf(new HostWeight("127.0.0.2"))).getScore();
		Assert.assertEquals(2, value);

		value = weights.get(weights.indexOf(new HostWeight("127.0.0.3"))).getScore();
		Assert.assertEquals(1, value);
	}

	@Test
	public void testSortHostWeights() {
		// - Fixed weightsList
		List<HostWeight> weights = new ArrayList<HostWeight>();
		weights.add(new HostWeight("127.0.0.1", -10));
		weights.add(new HostWeight("127.0.0.2", 88));
		weights.add(new HostWeight("127.0.0.3", 0));
		weights.add(new HostWeight("127.0.0.4", 25));

		scaler.sortHostWeights(weights);

		// Ascending order: 127.0.0.1, 127.0.0.3, 127.0.0.4, 127.0.0.2

		Assert.assertEquals("127.0.0.1", weights.get(0).getHost());
		Assert.assertEquals("127.0.0.3", weights.get(1).getHost());
		Assert.assertEquals("127.0.0.4", weights.get(2).getHost());
		Assert.assertEquals("127.0.0.2", weights.get(3).getHost());

		// - Retrieve list from scaler
		weights = scaler.hostWeights();
		scaler.sortHostWeights(weights);

		// Ascending order: 127.0.0.1, 127.0.0.3, 127.0.0.2
		Assert.assertEquals("127.0.0.1", weights.get(0).getHost());
		Assert.assertEquals("127.0.0.3", weights.get(1).getHost());
		Assert.assertEquals("127.0.0.2", weights.get(2).getHost());
	}

	@Test
	public void testMathAbs() {
		Integer val;

		val = -10;
		Assert.assertEquals(10, Math.abs(val));

		val = 0;
		Assert.assertEquals(0, Math.abs(val));
	}

	@Test
	public void testSelectScaleEntities() {
		// - Fixed weightsList
		List<HostWeight> weights = new ArrayList<HostWeight>();
		weights.add(new HostWeight("127.0.0.1", -88));
		weights.add(new HostWeight("127.0.0.2", 88));
		weights.add(new HostWeight("127.0.0.3", 0));
		weights.add(new HostWeight("127.0.0.4", 25));
		weights.add(new HostWeight("127.0.0.5", -2));

		List<HostWeight> scale = scaler.selectScaleEntities(weights);
		Assert.assertEquals(2, scale.size());

		boolean match = false;

		if (scale.get(0).getHost().equals("127.0.0.1") || scale.get(0).getHost().equals("127.0.0.2")) {
			match = true;
		}

		Assert.assertTrue(match);

	}
	
	@Test
	public void testPerformScaleCalculation() {
		scaler.performScaleCalculation();
	}

	/**
	 * Fake-insert initial breach-messages<br>
	 * - 127.0.0.1 = -4<br>
	 * - 127.0.0.2 = 2<br>
	 * - 127.0.0.3 = 1
	 */
	private static void generateBreachMessages() {

		BreachMessage<Long> msgLong = new BreachMessage<Long>(BreachType.MIN_DISK_USAGE, 10L);
		msgLong.setSenderHost("127.0.0.1");
		slaveListener.storeMessage(msgLong);

		msgLong = new BreachMessage<Long>(BreachType.MIN_DISK_USAGE, 11L);
		msgLong.setSenderHost("127.0.0.1");
		slaveListener.storeMessage(msgLong);

		BreachMessage<Long> msgLong2 = new BreachMessage<Long>(BreachType.MAX_DISK_USAGE, 200000L);
		msgLong2.setSenderHost("127.0.0.2");
		slaveListener.storeMessage(msgLong2);

		BreachMessage<Double> msgDouble = new BreachMessage<Double>(BreachType.MAX_MEMORY_USAGE, 92.0);
		msgDouble.setSenderHost("127.0.0.3");
		slaveListener.storeMessage(msgDouble);
	}

}
