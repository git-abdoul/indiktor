package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleWaitEventMeasurement.OracleWaitEventResult;

public class OracleWaitEventResourceData extends IkrResourceData {
	private List<OracleWaitEventResult> infos;
	
	public OracleWaitEventResourceData(List<OracleWaitEventResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getWaits() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleWaitEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWaits()));
			}
		}
		return values;
	}
	
	public Map<String, String> getTotalTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleWaitEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getTotalTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPercentage() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleWaitEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPercentage() / 100));
			}
		}
		return values;
	}
}
