package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineResult;

public class SybaseEngineIkrInstanceData extends IkrInstanceData {
	private SybaseEngineResult info = null;
	
	public SybaseEngineIkrInstanceData(String dbInstance, SybaseEngineResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getYields() {
		return String.valueOf(info.getYields());
	}
	
	public String getConnections() {
		return String.valueOf(info.getConnections());
	}
	
	public String getProcessesAffinitied() {
		return String.valueOf(info.getProcessesAffinitied());
	}
	
	public String getStatus() {
		return String.valueOf(info.getStatus());
	}
	
	public String getStartTime() {
		return String.valueOf(info.getStartTime());
	}
	
	public String getEndTime() {
		return String.valueOf(info.getEndTime());
	}
}
