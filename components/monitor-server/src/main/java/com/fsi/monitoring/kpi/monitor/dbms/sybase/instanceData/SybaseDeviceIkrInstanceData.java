package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseDeviceMeasurement.SybaseDeviceIOResult;

public class SybaseDeviceIkrInstanceData extends IkrInstanceData {
	private SybaseDeviceIOResult info = null;
	
	public SybaseDeviceIkrInstanceData(String dbInstance, SybaseDeviceIOResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getReads() {
		return String.valueOf(info.getReads());
	}
	
	public String getApfreads() {
		return String.valueOf(info.getApfreads());
	}
	
	public String getWrites() {
		return String.valueOf(info.getWrites());
	}
	
	public String getSemaphoreRequests() {
		return String.valueOf(info.getSemaphoreRequests());
	}
	
	public String getWrgetSemaphoreWaitsites() {
		return String.valueOf(info.getWrites());
	}
	
	public String getIotime() {
		return String.valueOf(info.getIotime());
	}
}
