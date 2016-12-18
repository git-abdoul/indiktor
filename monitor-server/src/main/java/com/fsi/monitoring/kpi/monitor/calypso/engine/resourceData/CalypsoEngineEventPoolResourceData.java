package com.fsi.monitoring.kpi.monitor.calypso.engine.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.calypso.tk.event.PSEventMonitor;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoEngineEventPoolResourceData extends IkrResourceData {
	private Map<String, PSEventMonitor> stats;
	private Map<String, Integer> queueLimits;
	
	public CalypsoEngineEventPoolResourceData(Map<String, PSEventMonitor> stats, Map<String, Integer> queueLimits, Date captureTime) {
		super(captureTime);
		this.stats = stats;
		this.queueLimits = queueLimits;
	}
	
	public Map<String, String> getCurrentPool() {
		Map<String, String> values = new HashMap<String, String>();
		if (stats!=null) {
			for (String name : stats.keySet()) {
				PSEventMonitor infoMonitor = stats.get(name);
				values.put(name, String.valueOf(infoMonitor.getQueueSize()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPoolLimit() {
		Map<String, String> values = new HashMap<String, String>();
		if (queueLimits!=null) {
			for (String name : queueLimits.keySet()) {
				values.put(name, String.valueOf(queueLimits.get(name)));
			}
		}
		return values;
	}
	
	public Map<String, String> getMaxPoolReached() {
		Map<String, String> values = new HashMap<String, String>();
		if (stats!=null) {
			for (String name : stats.keySet()) {
				PSEventMonitor infoMonitor = stats.get(name);
				values.put(name, String.valueOf(infoMonitor.getMaxQueueSize()));
			}
		}
		return values;
	}
}
