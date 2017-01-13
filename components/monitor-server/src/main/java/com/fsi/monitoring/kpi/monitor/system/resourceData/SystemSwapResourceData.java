package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostSwap;

public class SystemSwapResourceData extends IkrResourceData {
	
	private HostSwap info = null;
	
	public SystemSwapResourceData(HostSwap info,
			   				  		Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getSwapPageOut() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("swap", String.valueOf(info.getPageOut()));
		return values;
	}
	
	public Map<String, String> getSwapPageIn() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("swap", String.valueOf(info.getPageIn()));
		return values;
	}
	
	public Map<String, String> getSwapUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("swap", String.valueOf(info.getUsed()));
		return values;
	}
	
	public Map<String, String> getSwapFree() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("swap", String.valueOf(info.getFree()));
		return values;
	}
	
	public Map<String, String> getTotalSwap() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("swap", String.valueOf(info.getTotal()));
		return values;
	}
}
