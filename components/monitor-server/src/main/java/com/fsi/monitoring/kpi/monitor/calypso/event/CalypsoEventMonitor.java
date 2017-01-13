package com.fsi.monitoring.kpi.monitor.calypso.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.calypso.tk.event.EventStats;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.event.resourceData.CalypsoEventPendingTimeResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.event.resourceData.CalypsoEventResourceData;

public class CalypsoEventMonitor
extends MonitorTask {
	private static final Logger LOG = Logger.getLogger(CalypsoEventMonitor.class);
	
	private ScheduledFuture<?> schedulerHandler;	
	
	private int pendingTimeFetchingDelay = 0;
	private int pendingRaisingTime = 0;
	
	private Map<String,PendingEventStat> eventPendingTimes;
	private Map<String, Hashtable<String, Integer>> pendingEventClassesByEngine;
	
	@Override
	protected void preStart() {
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		
		final Runnable eventPendingTimeFetcher = new Runnable() {			
			public void run() {
				try {
					fetchEventPendingTime();
				} catch (ConnectorException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		};
		
		schedulerHandler = scheduler.scheduleAtFixedRate(eventPendingTimeFetcher, 0, pendingTimeFetchingDelay, TimeUnit.SECONDS);
	}
	
	@Override
	protected void preFetchs() throws Exception {}	
	
	@Override
	protected void postFetchs() throws Exception {}

	public CalypsoEventResourceData fetchCALYPSO_PSEVENT() 
	throws ConnectorException, FetchException {		
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		EventStats eventStats = calypsoConnector.getEventStats();
		Map<String, Hashtable<String, Integer>> pendingEventClasses = new HashMap<String, Hashtable<String,Integer>>();
		Vector<String> engineNames = eventStats.getEngineNames();		
		for (String engineName : engineNames) {
			try {
				pendingEventClasses.put(engineName, calypsoConnector.getNumberOfPendingEventProcessing(engineName));
			} catch (Exception exception) {
				throw new FetchException(exception);	
			}
		}
		return new CalypsoEventResourceData(new Date(), eventStats, pendingEventClasses);
	}	
	
	public CalypsoEventPendingTimeResourceData fetchCALYPSO_PSEVENT_PT() 
	throws ConnectorException, FetchException {
		Map<String, Hashtable<String, Integer>> pendingEventClasses = new HashMap<String, Hashtable<String,Integer>>();
		synchronized (pendingEventClassesByEngine) {
			pendingEventClasses.putAll(pendingEventClassesByEngine);
		}				
		return new CalypsoEventPendingTimeResourceData(new Date(), pendingEventClasses, pendingRaisingTime);
	}	
	
	private void fetchEventPendingTime() throws ConnectorException {
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		List<CalypsoEventObject> res =  calypsoConnector.getPendingEventProcessing();
		
		Date now = new Date();
		
		Map<String,PendingEventStat> temp = new HashMap<String, PendingEventStat>();
		for(CalypsoEventObject obj : res) {
			PendingEventStat stat = eventPendingTimes.get(obj.getKey());
			if (stat == null)
				stat = new PendingEventStat(now.getTime(), 0);
			else {
				long length = now.getTime() - stat.getStart();
				stat.setLength(length/1000);
			}
			temp.put(obj.getKey(), stat);
		}
		
		eventPendingTimes = temp;		
		
		synchronized (pendingEventClassesByEngine) {
			pendingEventClassesByEngine.clear();
			for (String key : eventPendingTimes.keySet()) {
				String[] param = key.split("_");
				Hashtable<String,Integer> map = pendingEventClassesByEngine.get(param[0]);
				int current = (int)eventPendingTimes.get(key).getLength();
				if (map == null) {
					map = new Hashtable<String, Integer>();
					pendingEventClassesByEngine.put(param[0], map);
				}
				else {
					Integer old = map.get(param[1]);
					if (old != null && old > current)
						current = old;
				}
				
				map.put(param[1], current);
			}		
		}		
	}
	
	public void init() throws Exception {
		super.init();	
		
		eventPendingTimes = new HashMap<String, PendingEventStat>();
		pendingEventClassesByEngine = new HashMap<String, Hashtable<String,Integer>>();
		
		String pendingTimeFetchingDelayStr = monitorConfig.getAttributes().get("PENDING_TIME_FETCHING_DELAY");
		if (pendingTimeFetchingDelayStr!=null&&pendingTimeFetchingDelayStr.length()>0){
			pendingTimeFetchingDelay = Integer.parseInt(pendingTimeFetchingDelayStr)*60;
		}
		
		if (pendingTimeFetchingDelay == 0)
			pendingTimeFetchingDelay = 600;
		
		String pendingRaisingTimeStr = monitorConfig.getAttributes().get("PENDING_RAISING_TIME");
		if (pendingRaisingTimeStr!=null&&pendingRaisingTimeStr.length()>0){
			pendingRaisingTime = Integer.parseInt(pendingRaisingTimeStr)*60;
		}
		
		if (pendingRaisingTime == 0)
			pendingRaisingTime = 1800;
	}

	@Override
	protected void initConnection() throws Exception {}	
	
	
	private class PendingEventStat {
		private long start;
		private long length;
		
		public PendingEventStat(long start, long length) {
			super();
			this.start = start;
			this.length = length;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public long getStart() {
			return start;
		}		
	}
}
