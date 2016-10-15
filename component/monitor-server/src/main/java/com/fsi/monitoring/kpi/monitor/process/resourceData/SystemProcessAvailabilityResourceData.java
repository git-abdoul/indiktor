package com.fsi.monitoring.kpi.monitor.process.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class SystemProcessAvailabilityResourceData extends IkrResourceData {
	private Map<String, Integer> infos;

	public SystemProcessAvailabilityResourceData(Map<String, Integer> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getUp() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				int up = infos.get(name);
				values.put(name,String.valueOf((up==1)?true:false));
			}
		}
		return values;
	}

}
