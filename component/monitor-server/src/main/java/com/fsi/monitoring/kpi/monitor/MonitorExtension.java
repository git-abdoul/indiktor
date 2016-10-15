package com.fsi.monitoring.kpi.monitor;

import java.util.Map;

import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;


public abstract class MonitorExtension {	
	private Map<String,Connector> connectors;
	private MonitorConfig monitorConfig;
	private LogicalEnv logicalEnv;
	private IkrStaticDomain type;
	
	protected abstract void preFetchs()throws Exception;
	protected abstract void postFetchs() throws Exception;
	
	public final void setConnectors(Map<String,Connector> connectors) {
		this.connectors = connectors;
	}
	
	public final void setMonitorConfig(MonitorConfig monitorConfig) {
		this.monitorConfig = monitorConfig;
	}
	
	public final void setType(IkrStaticDomain type) {
		this.type = type;
	}
	
	public final void setLogicalEnv(LogicalEnv logicalEnv) {
		this.logicalEnv = logicalEnv;
	}
	
	public Connector getConnector(String connectorType) {
		Connector connector = null;
		if (connectors!=null)
			connector = connectors.get(connectorType);
		return connector;
	}
	
	public Map<String, Connector> getConnectors() {
		return connectors;
	}
	
	public IkrStaticDomain getType() {
		return type;
	}

	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}	
	
	public String getAttribute(String key) {
		return monitorConfig.getAttribute(key);
	}
	
	public Map<String, String> getAttributes() {
		return monitorConfig.getAttributes();
	}
	
	public MonitorConfig getConfig() {
		return this.monitorConfig;
	}
}
