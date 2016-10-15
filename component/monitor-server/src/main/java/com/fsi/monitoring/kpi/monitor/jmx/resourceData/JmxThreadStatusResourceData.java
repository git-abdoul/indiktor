package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxThreadStatusResourceData extends IkrResourceData {
	
	private Map<String, Double> threadStats;
	private String processName;

	public JmxThreadStatusResourceData(Map<String, Double> threadStats, String processName, Date captureTime) {
		super(captureTime);
		this.threadStats = threadStats;
		this.processName = processName;
	}
	
	public Map<String, String> getCount() {
		Map<String, String> values = new HashMap<String, String>();
		Map<String, Double> threadTypeStats = new HashMap<String, Double>();
		double global = 0;
		if (threadStats!=null) {
			for (String key : threadStats.keySet()) {
				String[] keyElts = key.split(",");
				if (keyElts.length == 2) {
					String type = keyElts[0];
					Double val = threadTypeStats.get(type);
					if (val == null) 
						threadTypeStats.put(type, threadStats.get(key));
					else
						threadTypeStats.put(type, val+threadStats.get(key));
				}
				global = global + threadStats.get(key);
				values.put(processName+"["+key+"]", String.valueOf(threadStats.get(key)));
			}
			
			for (String type : threadTypeStats.keySet()) {
				values.put(processName+"["+type+"]", String.valueOf(threadTypeStats.get(type)));
			}
			
			values.put(processName+"[GLOBAL]", String.valueOf(global));
		}
		return values;
	}
}
