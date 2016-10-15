package com.fsi.monitoring.kpi.monitor.calypso.es.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoEventServerPoolResourceData extends IkrResourceData {
	private long[] pools;
	
	public CalypsoEventServerPoolResourceData(long[] pools, Date captureTime) {
		super(captureTime);
		this.pools = pools;
	}
	
	public Map<String, String> getCurrentPool() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("eventserver", (pools!=null)?String.valueOf(pools[0]):null);
		return values;
	}
	
	public Map<String, String> getPoolLimit() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("eventserver", (pools!=null)?String.valueOf(pools[2]):null);
		return values;
	}
	
	public Map<String, String> getMaxPoolReached() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("eventserver", (pools!=null)?String.valueOf(pools[1]):null);
		return values;
	}
}