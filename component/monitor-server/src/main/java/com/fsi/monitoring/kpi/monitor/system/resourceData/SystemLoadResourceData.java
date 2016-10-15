package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostProcLoads;

public class SystemLoadResourceData extends IkrResourceData {
	
	private HostProcLoads info = null;
	
	public SystemLoadResourceData(HostProcLoads info,
			   				  		 Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getLoad15Min() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("load", String.valueOf(info.getLoad15Min()));
		return values;
	}
	
	public Map<String, String> getLoad5Min() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("load", String.valueOf(info.getLoad5Min()));
		return values;
	}
	
	public Map<String, String> getLoad1Min() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("load", String.valueOf(info.getLoad1Min()));
		return values;
	}
}
