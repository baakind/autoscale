package no.uio.master.autoscale.net;

import org.hyperic.sigar.Sigar;

public class ClusterMonitor {

	private static Sigar monitor;
	
	public ClusterMonitor() {
		monitor = new Sigar();
	}
}
