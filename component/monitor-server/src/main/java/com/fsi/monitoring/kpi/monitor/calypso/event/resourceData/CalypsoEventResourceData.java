package com.fsi.monitoring.kpi.monitor.calypso.event.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.calypso.tk.event.EventStats;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoEventResourceData extends IkrResourceData {
	private static final long serialVersionUID = -329722524248133882L;
	
	private EventStats eventStats;
	private Map<String, Hashtable<String, Integer>> pendingEventClasses;

	public CalypsoEventResourceData(Date captTime,
								EventStats eventStats,
								Map<String, Hashtable<String, Integer>> pendingEventClasses) {
		super(captTime);
		this.eventStats = eventStats;
		this.pendingEventClasses = pendingEventClasses;
	}
	
	public Map<String, String> getGlobalConsumed() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("global", (eventStats!=null)?String.valueOf(eventStats.getTotalConsumed()):null);
		return values;
	}
	
	public Map<String, String> getGlobalProduced() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("global", (eventStats!=null)?String.valueOf(eventStats.getTotalProduced()):null);
		return values;
	}
	
	public Map<String, String> getGlobalPending() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("global", (eventStats!=null)?String.valueOf(eventStats.getCurrent()):null);
		return values;
	}
	
	public Map<String, String> getConsumedByEngine() {
		Map<String, String> values = new HashMap<String, String>();
		if (eventStats!=null) {
			Vector<String> engines = eventStats.getEngineNames();
			for (String name : engines) {
				values.put(name, String.valueOf(eventStats.getConsumed(name)));
			}
		}
		return values;
	}
	
	public Map<String, String> getProducedByEngine() {
		Map<String, String> values = new HashMap<String, String>();
		if (eventStats!=null) {
			Vector<String> engines = eventStats.getEngineNames();
			for (String name : engines) {
				values.put(name, String.valueOf(eventStats.getConsumed(name)+eventStats.getCurrent(name)));
			}
		}
		return values;
	}
	
	public Map<String, String> getPendingByEngine() {
		Map<String, String> values = new HashMap<String, String>();
		if (eventStats!=null) {
			Vector<String> engines = eventStats.getEngineNames();
			for (String name : engines) {
				values.put(name, String.valueOf(eventStats.getCurrent(name)));
			}
		}
		return values;
	}
	
	public Map<String, String> getPendingByEngineAndByClass() {
		Map<String, String> values = new HashMap<String, String>();
		if (pendingEventClasses!=null) {
			for (String engine : pendingEventClasses.keySet()) {
				Hashtable<String, Integer> pendings = pendingEventClasses.get(engine);
				for(String eventClass : pendings.keySet()) {
					values.put(engine + "[" + eventClass + "]", String.valueOf(pendings.get(eventClass)));
				}
			}
		}
		return values;
	}
}
