package com.fsi.monitoring.kpi.monitor.calypso.cache.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.calypso.tk.util.cache.CacheMetric;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoDsCacheRatioResourceData extends IkrResourceData {
	
	private Map<String,CacheMetric> cacheMetrics;
	
	public CalypsoDsCacheRatioResourceData(Map<String,CacheMetric> cacheMetrics, Date captureTime) {
		super(captureTime);
		this.cacheMetrics = cacheMetrics;
	}
	
	public Map<String, String> getRequests() {
		Map<String, String> values = new HashMap<String, String>();
		for (String key : cacheMetrics.keySet()) {
			CacheMetric metric = cacheMetrics.get(key);
			values.put("Cache["+key+"]", String.valueOf(metric.getRequests()));
		}		
		return values;
	}

	public Map<String, String> getHits() {
		Map<String, String> values = new HashMap<String, String>();
		for (String key : cacheMetrics.keySet()) {
			CacheMetric metric = cacheMetrics.get(key);
			values.put("Cache["+key+"]", String.valueOf(metric.getHits()));
		}		
		return values;
	}	
}
