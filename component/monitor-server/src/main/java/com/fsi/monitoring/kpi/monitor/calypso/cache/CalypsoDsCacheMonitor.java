package com.fsi.monitoring.kpi.monitor.calypso.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.core.CacheLimit;
import com.calypso.tk.util.cache.CacheMetric;
import com.calypso.tk.util.cache.CacheMetrics;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.cache.resourceData.CalypsoDsCacheRatioResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.cache.resourceData.CalypsoDsCacheResourceData;



public class CalypsoDsCacheMonitor extends MonitorTask {
	private static final Logger logger = Logger.getLogger(CalypsoDsCacheMonitor.class);
	
	private Hashtable<String,CacheMetrics> cacheMetrics;
	Hashtable<String,CacheLimit> cacheLimits;
	
	@Override
	protected void preStart() {}	

	@Override
	protected void preFetchs() throws Exception {
		CalypsoConnector calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);		
		cacheMetrics = calypsoConnector.getCacheMetrics();
		cacheLimits = calypsoConnector.getCacheLimits();
	}

	@Override
	protected void postFetchs() throws Exception {}
	
	public CalypsoDsCacheResourceData fetchCALYPSO_DS_CACHE()
	throws ConnectorException {		
		CalypsoDsCacheResourceData res = null;
		if (cacheMetrics == null || cacheLimits == null)
			logger.error("Impossible to find CacheMetrics from a dsConnection");		
		else 
			res = new CalypsoDsCacheResourceData(cacheMetrics, cacheLimits, new Date());		
		
		return res;
	}
	
	public CalypsoDsCacheRatioResourceData fetchCALYPSO_DS_CACHE_READ()
	throws ConnectorException, FetchException {			
		CalypsoDsCacheRatioResourceData res = null;
		if (cacheMetrics == null)
			logger.error("Impossible to find CacheMetrics from a dsConnection");		
		else { 
			Map<String, CacheMetric> readMetrics = new HashMap<String, CacheMetric>();
			for (String key : cacheMetrics.keySet()) {
				CacheMetrics metrics = cacheMetrics.get(key);
				readMetrics.put(key, metrics.getReadMetric());
			}		
			res = new CalypsoDsCacheRatioResourceData(readMetrics, new Date());	
		}
		
		return res;
	}
	
	public CalypsoDsCacheRatioResourceData fetchCALYPSO_DS_CACHE_REMOVE()
	throws ConnectorException, FetchException {	
		CalypsoDsCacheRatioResourceData res = null;
		if (cacheMetrics == null)
			logger.error("Impossible to find CacheMetrics from a dsConnection");		
		else { 
			Map<String, CacheMetric> readMetrics = new HashMap<String, CacheMetric>();
			for (String key : cacheMetrics.keySet()) {
				CacheMetrics metrics = cacheMetrics.get(key);
				readMetrics.put(key, metrics.getRemoveMetric());
			}		
			res = new CalypsoDsCacheRatioResourceData(readMetrics, new Date());	
		}
		
		return res;
	}
	
	public CalypsoDsCacheRatioResourceData fetchCALYPSO_DS_CACHE_WRITE()
	throws ConnectorException, FetchException {		
		CalypsoDsCacheRatioResourceData res = null;
		if (cacheMetrics == null)
			logger.error("Impossible to find CacheMetrics from a dsConnection");		
		else { 
			Map<String, CacheMetric> readMetrics = new HashMap<String, CacheMetric>();
			for (String key : cacheMetrics.keySet()) {
				CacheMetrics metrics = cacheMetrics.get(key);
				readMetrics.put(key, metrics.getWriteMetric());
			}		
			res = new CalypsoDsCacheRatioResourceData(readMetrics, new Date());	
		}
		
		return res;
	}
	
	
	@Override
	protected void initConnection() throws Exception {}
}
