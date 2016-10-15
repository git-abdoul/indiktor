package com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class DataFlowResourceData extends IkrResourceData {
	private Map<String, Long> stats;
	private Map<String, List<String>> dataAttributes;

	public DataFlowResourceData(Map<String, Long> stats, Date captureTime) {
		super(captureTime);
		this.stats = stats;
	}
	
	public Map<String, String> getSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (stats!=null) {
			for (String name : stats.keySet()) {
				values.put(name, String.valueOf(stats.get(name)));
			}
		}
		return values;
	}
	
	public Map<String, List<String>> getAttribute() {
		return dataAttributes;
	}
	
	public Map<String, String> getLiveCount() {
		return null;
	}
	
	public Map<String, String> getDeadCount() {
		return null;
	}
	
	public Map<String, String> getPurgedCount() {
		return null;
	}

	public void setDataAttributes(Map<String, List<String>> dataAttributes) {
		this.dataAttributes = dataAttributes;
	}	
}
