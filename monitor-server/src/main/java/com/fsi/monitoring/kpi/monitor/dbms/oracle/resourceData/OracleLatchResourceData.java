package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLatchMeasurement.OracleLatchResult;

public class OracleLatchResourceData extends IkrResourceData {
	private List<OracleLatchResult> infos;
	
	public OracleLatchResourceData(List<OracleLatchResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getGets() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getGets()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMisses() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMisses()));
			}
		}
		return values;
	}
	
	public Map<String, String> getImmediateMisses() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getImmediateMisses()));
			}
		}
		return values;
	}
	
	public Map<String, String> getImmediateGets() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getImmediateGets()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMissRate() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMissRate()));
			}
		}
		return values;
	}
}
