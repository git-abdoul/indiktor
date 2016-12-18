package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStatementMeasurement.OracleSQLStatsResult;

public class OracleSQLStatsResourceData extends IkrInstanceData {
	private OracleSQLStatsResult info;
	
	public OracleSQLStatsResourceData(OracleSQLStatsResult info, Date captureTime) {
		super(info.getName(),captureTime);
		this.info = info;
	}
	
	public String getSorts() {
		return String.valueOf(info.getSorts());
	}
	
	public String getFetches() {
		return String.valueOf(info.getFetches());
	}
	
	public String getExecutions() {
		return String.valueOf(info.getExecutions());
	}
	
	public String getLoads() {
		return String.valueOf(info.getLoads());
	}
	
	public String getParseCalls() {
		return String.valueOf(info.getParseCalls());
	}
	
	public String getDiskReads() {
		return String.valueOf(info.getDiskReads());
	}
	
	public String getBufferGets() {
		return String.valueOf(info.getBufferGets());
	}
	
	public String getRowsProcessed() {
		return String.valueOf(info.getRowsProcessed());
	}
	
	public String getInvalidations() {
		return String.valueOf(info.getInvalidations());
	}
	
	public String getCpuTime() {
		return String.valueOf(info.getCpuTime());
	}
	
	public String getElapsedTime() {
		return String.valueOf(info.getElapsedTime());
	}
}
