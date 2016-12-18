package com.fsi.monitoring.kpi.monitor.calypso.cache.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.calypso.tk.core.CacheLimit;
import com.calypso.tk.util.cache.CacheMetrics;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoDsCacheResourceData extends IkrResourceData {
	private static final long serialVersionUID = -329722524248133882L;

	private Hashtable<String,CacheMetrics>  cacheMetrics;
	Hashtable<String,CacheLimit> cacheLimits;

	public CalypsoDsCacheResourceData(Hashtable<String,CacheMetrics>  cacheMetrics,
									  Hashtable<String,CacheLimit> cacheLimits,
									  Date capturetime) {
		super(capturetime);
		this.cacheMetrics = cacheMetrics;
		this.cacheLimits = cacheLimits;
	}
	
	public Map<String, String> getSize() {
		Map<String, String> values = new HashMap<String, String>();
		int global = 0;
		for (String key : cacheMetrics.keySet()) {
			CacheMetrics metrics = cacheMetrics.get(key);
			global = global + metrics.getPopulation();
			values.put("Cache["+key+"]", String.valueOf(metrics.getPopulation()));
		}		
		values.put("Cache[Global]", String.valueOf(global));
		return values;
	}
	
	public Map<String, String> getLimit() {
		Map<String, String> values = new HashMap<String, String>();
		int global = 0;
		for (String key : cacheLimits.keySet()) {
			CacheLimit limit = cacheLimits.get(key);
			global = global + limit.getServerMaxSize();
			values.put("Cache["+key+"]", String.valueOf(limit.getServerMaxSize()));
		}		
		values.put("Cache[Global]", String.valueOf(global));
		return values;
	}
	
	public Map<String, String> getMemory() {
		Map<String, String> values = new HashMap<String, String>();
		double global = 0;
		for (String key : cacheMetrics.keySet()) {			
			CacheMetrics metrics = cacheMetrics.get(key);
			global = global + metrics.getPopulation()*getUnitMemory(key);
			values.put("Cache["+key+"]", String.valueOf(metrics.getPopulation()*getUnitMemory(key)));
		}		
		values.put("Cache[Global]", String.valueOf(global));
		return values;
	}
	
	public Map<String, String> getMaxMemory() {
		Map<String, String> values = new HashMap<String, String>();
		double global = 0;
		for (String key : cacheLimits.keySet()) {
			CacheLimit limit = cacheLimits.get(key);
			global = global + limit.getServerMaxSize()*getUnitMemory(key);
			values.put("Cache["+key+"]", String.valueOf(limit.getServerMaxSize()*getUnitMemory(key)));
		}		
		values.put("Cache[Global]", String.valueOf(global));
		return values;
	}
	
	private double getUnitMemory(String type) {
		if ("Curve".equalsIgnoreCase(type) || "Surface".equalsIgnoreCase(type))
			return 2000;
		else if ("Trade".equalsIgnoreCase(type))
			return 3000;
		return 1000;
	}
}
