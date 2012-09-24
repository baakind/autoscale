package no.uio.master.autoscale.node;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import no.uio.master.autoscale.Autoscale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Autoscale JMX operations.<br>
 * Code from org.apache.cassandra.tools#NodeProbe
 * @author toraba
 */
public class NodeData {
	private static Logger LOG = LoggerFactory.getLogger(NodeData.class);
	
	private static final String fmtUrl = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
	private static final int defaultPort = 7199;
	final String host;
	final int port;
	private String username;
	private String password;
	
	private JMXConnector jmxc;
	private MBeanServerConnection mbeanServerConn;
	private MemoryMXBean memoryMXBean;
	
	public NodeData(String host, int port, String username , String password) throws IOException {
		assert username != null && !username.isEmpty() && password != null && !password.isEmpty()
	               : "neither username nor password can be blank";

	        this.host = host;
	        this.port = port;
	        this.username = username;
	        this.password = password;
	}
	
	public NodeData(String host, int port) throws IOException {
		LOG.debug("Initialize host: " + host + ", port: " + port);
		this.host = host;
        this.port = port;
	}
	
	public NodeData(String host) throws IOException {
		LOG.debug("Initialize host: " + host);
		this.host = host;
        this.port = defaultPort;
	}
	
	public void connect() throws IOException  {
			LOG.debug("Connect init...");
			JMXServiceURL jmxUrl = null;
				jmxUrl = new JMXServiceURL(String.format(fmtUrl, host, port));

			Map<String,Object> env = new HashMap<String,Object>();
			if(username != null) {
				String[] creds = { username, password };
				env.put(JMXConnector.CREDENTIALS, env);
			}
				jmxc = JMXConnectorFactory.connect(jmxUrl,env);
				mbeanServerConn = jmxc.getMBeanServerConnection();
			
			// MemoryMXBean
				memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
			LOG.debug("Connected");
	}
	
	public void close() throws IOException {
        jmxc.close();
    }
	
	
	/**
	 * Percentage used of heap memory
	 * @return
	 * @throws IOException 
	 */
	public Double heapMemoryUsed() throws IOException {
		connect();
		Double mem = 0.0;
		
		Long used = memoryMXBean.getHeapMemoryUsage().getUsed();
		Long max = memoryMXBean.getHeapMemoryUsage().getMax();
		
		mem = ((double) used / (double) max) * 100; 
		close();
		return mem;
	}
	
	/**
	 * Percentage used of non-heap memory
	 * @return
	 * @throws IOException 
	 */
	public Double nonHeapMemoryUsed() throws IOException {
		connect();
		Double mem = 0.0;
		
		Long used = memoryMXBean.getNonHeapMemoryUsage().getUsed();
		Long max = memoryMXBean.getNonHeapMemoryUsage().getMax();
		
		mem = ((double) used / (double) max) * 100; 
		close();
		return mem;
	}
	
	
	public String getHost() {
		return this.host;
	}
	
	public Integer getPort() {
		return this.port;
	}

	@Override
	public String toString() {
		return "NodeData [host=" + host + ", port=" + port + "]";
	}
}
