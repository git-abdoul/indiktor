package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLatchMeasurement.OracleLatchPerfResult;

public class OracleLatchPerfResourceData extends IkrResourceData {
	private List<OracleLatchPerfResult> infos;
	
	public OracleLatchPerfResourceData(List<OracleLatchPerfResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getWaitTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWaitTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getSleep() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getSleep()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPercent() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLatchPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPercent()));
			}
		}
		return values;
	}
}
