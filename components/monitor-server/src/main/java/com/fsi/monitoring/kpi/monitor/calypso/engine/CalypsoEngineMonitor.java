package com.fsi.monitoring.kpi.monitor.calypso.engine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventMonitor;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.engine.resourceData.CalypsoEngineCacheResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.engine.resourceData.CalypsoEngineEventPoolResourceData;


public class CalypsoEngineMonitor extends MonitorTask implements CalypsoListener {
	private Map<String, PSEventMonitor> stats;	
	private List<String> engines;
	
	
	public void onEventReceived(PSEvent event) {
		if (event instanceof PSEventMonitor) {
			PSEventMonitor psEventMonitor = (PSEventMonitor) event;
			stats.put(psEventMonitor.getSource(), psEventMonitor);
		}		
	}	
	
	@Override
	protected void preStart() {
		stats = new HashMap<String, PSEventMonitor>();
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		List<String> events = new ArrayList<String>();
		events.add(PSEventMonitor.class.getName());
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}
	
	@Override
	protected void preFetchs() throws Exception {		
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		engines = calypsoConnector.getEventListener().getActiveEngines();
		for(String engineName : engines) {
//			calypsoConnector.getEventListener().sendEngineCacheStatsRequest(engineName);
		}
	}

	@Override
	protected void postFetchs() throws Exception {}

	public CalypsoEngineEventPoolResourceData fetchCALYPSO_ENGINE_EVENTPOOL() 
	throws ConnectorException {
//		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
//		Map<String, Integer> queueLimits = new HashMap<String, Integer>();
//		for(String engineName : engines) {
//			String value = calypsoConnector.getEngineParam(engineName, Engine.MAX_QUEUE_SIZE);
//			if (value != null) {
//				queueLimits.put(engineName, Integer.parseInt(value));
//			}
//			else {
//				queueLimits.put(engineName, 0);
//			}
//		}
//		return new CalypsoEngineEventPoolResourceData(stats, queueLimits, new Date());
		return null;
	}
	
	public CalypsoEngineCacheResourceData fetchCALYPSO_ENGINE_CACHE() 
	throws ConnectorException, FetchException {
//		return new CalypsoEngineCacheResourceData(stats, new Date());
		return null;
	}		
	
	@Override
	protected void initConnection() throws Exception {}	
}
