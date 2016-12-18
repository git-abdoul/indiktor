package com.fsi.monitoring.kpi.monitor.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.monitor.Monitor;


public class MonitorFactory {
	protected final static Logger LOG = Logger.getLogger(MonitorFactory.class);
	
	private DataModelPM dataModelPM;
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}
	
	public void initFactory() {}

	public Map<Long,Monitor> createMonitors(int logicalEnvId) throws Exception {
		Map<Long,Monitor> monitors = new HashMap<Long, Monitor>();
		
		Map<Long, MonitorConfig> monitorConfigs = dataModelPM.getMonitorConfigs(logicalEnvId);
			
		for (MonitorConfig monitorConfig: monitorConfigs.values()) {
				Class<Monitor> monitorClass;
				Monitor monitor = null;
				String className = monitorConfig.getMetricDomainConfig().getClassName();
				try {
					monitorClass = (Class<Monitor>)Class.forName(className);
					monitor = monitorClass.newInstance();
				} catch (ClassNotFoundException e) {
					throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e);
				} catch (InstantiationException e) {
					throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e);
				} catch (IllegalAccessException e) {
					throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e);
				}
				
				monitor.setMonitorConfig(monitorConfig);				
				monitors.put(monitorConfig.getId(),monitor);
		}
		
		return monitors;
	}
	
	public Monitor createMonitor(long monitorId) throws Exception {
		Monitor monitor = null;
		
		MonitorConfig monitorConfig = dataModelPM.getMonitorConfig(monitorId);
			
		Class<Monitor> monitorClass;
		String className = monitorConfig.getMetricDomainConfig().getClassName();
		try {
			monitorClass = (Class<Monitor>)Class.forName(className);
			monitor = monitorClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e.getMessage());
		} catch (InstantiationException e) {
			throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Exception("Error occured while creating Monitor" + "<"+monitorConfig.getContext()+"> : " + e.getMessage());
		}
		monitor.setMonitorConfig(monitorConfig);
		
		return monitor;
	}
}
