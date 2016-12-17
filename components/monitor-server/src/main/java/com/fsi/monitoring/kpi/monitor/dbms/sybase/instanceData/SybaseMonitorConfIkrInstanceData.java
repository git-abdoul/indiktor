package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseMonitorConfMeasurement.SybaseMonitorConfResult;

public class SybaseMonitorConfIkrInstanceData extends IkrInstanceData {
	private SybaseMonitorConfResult info = null;
	
	public SybaseMonitorConfIkrInstanceData(String dbInstance, SybaseMonitorConfResult info, Date captureTime) {
		super(info.getName()+ "@" +dbInstance,captureTime);
		this.info = info;
	}
	
	public String getNumActive() {
		return String.valueOf(info.getNumActive());
	}
	
	public String getMaxUsed() {
		return String.valueOf(info.getMaxUsed());
	}
	
	public String getNumFree() {
		return String.valueOf(info.getNumFree());
	}
	
	public String getReuseCnt() {
		return String.valueOf(info.getReuseCnt());
	}
	
	public String getUtilizationRatio() {
		return String.valueOf(info.getPctAct());
	}
}
