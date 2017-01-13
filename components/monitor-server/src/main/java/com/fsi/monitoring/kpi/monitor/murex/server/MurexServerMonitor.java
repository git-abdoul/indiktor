package com.fsi.monitoring.kpi.monitor.murex.server;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSystemAgentMonitor;
import com.fsi.monitoring.kpi.monitor.murex.server.resourceData.MurexServerResourceData;

public class MurexServerMonitor extends MurexSystemAgentMonitor {
	private static final long serialVersionUID = 6468401559993931076L;
	
	@Override
	protected void subscribeToInfo() {
		systemAgentConnector.register("SERVER", this);
	}	
	
	public MurexServerResourceData fetchMUREX_SERVER()
	throws ConnectorException {	
		return new MurexServerResourceData(infos, new Date());
	}	
}
