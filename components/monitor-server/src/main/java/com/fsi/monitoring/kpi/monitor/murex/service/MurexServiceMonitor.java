package com.fsi.monitoring.kpi.monitor.murex.service;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSystemAgentMonitor;
import com.fsi.monitoring.kpi.monitor.murex.service.resourceData.MurexServiceResourceData;

public class MurexServiceMonitor extends MurexSystemAgentMonitor {
	private static final long serialVersionUID = 5610265028185954732L;
	
	@Override
	protected void subscribeToInfo() {
		systemAgentConnector.register("SERVICE", this);
	}	
	
	public MurexServiceResourceData fetchMUREX_SERVICE()
	throws ConnectorException {	
		return new MurexServiceResourceData(infos, new Date());
	}	
}
