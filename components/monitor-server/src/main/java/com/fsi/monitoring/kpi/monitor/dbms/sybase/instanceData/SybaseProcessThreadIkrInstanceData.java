package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessThreadResult;

public class SybaseProcessThreadIkrInstanceData extends IkrInstanceData {
	private SybaseProcessThreadResult info = null;
	
	public SybaseProcessThreadIkrInstanceData(String dbInstance, SybaseProcessThreadResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getThreadActiveCount() {
		return String.valueOf(info.getActiveCount());
	}
	
	public String getThreadParallelQueries() {
		return String.valueOf(info.getParallelQueries());
	}
	
	public String getThreadPlansAltered() {
		return String.valueOf(info.getPlansAltered());
	}
}
