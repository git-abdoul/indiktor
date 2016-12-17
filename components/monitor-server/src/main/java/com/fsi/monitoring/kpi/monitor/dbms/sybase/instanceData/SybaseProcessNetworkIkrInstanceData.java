package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessNetworkResult;

public class SybaseProcessNetworkIkrInstanceData extends IkrInstanceData {
	private SybaseProcessNetworkResult info = null;
	
	public SybaseProcessNetworkIkrInstanceData(String dbInstance, SybaseProcessNetworkResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getNetPacketsSize() {
		return String.valueOf(info.getPacketsSize());
	}
	
	public String getNetPacketsSent() {
		return String.valueOf(info.getPacketsSent());
	}
	
	public String getNetPacketsReceived() {
		return String.valueOf(info.getPacketsReceived());
	}
	
	public String getNetBytesSent() {
		return String.valueOf(info.getBytesSent());
	}
	
	public String getNetBytesReceived() {
		return String.valueOf(info.getBytesReceived());
	}
}
