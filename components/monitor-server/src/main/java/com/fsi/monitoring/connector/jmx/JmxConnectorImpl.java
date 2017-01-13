package com.fsi.monitoring.connector.jmx;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.AbstractConnector;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.sun.management.OperatingSystemMXBean;

import sun.tools.jconsole.MemoryPoolProxy;
import sun.tools.jconsole.MemoryPoolStat;
import sun.tools.jconsole.ProxyClient;

public class JmxConnectorImpl 
extends AbstractConnector 
implements JmxConnector {

	private static final Logger logger = Logger.getLogger(JmxConnectorImpl.class);
	
	private ProxyClient proxyClient;
	
	private JmxConnectorConfig config;
	
	public JmxConnectorImpl(JmxConnectorConfig jmxConnectorConfig) {
		super(jmxConnectorConfig);
		this.config = jmxConnectorConfig;
	}

	public void openConnection() throws Exception {
		proxyClient = new ProxyClient(config.getConnectorContext(),
									  config.getPort(),
									  config.getUserName(),
									  config.getPassword());
		if (proxyClient==null)
			throw new Exception("An Error Occured while trying to connect to the JMX Proxy");
	}

	public void closeConnection() throws Exception {
		if (proxyClient != null) {
			proxyClient.markAsDead();
		}
	}
	
	public ClassLoadingMXBean getClassLoadingMXBean()
	throws ConnectorException {
		checkStatus();
		
		ClassLoadingMXBean res = null;
		
		try {
			res = proxyClient.getClassLoadingMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX ClassLoadingMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}
	
	public RuntimeMXBean getRuntimeMXBean()
	throws ConnectorException {
		checkStatus();
		
		RuntimeMXBean res = null;
		
		try {
			res = proxyClient.getRuntimeMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX RuntimeMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}

	public ThreadMXBean getThreadMXBean()
	throws ConnectorException {
		checkStatus();
		
		ThreadMXBean res = null;
		
		try {
			res = proxyClient.getThreadMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX ThreadMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}	
	
	public CompilationMXBean getCompilationMXBean()
	throws ConnectorException {
		checkStatus();
		
		CompilationMXBean res = null;
		
		try {
			res = proxyClient.getCompilationMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX CompilationMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}		
	
	public List<GarbageCollectorMXBean> getGarbageCollectorBeans()
	throws ConnectorException {
		checkStatus();	
		
		List<GarbageCollectorMXBean> res = null;

		try {
			res = (List<GarbageCollectorMXBean>) proxyClient.getGarbageCollectorMXBeans();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX GarbageCollectorMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}	
	
	
	public Map<String, MemoryUsage> getMemoryPoolMXBean()
	throws ConnectorException {
		checkStatus();
		
		Map<String, MemoryUsage> proxies = null;
		
		try {
			Collection<MemoryPoolProxy> poolProxies = proxyClient.getMemoryPoolProxies();
		
			proxies = new HashMap<String, MemoryUsage>();
			
			for(MemoryPoolProxy poolProxy : poolProxies) {
				MemoryPoolStat poolStat = poolProxy.getStat();
				proxies.put(poolStat.getPoolName(), poolStat.getUsage());
			}
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
	    }	
		
		if (proxies == null) {
			reportFailure("JMX MemoryPoolMXBean is null: " + getType() + "--" +  getName());
		}
		
		return proxies;
	}	
	
	
	public MemoryMXBean getMemoryMXBean()
	throws ConnectorException {
		checkStatus();
		
		MemoryMXBean res = null;
		
		try {
			res = proxyClient.getMemoryMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX MemoryMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}
	
	
	public OperatingSystemMXBean getSunOperatingSystemBean()	
	throws ConnectorException {
		checkStatus();
		
		OperatingSystemMXBean res = null;
		
		try {
			res = proxyClient.getSunOperatingSystemMXBean();
		} catch (Exception exc) {
			reportFailure("Error with JMX proxy client <" + getType() + "--" +  getName() + "> : " + exc.getMessage(), exc);
		}
		
		if (res == null) {
			reportFailure("JMX OperatingSystemMXBean is null: " + getType() + "--" +  getName());
		}
		
		return res;
	}		
	
	
	public String getConnectorContext() {
		return config.getConnectorContext();
	}

	public String getProcessName() {
		return config.getProcessName();
	}
	
}
