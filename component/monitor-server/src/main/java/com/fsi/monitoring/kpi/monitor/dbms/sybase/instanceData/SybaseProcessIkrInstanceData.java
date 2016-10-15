package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessResult;

public class SybaseProcessIkrInstanceData extends IkrInstanceData {
	private SybaseProcessResult info = null;
	
	public SybaseProcessIkrInstanceData(String dbInstance, SybaseProcessResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getCpuTime() {
		return String.valueOf(info.getCpuTime());
	}
	
	public String getIndexAccesses() {
		return String.valueOf(info.getIndexAccesses());
	}
	
	public String getMemory() {
		return String.valueOf(info.getMemory()*(1L << 10));
	}
	
	public String getTableAccesses() {
		return String.valueOf(info.getTableAccesses());
	}
}
