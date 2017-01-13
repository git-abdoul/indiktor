package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStatementMeasurement.OracleSQLGlobalStatsMemoryResult;

public class OracleSQLGlobalStatsMemoryResourceData extends IkrResourceData {
	private OracleSQLGlobalStatsMemoryResult info;
	
	public OracleSQLGlobalStatsMemoryResourceData(OracleSQLGlobalStatsMemoryResult info, Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getCpuParseTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getCpuParsetime()));
		}
		return values;
	}
	
	public Map<String, String> getParseElapseTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getElapsedParseTime()));
		}
		return values;
	}
	
	public Map<String, String> getTotalParseCount() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getTotalParseCount()));
		}
		return values;
	}
	
	public Map<String, String> getHardParseCount() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getHardParseCount()));
		}
		return values;
	}
	
	public Map<String, String> getFailedParseCount() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getFailedParseCount()));
		}
		return values;
	}
	
	public Map<String, String> getExecuteCount() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getExecuteCount()));
		}
		return values;
	}
}
