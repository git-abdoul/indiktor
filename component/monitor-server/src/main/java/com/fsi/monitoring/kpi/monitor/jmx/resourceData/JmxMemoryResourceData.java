package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxMemoryResourceData extends IkrResourceData {
	private Map<String, MemoryUsage> localMemoryUsages;
	
	public JmxMemoryResourceData(String processName, Map<String, MemoryUsage> memoryUsages, Date captureTime, boolean isMemoryPool) {
		super(captureTime);	
		localMemoryUsages = new HashMap<String, MemoryUsage>();
		if (!isMemoryPool) {
			this.localMemoryUsages.put(processName, memoryUsages.get(processName));
		}
		else {
			for (String poolName : memoryUsages.keySet()) {
				this.localMemoryUsages.put(processName+"["+poolName+"]", memoryUsages.get(poolName));
			}
		}
	}
	
	public Map<String, String> getInit() {
		Map<String, String> values = new HashMap<String, String>();
		for (String instance : localMemoryUsages.keySet()) {
			MemoryUsage usage = localMemoryUsages.get(instance);
			values.put(instance, String.valueOf(usage.getInit()));
		}		
		return values;
	}
	
	public Map<String, String> getUsed() {
		Map<String, String> values = new HashMap<String, String>();
		for (String instance : localMemoryUsages.keySet()) {
			MemoryUsage usage = localMemoryUsages.get(instance);
			values.put(instance, String.valueOf(usage.getUsed()));
		}		
		return values;
	}
	
	public Map<String, String> getCommitted() {
		Map<String, String> values = new HashMap<String, String>();
		for (String instance : localMemoryUsages.keySet()) {
			MemoryUsage usage = localMemoryUsages.get(instance);
			values.put(instance, String.valueOf(usage.getCommitted()));
		}		
		return values;
	}
	
	public Map<String, String> getMax() {
		Map<String, String> values = new HashMap<String, String>();
		for (String instance : localMemoryUsages.keySet()) {
			MemoryUsage usage = localMemoryUsages.get(instance);
			values.put(instance, String.valueOf(usage.getMax()));
		}		
		return values;
	}
}
