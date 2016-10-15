package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseDataCacheResult;

public class SybaseDataCacheIkrInstanceData extends IkrInstanceData {
	private SybaseDataCacheResult info;
	
	public SybaseDataCacheIkrInstanceData(String dbInstance, SybaseDataCacheResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getBufferPools() {
		return String.valueOf(info.getBufferPools());
	}
	
	public String getSearches() {
		return String.valueOf(info.getSearches());
	}
	
	public String getPhysicalReads() {
		return String.valueOf(info.getPhysicalReads());
	}
	
	public String getLogicalReads() {
		return String.valueOf(info.getLogicalReads());
	}
	
	public String getPhysicalWrites() {
		return String.valueOf(info.getPhysicalWrites());
	}
	
	public String getStalls() {
		return String.valueOf(info.getStalls());
	}
	
	public String getCachePartitions() {
		return String.valueOf(info.getCachePartitions());
	}
}
