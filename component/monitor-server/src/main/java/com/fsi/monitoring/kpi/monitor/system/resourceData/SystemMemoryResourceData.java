package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostMemory;

public class SystemMemoryResourceData 
extends IkrResourceData {
	
	private long used;
	private long free;
	private long total;
	
	
	public SystemMemoryResourceData(HostMemory info,
			   				  		   Date captureTime) {
		super(captureTime);
		this.used = info.getUsed();
		this.free = info.getFree();
		this.total = info.getTotal();
	}

	public void setUsed(long used) {
		this.used = used;
	}
	
	public void setFree(long free) {
		this.free = free;
	}
	
	public void setTotal(long total) {
		this.total = total;
	}
	
	public Map<String, String> getUsed() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("memory", String.valueOf(used));
		return values;
	}
	
	public Map<String, String> getFree() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("memory", String.valueOf(free));
		return values;
	}
	
	public Map<String, String> getTotal() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("memory", String.valueOf(total));
		return values;
	}
}
