package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineCpuTimeResult;

public class SybaseEngineCpuTimeIkrInstanceData extends IkrInstanceData {
	private SybaseEngineCpuTimeResult info = null;
	
	public SybaseEngineCpuTimeIkrInstanceData(String dbInstance, SybaseEngineCpuTimeResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getCpuTimeGlobal() {
		return String.valueOf(info.getGlobal());
	}
	
	public String getCpuTimeSystem() {
		return String.valueOf(info.getSystem());
	}
	
	public String getCpuTimeUser() {
		return String.valueOf(info.getUser());
	}
	
	public String getCpuTimeIdle() {
		return String.valueOf(info.getIdle());
	}
}
