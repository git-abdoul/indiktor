package com.fsi.monitoring.kpi.monitor.murex.session;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSystemAgentMonitor;
import com.fsi.monitoring.kpi.monitor.murex.session.resourceData.MurexSessionResourceData;

public class MurexSessionMonitor extends MurexSystemAgentMonitor {
	private static final long serialVersionUID = 3546042629826272964L;
	
	@Override
	protected void subscribeToInfo() {
		systemAgentConnector.register("SESSION", this);
	}	
	
	public MurexSessionResourceData fetchMUREX_SESSION()
	throws ConnectorException {	
		return new MurexSessionResourceData(infos, new Date());
	}	
}
