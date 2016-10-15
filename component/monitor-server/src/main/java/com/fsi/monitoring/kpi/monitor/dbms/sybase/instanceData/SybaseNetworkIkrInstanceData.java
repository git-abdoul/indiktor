package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseNetworkMeasurement.SybaseNetworkResult;

public class SybaseNetworkIkrInstanceData extends IkrInstanceData {
	private SybaseNetworkResult info = null;
	
	public SybaseNetworkIkrInstanceData(String dbInstance, SybaseNetworkResult info, Date captureTime) {
		super("NetStat@" + dbInstance,captureTime);
		this.info = info;
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
