package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement.OracleVirtualCircuitResult;

public class OracleVirtualCircuitResourceData extends IkrResourceData {
	private List<OracleVirtualCircuitResult> infos;
	
	public OracleVirtualCircuitResourceData(List<OracleVirtualCircuitResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleVirtualCircuitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
	
	public Map<String, String> getQueue() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleVirtualCircuitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getQueue()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleVirtualCircuitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBytes()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBreak() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleVirtualCircuitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBreaks()));
			}
		}
		return values;
	}
}
