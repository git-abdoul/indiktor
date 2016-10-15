package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessContentionResult;

public class SybaseProcessContentionIkrInstanceData extends IkrInstanceData {
	private SybaseProcessContentionResult info = null;
	
	public SybaseProcessContentionIkrInstanceData(String dbInstance, SybaseProcessContentionResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getContentionWaitTime() {
		return String.valueOf(info.getWaitTime());
	}
	
	public String getLocksHeld() {
		return String.valueOf(info.getLocksHeld());
	}
}
