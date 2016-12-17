package com.fsi.monitoring.kpi.monitor.jmx;

import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.fsi.monitoring.connector.jmx.JmxConnector;
import com.fsi.monitoring.kpi.monitor.MonitorTask;

public abstract class JmxMonitorTask extends MonitorTask {
	protected JmxConnector jmxConnector;
	
	
	@Override
	protected void preStart() {}	
	
	@Override
	protected void preFetchs() throws Exception {
		jmxConnector = (JmxConnector)getConnector(JmxConnectorConfig.TYPE);
	}	
	
	@Override
	protected void postFetchs() throws Exception {}

	@Override
	public void initConnection() throws Exception {}
}
