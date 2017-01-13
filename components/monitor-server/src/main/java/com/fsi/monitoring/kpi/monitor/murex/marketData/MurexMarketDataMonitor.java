package com.fsi.monitoring.kpi.monitor.murex.marketData;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSystemAgentMonitor;
import com.fsi.monitoring.kpi.monitor.murex.marketData.resourceData.MurexMarketDataResourceData;

public class MurexMarketDataMonitor extends MurexSystemAgentMonitor {
	private static final long serialVersionUID = 5906747676719765304L;
	
	@Override
	protected void subscribeToInfo() {
		systemAgentConnector.register("REALTIME", this);
	}	
	
	public MurexMarketDataResourceData fetchMUREX_MD()
	throws ConnectorException {	
		return new MurexMarketDataResourceData(infos, new Date());
	}	
}
