package org.uio.autoscale.config;

/**
 * Configuration file for autoscale
 * @author andreas
 */
public class Config {
	public Boolean autoscale = true;
	public Integer intervall_timer = 1;
	public Integer min_number_of_nodes = 1;
	
	public MemoryOptions memory_options = new MemoryOptions();
	
	public DiskOptions disk_options = new DiskOptions();
}
