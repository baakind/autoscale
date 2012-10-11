package no.uio.master.autoscale.config;

import me.prettyprint.hector.api.Cluster;

public class Config {
	/* Configurations changed by constructor arguments */
	public static Integer intervall_timer = 1; 
	public static Integer threshold_breach_limit = 10;
	public static Integer min_number_of_nodes = 1;
	public static Double min_memory_usage = 10.0;
	public static Double max_memory_usage = 90.0;
	public static Long min_free_disk_space = 60L;
	public static Long max_free_disk_space = 20000L;
	public static String storage_location = "/";
	public static Cluster cluster = null;
	
	/* Comunication-ports */
	public static Integer master_input_port = 7798;
	public static Integer master_output_port = 7799;
}
