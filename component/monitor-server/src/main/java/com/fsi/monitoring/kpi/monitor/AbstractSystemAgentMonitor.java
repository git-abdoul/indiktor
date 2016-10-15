package com.fsi.monitoring.kpi.monitor;

import java.io.Serializable;

import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.connector.systemAgent.SystemAgentCallback;
import com.fsi.monitoring.connector.systemAgent.SystemAgentConnector;

public abstract class AbstractSystemAgentMonitor extends MonitorTask implements SystemAgentCallback, Serializable {
	private static final long serialVersionUID = -1438788401041842225L;
	
	protected SystemAgentConnector systemAgentConnector;

	@Override
	protected void initConnection() throws Exception {}

	@Override
	protected void preStart() {
		systemAgentConnector = (SystemAgentConnector)getConnector(SystemAgentConnectorConfig.TYPE);	
		subscribeToInfo();
	}
	
	protected abstract void subscribeToInfo();
	

	@Override
	protected void preFetchs() throws Exception {}

}
