package com.fsi.monitoring.connector.jmx;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.ConnectorException;
import com.sun.management.OperatingSystemMXBean;

public interface JmxConnector 
extends Connector {

	String getProcessName();
	
	ClassLoadingMXBean getClassLoadingMXBean() throws ConnectorException;
	RuntimeMXBean getRuntimeMXBean() throws ConnectorException;
	CompilationMXBean getCompilationMXBean() throws ConnectorException;
	MemoryMXBean getMemoryMXBean() throws ConnectorException;
	Map<String, MemoryUsage> getMemoryPoolMXBean() throws ConnectorException;
	List<GarbageCollectorMXBean> getGarbageCollectorBeans() throws ConnectorException;
	
	ThreadMXBean getThreadMXBean() throws ConnectorException;
	
	OperatingSystemMXBean getSunOperatingSystemBean() throws ConnectorException;	
	
}
