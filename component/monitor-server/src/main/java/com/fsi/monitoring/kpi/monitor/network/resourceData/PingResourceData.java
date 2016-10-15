package com.fsi.monitoring.kpi.monitor.network.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.network.service.PingService.PingResult;

public class PingResourceData extends IkrResourceData {	
	private List<PingResult> pings;
	private String mode;
	
	public PingResourceData(List<PingResult> pings,
						String mode,
			   			Date captureTime) {
		super(captureTime);
		this.pings = pings;
		this.mode = mode;
	}
	
	public Map<String, String> getMin() {
		Map<String, String> values = new HashMap<String, String>();
		if (pings!=null) {
			for (PingResult result : pings) {
				values.put(result.getHostname()+"["+mode+"]",String.valueOf((result != null) ? result.getMin() : 0));
			}
		}
		return values;
	}
	
	public Map<String, String> getMax() {
		Map<String, String> values = new HashMap<String, String>();
		if (pings!=null) {
			for (PingResult result : pings) {
				values.put(result.getHostname()+"["+mode+"]",String.valueOf((result != null) ? result.getMax() : 0));
			}
		}
		return values;
	}
	
	public Map<String, String> getAverage() {
		Map<String, String> values = new HashMap<String, String>();
		if (pings!=null) {
			for (PingResult result : pings) {
				values.put(result.getHostname()+"["+mode+"]",String.valueOf((result != null) ? result.getAverage() : 0));
			}
		}
		return values;
	}
}
