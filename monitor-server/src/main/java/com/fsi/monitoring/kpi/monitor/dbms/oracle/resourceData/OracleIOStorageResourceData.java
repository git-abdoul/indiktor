package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleIOMeasurement.OracleIOStorageResult;

public class OracleIOStorageResourceData extends IkrResourceData {
	private List<OracleIOStorageResult> infos;
	
	public OracleIOStorageResourceData(List<OracleIOStorageResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getAllocated() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOStorageResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getAllocated()));
			}
		}
		return values;
	}
	
	public Map<String, String> getUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOStorageResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getUsed()));
			}
		}
		return values;
	}
	
	public Map<String, String> getUnused() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOStorageResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getUnused()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPercentUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOStorageResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPercentUsed()));
			}
		}
		return values;
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleIOStorageResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
}
