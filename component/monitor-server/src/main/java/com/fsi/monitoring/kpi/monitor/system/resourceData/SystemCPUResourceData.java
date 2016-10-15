package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostCpuPerc;

public class SystemCPUResourceData extends IkrResourceData {
	
	private Map<String, HostCpuPerc> infos = null;
	
	public SystemCPUResourceData(Map<String, HostCpuPerc> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getIdle() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				values.put(name, String.valueOf(infos.get(name).getIdle()));
			}
		}
		return values;
	}
	
	public Map<String, String> getUser() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				values.put(name, String.valueOf(infos.get(name).getUser()));
			}
		}
		return values;
	}
	
	public Map<String, String> getSystem() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				values.put(name, String.valueOf(infos.get(name).getSystem()));
			}
		}
		return values;
	}
	
	public Map<String, String> getIowait() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				values.put(name, String.valueOf(infos.get(name).getWait()));
			}
		}
		return values;
	}
	
	public Map<String, String> getTotal() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (String name : infos.keySet()) {
				double val = infos.get(name).getUser()+infos.get(name).getSystem();
				values.put(name, String.valueOf((val>0)?val:0));
			}
		}
		return values;
	}
}
