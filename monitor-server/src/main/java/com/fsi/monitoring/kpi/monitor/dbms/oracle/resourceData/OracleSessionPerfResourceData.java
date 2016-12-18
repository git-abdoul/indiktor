package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSessionMeasurement.OracleSessionPerfResult;

public class OracleSessionPerfResourceData extends IkrResourceData {
	private List<OracleSessionPerfResult> infos;
	
	public OracleSessionPerfResourceData(List<OracleSessionPerfResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getElapsedTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getElapsedTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getCpuUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getCpuUsed()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMemorySorts() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getMemorySorts()));
			}
		}
		return values;
	}
	
	public Map<String, String> getTableScans() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getTablesScans()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPhysicalReads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPhysicalReads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getLogicalReads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getLogicalReads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getCommit() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getCommit()));
			}
		}
		return values;
	}
	
	public Map<String, String> getCursor() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getCursor()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPhysicalWrites() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSessionPerfResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPhysicalWrites()));
			}
		}
		return values;
	}
}
