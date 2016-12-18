package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleIOMeasurement.OracleIOResult;

public class OracleIOStatsResourceData extends IkrResourceData {
	private List<OracleIOResult> infos;
	
	public OracleIOStatsResourceData(List<OracleIOResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getPhysicalReads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPhysicalReads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPhysicalWrites() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPhysicalWrites()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBlockReads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBlockReads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getBlockWrites() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getBlockWrites()));
			}
		}
		return values;
	}
	
	public Map<String, String> getReadTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getReadTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWriteTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWriteTime()));
			}
		}
		return values;
	}
}
