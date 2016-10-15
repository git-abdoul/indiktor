package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseCachePoolResult;

public class SybaseCachePoolIkrInstanceData extends IkrInstanceData {
	private SybaseCachePoolResult info;
	
	public SybaseCachePoolIkrInstanceData(String dbInstance, SybaseCachePoolResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getIoBufferSize() {
		return String.valueOf(info.getIoBufferSize()*(1L << 10));
	}
	
	public String getPoolMemory() {
		return String.valueOf(info.getMemory()*(1L << 10));
	}
	
	public String getPhysicalReads() {
		return String.valueOf(info.getPhysicalReads());
	}
	
	public String getStalls() {
		return String.valueOf(info.getStalls());
	}
	
	public String getBuffersToLRU() {
		return String.valueOf(info.getBufferToLRU());
	}
	
	public String getPagesTouched() {
		return String.valueOf(info.getPagesTouched());
	}
	
	public String getPagesRead() {
		return String.valueOf(info.getPagesRead());
	}
	
	public String getBuffersToMRU() {
		return String.valueOf(info.getBufferToMRU());
	}
}
