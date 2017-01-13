package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseProcedureCacheResult;

public class SybaseProcedureCacheIkrInstanceData extends IkrInstanceData {
	private SybaseProcedureCacheResult info;
	
	public SybaseProcedureCacheIkrInstanceData(String dbInstance, SybaseProcedureCacheResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
//	public String getMemory() {
//		return String.valueOf(info.getMemories());
//	}
	
	public String getRequests() {
		return String.valueOf(info.getRequests());
	}
	
	public String getLoads() {
		return String.valueOf(info.getLoads());
	}
	
	public String getWrites() {
		return String.valueOf(info.getWrites());
	}
	
	public String getStalls() {
		return String.valueOf(info.getStalls());
	}
}
