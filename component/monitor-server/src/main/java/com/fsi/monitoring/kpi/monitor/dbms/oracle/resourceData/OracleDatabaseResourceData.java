package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleDatabaseMeasurement.OracleDatabaseResult;

public class OracleDatabaseResourceData extends IkrResourceData {
	private OracleDatabaseResult info;
	
	public OracleDatabaseResourceData(OracleDatabaseResult info, Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getCreationDate() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put("info",String.valueOf(info.getCreationDate()));
		}
		return values;
	}
	
	public Map<String, String> getLogMode() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put("info",String.valueOf(info.getLogMode()));
		}
		return values;
	}
	
	public Map<String, String> getOpenMode() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put("info",String.valueOf(info.getOpenMode()));
		}
		return values;
	}
	
	public Map<String, String> getCurrentConnections() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put("info",String.valueOf(info.getCurrentConnections()));
		}
		return values;
	}
}
