package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybasePartitionMeasurement.SybasePartitionResult;

public class SybasePartitionIkrInstanceData extends IkrInstanceData {
	private SybasePartitionResult info = null;
	
	public SybasePartitionIkrInstanceData(String dbInstance, SybasePartitionResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getLogicalReads() {
		return String.valueOf(info.getLogicalReads());
	}
	
	public String getPhysicalReads() {
		return String.valueOf(info.getPhysicalReads());
	}
	
	public String getApfreads() {
		return String.valueOf(info.getApfReads());
	}
	
	public String getPagesRead() {
		return String.valueOf(info.getPagesRead());
	}
	
	public String getPhysicalWrites() {
		return String.valueOf(info.getPhysicalWrites());
	}
	
	public String getPagesWritten() {
		return String.valueOf(info.getPagesWritten());
	}
	
	public String getRowsInserted() {
		return String.valueOf(info.getRowsInserted());
	}
	
	public String getRowsDeleted() {
		return String.valueOf(info.getRowsDeleted());
	}
	
	public String getRowsUpdated() {
		return String.valueOf(info.getRowsUpdated());
	}
}
