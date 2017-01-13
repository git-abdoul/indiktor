package com.fsi.monitoring.kpi.monitor.calypso.ds;



import java.util.Date;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.ds.resourceData.CalypsoDataServerResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.ds.resourceData.DSEventBuffer;


public class CalypsoDataServerMonitor 
extends MonitorTask {
	private static final Logger LOG = Logger.getLogger(CalypsoDataServerMonitor.class);
	
	private int[] dbConnectionCount;
	private int[] connectedClients;
	private Boolean isDsConnectedToEs;
	private DSEventBuffer eventBuffer;
	
	@Override
	protected void preStart() {}
	
	@Override
	protected void preFetchs() throws Exception {}

	@Override
	protected void postFetchs() throws Exception {}

	public CalypsoDataServerResourceData fetchCALYPSO_DATASERVER() 
	throws ConnectorException, FetchException {
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		dbConnectionCount = calypsoConnector.getDBConnectionsCount();
		connectedClients = new int[2];
		connectedClients[0] = calypsoConnector.getConnectedClients().size();
		connectedClients[1] = calypsoConnector.getVerifiedConnectedClients().size();
		isDsConnectedToEs = calypsoConnector.isDataServerConnected2EventServer();
		int limit = calypsoConnector.getEventBufferMaxSize();
		int max = calypsoConnector.getEventBufferCurrentMax();
		int current = calypsoConnector.getEventBufferSize();
		eventBuffer = new DSEventBuffer(current, max, limit);
		
		CalypsoDataServerResourceData res = new CalypsoDataServerResourceData(new Date());
		if (dbConnectionCount == null)
			LOG.error("Impossible to fetch dbConection metric from a dsConnection");		
		else {			
			res.setConnectedClients(dbConnectionCount);
		}
		
		if (dbConnectionCount == null)
			LOG.error("Impossible to fetch dbConection metric from a dsConnection");		
		else {
			res.setDbConnectionCount(dbConnectionCount);
		}
		
		if (isDsConnectedToEs == null)
			LOG.error("Impossible to fetch isDsConnectedToEs metric from a dsConnection");		
		else {
			res.setDsConnectedToEs(isDsConnectedToEs);
		}
		
		if (eventBuffer == null)
			LOG.error("Impossible to fetch eventBuffer metric from a dsConnection");		
		else {
			res.setEventBuffer(eventBuffer);
		}
		
		return res;
	}	

	@Override
	protected void initConnection() throws Exception {}	
}
