package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleProcessMeasurement.OracleProcessMemoryResult;

public class OracleProcessMemoryResourceData extends IkrResourceData {
	private List<OracleProcessMemoryResult> infos;
	
	public OracleProcessMemoryResourceData(List<OracleProcessMemoryResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getMemoryUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleProcessMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMemoryUsed()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMemoryAllocated() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleProcessMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMemoryAllocated()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMemoryFreeable() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleProcessMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMemoryFreeable()));
			}
		}
		return values;
	}
}
