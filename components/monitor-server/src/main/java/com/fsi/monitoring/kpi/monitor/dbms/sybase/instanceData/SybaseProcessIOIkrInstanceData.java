package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessIOResult;

public class SybaseProcessIOIkrInstanceData extends IkrInstanceData {
	private SybaseProcessIOResult info = null;
	
	public SybaseProcessIOIkrInstanceData(String dbInstance, SybaseProcessIOResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getIoPhysicalReads() {
		return String.valueOf(info.getPhysicalReads());
	}
	
	public String getIoLogicalReads() {
		return String.valueOf(info.getLogicalReads());
	}
	
	public String getIoPagesRead() {
		return String.valueOf(info.getPagesRead());
	}
	
	public String getIoPhysicalWrites() {
		return String.valueOf(info.getPhysicalWrites());
	}
	
	public String getIoPagesWritten() {
		return String.valueOf(info.getPagesWritten());
	}
}
