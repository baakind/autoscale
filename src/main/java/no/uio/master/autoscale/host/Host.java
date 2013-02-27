package no.uio.master.autoscale.host;

/**
 * Holds data about a single host-instance of the cluster
 * @author andreas
 */
public interface Host {

	public String getHost();
	
	public String getIp();
	
	public Integer getPort();
	
	public void setHost(String host);
	
	public void setPort(Integer port);
}
