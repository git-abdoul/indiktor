package com.fsi.monitoring.kpi.monitor.jmx;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.Monitor;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxMemoryResourceData;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxProcessClassResourceData;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxProcessGCResourceData;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxProcessJVMResourceData;
import com.sun.management.OperatingSystemMXBean;


public class JmxMonitor
extends JmxMonitorTask 
implements Monitor {
	private static final Logger LOG = Logger.getLogger(JmxMonitor.class);
	
	private Map<String, MemoryUsage> proxies;

	@Override
	protected void preFetchs() throws Exception {
		super.preFetchs();
		proxies = jmxConnector.getMemoryPoolMXBean();			
	}	
	
//	public JmxMemoryResourceData fetchJMX_PROCESS_CODE_CACHE_MEMORY()
//	throws ConnectorException {		
//		MemoryUsage usage = proxies.get("Code Cache");	
//		if (usage == null)
//			return null;
//		return new JmxMemoryResourceData(jmxConnector.getProcessName(), new Date(), usage);
//	}
//	
//	public JmxMemoryResourceData fetchJMX_PROCESS_EDEN_MEMORY()
//	throws ConnectorException {	
//		MemoryUsage usage = proxies.get("Eden Space");	
//		if (usage == null)
//			usage = proxies.get("PS Eden Space");
//		if (usage == null)
//			return null;
//		return new JmxMemoryResourceData(jmxConnector.getProcessName(), new Date(), usage);
//	}
//	
//	public JmxMemoryResourceData fetchJMX_PROCESS_PERMANENT_MEMORY()
//	throws ConnectorException {	
//		MemoryUsage usage = proxies.get("Perm Gen");	
//		if (usage == null)
//			usage = proxies.get("PS Perm Gen");
//		if (usage == null)
//			return null;
//		return new JmxMemoryResourceData(jmxConnector.getProcessName(), new Date(), usage);
//	}
//	
//	public JmxMemoryResourceData fetchJMX_PROCESS_SURVIVOR_MEMORY()
//	throws ConnectorException {		
//		MemoryUsage usage = proxies.get("Survivor Space");	
//		if (usage == null)
//			usage = proxies.get("PS Survivor Space");
//		if (usage == null)
//			return null;
//		return new JmxMemoryResourceData(jmxConnector.getProcessName(), new Date(), usage);
//	}
//	
//	public JmxMemoryResourceData fetchJMX_PROCESS_TENURED_MEMORY()
//	throws ConnectorException {	
//		MemoryUsage usage = proxies.get("Tenured Gen");	
//		if (usage == null)
//			usage = proxies.get("PS Old Gen");
//		if (usage == null)
//			return null;
//		return new JmxMemoryResourceData(jmxConnector.getProcessName(), new Date(), usage);
//	}
	
	public JmxMemoryResourceData fetchJMX_PROCESS_MEMORY_POOL()
	throws ConnectorException {	
		return new JmxMemoryResourceData(jmxConnector.getProcessName(), proxies, new Date(), true);
	}
	
	public JmxMemoryResourceData fetchJMX_PROCESS_NON_HEAP_MEMORY()
	throws ConnectorException {			
		MemoryMXBean memoryMXBean = jmxConnector.getMemoryMXBean();				
		MemoryUsage usage = memoryMXBean.getNonHeapMemoryUsage();
		Map<String, MemoryUsage> usages = new HashMap<String, MemoryUsage>();
		usages.put(jmxConnector.getProcessName(), usage);
		return new JmxMemoryResourceData(jmxConnector.getProcessName(), usages, new Date(), false);
	}
	
	public JmxMemoryResourceData fetchJMX_PROCESS_HEAP_MEMORY()
	throws ConnectorException {	
		MemoryMXBean memoryMXBean = jmxConnector.getMemoryMXBean();		
		MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
		Map<String, MemoryUsage> usages = new HashMap<String, MemoryUsage>();
		usages.put(jmxConnector.getProcessName(), usage);
		return new JmxMemoryResourceData(jmxConnector.getProcessName(), usages, new Date(), false);
	}
	
	public JmxProcessGCResourceData fetchJMX_PROCESS_GC()
	throws ConnectorException {	
		List<GarbageCollectorMXBean> garbageCollectorMXBeans = jmxConnector.getGarbageCollectorBeans();		
		return new JmxProcessGCResourceData(garbageCollectorMXBeans, jmxConnector.getProcessName(), new Date());
	}
	
	public JmxProcessClassResourceData fetchJMX_PROCESS_CLASS()
	throws ConnectorException {		
		ClassLoadingMXBean classLoadingMXBean = jmxConnector.getClassLoadingMXBean();
		return new JmxProcessClassResourceData(classLoadingMXBean, jmxConnector.getProcessName(), new Date());
	}
	
	public JmxProcessJVMResourceData fetchJMX_PROCESS_JVM()
	throws ConnectorException {
		RuntimeMXBean runtimeMXBean = jmxConnector.getRuntimeMXBean();
		CompilationMXBean compilationMXBean = jmxConnector.getCompilationMXBean();
		OperatingSystemMXBean operatingSystemMXBean = jmxConnector.getSunOperatingSystemBean();
		return new JmxProcessJVMResourceData(runtimeMXBean, compilationMXBean, operatingSystemMXBean, jmxConnector.getProcessName(), new Date());
	}	
}






	





