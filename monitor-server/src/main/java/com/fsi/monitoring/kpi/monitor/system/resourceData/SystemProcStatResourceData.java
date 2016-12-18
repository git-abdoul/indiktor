package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostProcStat;

public class SystemProcStatResourceData extends IkrResourceData {
	
	private HostProcStat info = null;
	
	public SystemProcStatResourceData(HostProcStat info, Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getThreads() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getThreads()));
		return values;
	}
	
	public Map<String, String> getSleepings() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getSleeping()));
		return values;
	}
	
	public Map<String, String> getStopped() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getStopped()));
		return values;
	}
	
	public Map<String, String> getZombie() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getZombie()));
		return values;
	}
	
	public Map<String, String> getRunning() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getRunning()));
		return values;
	}
	
	public Map<String, String> getIdle() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getIdle()));
		return values;
	}
	
	public Map<String, String> getTotal() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("procstat", String.valueOf(info.getTotal()));
		return values;
	}
}
