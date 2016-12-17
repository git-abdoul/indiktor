package com.fsi.monitoring.system.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.tools.jps.Arguments;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;
import com.fsi.monitoring.system.config.SystemAgentContext;
import com.fsi.monitoring.system.dto.SystemInfo;

public class MonitoringProcess {
	private static final Logger LOG = Logger.getLogger(MonitoringProcess.class);
	/**
	 * @uml.property  name="client"
	 * @uml.associationEnd  
	 */
	private SystemMonitoringHandler client;
		
	public MonitoringProcess() {}

	public SystemInfo monitor(String monitorType, String subType) {
		Class cls = null;
		Class getMethodArgs[] = null;
		Object invokeMethodArgs[] = null;	
		SystemException ex = null;
		
		try {
			cls = Class.forName(getHandlerName());
		} catch (ClassNotFoundException e) {
				ex = new SystemException("Class " + getHandlerName() + " not found", e,
						BaseException.EXCEPTION);
		}
		
		if (ex != null) {
			ex.logException(BaseException.EXCEPTION, LOG, true);
			return null;
		}
		
		String service = getService(monitorType, subType);		
		try {			
			Method meth = cls.getMethod(service, getMethodArgs);
			Object value = meth.invoke(client, invokeMethodArgs);
			if (value == null)
				return null;
			return (SystemInfo)value;
		} catch (SecurityException e) {
			ex = new SystemException(getHandlerName()+"."+service +"()"+" raised a security exception", e,
					BaseException.EXCEPTION);
		} catch (NoSuchMethodException e) {
			LOG.warn("Method <" + e.getMessage() + "> not yet implemented...");
		} catch (IllegalArgumentException e) {
			ex = new SystemException(getHandlerName()+"."+service+"()" +" 's arguments are incorrect", e,
					BaseException.EXCEPTION);
		} catch (IllegalAccessException e) {
			ex = new SystemException(getHandlerName()+"."+service +"()"+" cannot be accessed", e,
					BaseException.EXCEPTION);
		} catch (InvocationTargetException e) {
			ex = new SystemException(getHandlerName()+"."+service+"()" +" invocation failed", e,
					BaseException.EXCEPTION);
		} 
		if (ex != null)
			ex.logException(BaseException.EXCEPTION, LOG, true);		
		
		return null;
	}
	
	private String getHandlerName() {
		return SystemMonitoringHandler.class.getName();
	}
	
	private String getService(String monitorType, String subType) {
		if (subType == null) subType = "";
		String key = ("".equals(subType)) ? monitorType : monitorType + "_" + subType;
		return "get" + SystemAgentContext.getContext().getServices().getProperty(key);
	}	
	
	public MonitoredHost getJstatdClient() throws UnknownHostException, MonitorException {
		String hostname = InetAddress.getLocalHost().getHostName();
		int port = SystemAgentContext.getContext().getJstatdPort();
		String serverName = SystemAgentContext.getContext().getServerName();
		Arguments arguments = new Arguments(new String[] {hostname+":"+ port+"/"+serverName});
		HostIdentifier hostId = arguments.hostId();				
		MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);
		System.out.println("Connected to JStatd <" + hostname + ":" + port + "/"+ serverName+ ">");
		LOG.info("Connected to JStatd <" + hostname + ":" + port + "/"+ serverName+">");
		return monitoredHost;
	}
	
	public SystemMonitoringHandler getClient(MonitoredHost monitoredHost) {
		if (client == null) {
			client = new SystemMonitoringHandler(monitoredHost, new Sigar());
		}
		return client; 
	}
}
