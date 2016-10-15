package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineDiskIOResult;

public class SybaseEngineDiskIOIkrInstanceData extends IkrInstanceData {
	private SybaseEngineDiskIOResult info = null;
	
	public SybaseEngineDiskIOIkrInstanceData(String dbInstance, SybaseEngineDiskIOResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getDiskChecks() {
		return String.valueOf(info.getChecks());
	}
	
	public String getDiskPolled() {
		return String.valueOf(info.getPolled());
	}
	
	public String getDiskCompleted() {
		return String.valueOf(info.getCompleted());
	}
}
