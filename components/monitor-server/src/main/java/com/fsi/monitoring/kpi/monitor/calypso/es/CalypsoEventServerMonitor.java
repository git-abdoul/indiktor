package com.fsi.monitoring.kpi.monitor.calypso.es;

import java.util.Date;

import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.es.resourceData.CalypsoEventServerPoolResourceData;

public class CalypsoEventServerMonitor 
extends MonitorTask {
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {}
	
	@Override
	protected void postFetchs() throws Exception {}

	public CalypsoEventServerPoolResourceData fetchCALYPSO_EVENTSERVER_POOL()
	throws ConnectorException, FetchException {
		
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		long[] stats = calypsoConnector.getEventServerStats();
		if (stats==null)
			return null;
		long[] values = new long[4];
		values[0] = stats[0];
		values[1] = stats[2];
		values[2] = calypsoConnector.getEventListener().getEventServerMaxQueueSize();
		return new CalypsoEventServerPoolResourceData(values, new Date());
	}	

	@Override
	protected void initConnection() throws Exception {}		
}
