package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostUptime;

public class SystemUptimeResourceData extends IkrResourceData {
	
	private HostUptime info = null;
	
	public SystemUptimeResourceData(HostUptime info,
			   				  		Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getUptime() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("uptime", String.valueOf(info.getUptime()));
		return values;
	}
}
