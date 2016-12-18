package com.fsi.monitoring.kpi.monitor.calypso.engine.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.calypso.tk.event.PSEventMonitor;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoEngineCacheResourceData extends IkrResourceData {
	private Map<String, PSEventMonitor> stats;
	
	public CalypsoEngineCacheResourceData(Map<String, PSEventMonitor> stats, Date captureTime) {
		super(captureTime);
		this.stats = stats;
	}
	
	public Map<String, String> getSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (stats!=null) {
			for (String name : stats.keySet()) {
				PSEventMonitor infoMonitor = stats.get(name);
				Map<String, Integer> caches = infoMonitor.getCacheStats();
				if (caches!=null && caches.size()>0) {
					for (String cacheName : caches.keySet()) {
						values.put(name+"["+cacheName+"]", String.valueOf(caches.get(cacheName)));
					}
				}
			}
		}
		return values;
	}
	
	public Map<String, String> getMemory() {
		Map<String, String> values = new HashMap<String, String>();
		if (stats!=null) {
			for (String name : stats.keySet()) {
				PSEventMonitor infoMonitor = stats.get(name);
				Map<String, Integer> caches = infoMonitor.getCacheStats();
				if (caches!=null && caches.size()>0) {
					for (String cacheName : caches.keySet()) {
						values.put(name+"["+cacheName+"]", String.valueOf(caches.get(cacheName)*getUnitMemory(cacheName)));
					}
				}
			}
		}
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
