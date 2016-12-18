package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement.OracleDispatcherResult;

public class OracleDispatcherResourceData extends IkrResourceData {
	private List<OracleDispatcherResult> infos;
	
	public OracleDispatcherResourceData(List<OracleDispatcherResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleDispatcherResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMessages() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleDispatcherResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMessages()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleDispatcherResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBytes()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBusy() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleDispatcherResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBusy()));
			}
		}
		return values;
	}
	
	public Map<String, String> getIdle() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleDispatcherResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getIdle()));
			}
		}
		return values;
	}
}
