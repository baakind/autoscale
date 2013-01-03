package no.uio.master.autoscale.config;

import java.util.HashSet;
import java.util.Set;

import no.uio.master.autoscale.host.CassandraHost;

public class Config {
	/* Configurations changed by constructor arguments */
	public static Integer intervall_timer_agent = 1;
	public static Integer intervall_timer_scaler = 10;
	public static Integer threshold_breach_limit = 10;
	public static Integer min_number_of_nodes = 1;
	public static Double min_memory_usage = 10.0;
	public static Double max_memory_usage = 90.0;
	public static Long min_free_disk_space = 60L;
	public static Long max_free_disk_space = 20000L;
	
	/* Communication-ports */
	public static Integer master_input_port = 7798;
	public static Integer master_output_port = 7799;
	
	/* Agent JMX-port */
	public static Integer agent_default_jmx_port = 7199;
	
	/* Runtime configurations */
	private static volatile Set<CassandraHost> activeHosts = new HashSet<CassandraHost>(0);
	private static volatile Set<CassandraHost> inactiveHosts = new HashSet<CassandraHost>(0);
	
	public static void setActiveHosts(Set<CassandraHost> activeHosts) {
		synchronized (activeHosts) {
			Config.activeHosts = activeHosts;
		}
	}
	
	public static Set<CassandraHost> getActiveHosts() {
		synchronized(activeHosts) {
			return Config.activeHosts;
		}
	}
	
	public static void setInactiveHosts(Set<CassandraHost> inactiveHosts) {
		synchronized (Config.class) {
			Config.inactiveHosts = inactiveHosts;
		}
	}
	
	public static Set<CassandraHost> getInactiveHosts() {
		synchronized(Config.class) {
			return Config.inactiveHosts;
		}
	}
}
