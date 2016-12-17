package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSessionMeasurement.OracleSessionWaitResult;

public class OracleSessionWaitResourceData extends IkrResourceData {
	private List<OracleSessionWaitResult> infos;
	
	public OracleSessionWaitResourceData(List<OracleSessionWaitResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionWaitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
	
	public Map<String, String> getEvent() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionWaitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getEvent()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWaitTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionWaitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWaitTime()*1000));
			}
		}
		return values;
	}
	
	public Map<String, String> getLogonTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionWaitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getLogonTime()));
			}
		}
		return values;
	}
}
