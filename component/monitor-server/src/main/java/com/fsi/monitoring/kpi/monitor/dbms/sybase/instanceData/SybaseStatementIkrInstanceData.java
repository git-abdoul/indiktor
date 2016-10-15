package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseStatementMeasurement.SybaseStatementResult;

public class SybaseStatementIkrInstanceData extends IkrInstanceData {
	private SybaseStatementResult info = null;
	
	public SybaseStatementIkrInstanceData(String dbInstance, SybaseStatementResult info, Date captureTime) {
		super(info.getName()+ "@" +dbInstance,captureTime);
		this.info = info;
	}
	
	public String getCpuTime() {
		return String.valueOf(info.getCpuTime());
	}
	
	public String getContentionWaitTime() {
		return String.valueOf(info.getContention().getWaitTime());
	}
	
	public String getMemory() {
		return String.valueOf(info.getMemory()*(1L << 10));
	}
	
	public String getIoPhysicalReads() {
		return String.valueOf(info.getIo().getPhysicalReads());
	}
	
	public String getIoLogicalReads() {
		return String.valueOf(info.getIo().getLogicalReads());
	}
	
	public String getIoPagesModified() {
		return String.valueOf(info.getIo().getPagesModified());
	}
	
	public String getNetPacketsSent() {
		return String.valueOf(info.getNetwork().getPacketsSent());
	}
	
	public String getNetPacketsReceived() {
		return String.valueOf(info.getNetwork().getPacketsReceived());
	}
	
	public String getNetPacketsSize() {
		return String.valueOf(info.getNetwork().getPacketsSize()*(1L << 10));
	}
	
	public String getStartTime() {
		return String.valueOf(info.getStartTime());
	}
	
	public String getEndTime() {
		return String.valueOf(info.getEndTime());
	}
	
	public String getDuration() {
		return String.valueOf(info.getDuration());
	}
	
	public String getPlansAltered() {
		return String.valueOf(info.getPlansAltered());
	}
}
