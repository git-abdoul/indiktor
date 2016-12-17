package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxProcessGCResourceData extends IkrResourceData {

	private List<GarbageCollectorMXBean> garbageCollectorMXBeans;
	private String processName;

	public JmxProcessGCResourceData(List<GarbageCollectorMXBean> garbageCollectorMXBeans,
									String processName, 
									Date captureTime){
		super(captureTime);
		this.garbageCollectorMXBeans = garbageCollectorMXBeans;
		this.processName = processName;
	}
	
	public Map<String, String> getGcFullTime() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(garbageCollectorMXBeans.get(1).getCollectionTime()));
		return values;
	}
	
	public Map<String, String> getGcFullCount() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(garbageCollectorMXBeans.get(1).getCollectionCount()));
		return values;
	}
	
	public Map<String, String> getGcNewTime() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(garbageCollectorMXBeans.get(0).getCollectionTime()));
		return values;
	}
	
	public Map<String, String> getGcNewCount() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(garbageCollectorMXBeans.get(0).getCollectionCount()));
		return values;
	}
}
