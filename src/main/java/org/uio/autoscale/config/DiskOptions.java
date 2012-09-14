package org.uio.autoscale.config;

/**
 * Disk options
 * @author andreas
 */
public class DiskOptions {
	public Long max_free_usage_mb = 20000L;
	public Long min_free_usage_mb = 60L;
	public Integer threshold_breach_limit = 10;
}
