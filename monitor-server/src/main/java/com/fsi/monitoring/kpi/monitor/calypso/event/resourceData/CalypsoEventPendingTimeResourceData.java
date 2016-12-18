package com.fsi.monitoring.kpi.monitor.calypso.event.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoEventPendingTimeResourceData extends IkrResourceData {
	private static final long serialVersionUID = -329722524248133882L;
	
	private Map<String, Hashtable<String, Integer>> pendingEventClasses;
	private int pendingRaisingTime;

	public CalypsoEventPendingTimeResourceData(Date captTime, Map<String, Hashtable<String, Integer>> pendingEventClasses, int pendingRaisingTime) {
		super(captTime);
		this.pendingEventClasses = pendingEventClasses;
		this.pendingRaisingTime = pendingRaisingTime;
	}
	
	public Map<String, String> getPendingTimeByEngineAndByClass() {
		Map<String, String> values = new HashMap<String, String>();
		if (pendingEventClasses!=null) {
			for (String engine : pendingEventClasses.keySet()) {
				Hashtable<String, Integer> pendings = pendingEventClasses.get(engine);
				for(String eventClass : pendings.keySet()) {
					int pendingTimeVal = pendings.get(eventClass);
					if (pendingTimeVal > pendingRaisingTime)
						values.put(engine + "[" + eventClass + "]", String.valueOf(pendingTimeVal));
				}
			}
		}
		return values;
	}
}
