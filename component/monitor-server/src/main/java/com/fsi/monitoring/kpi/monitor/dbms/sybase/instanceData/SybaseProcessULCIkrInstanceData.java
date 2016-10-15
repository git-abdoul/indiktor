package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessULCResult;

public class SybaseProcessULCIkrInstanceData extends IkrInstanceData {
	private SybaseProcessULCResult info = null;
	
	public SybaseProcessULCIkrInstanceData(String dbInstance, SybaseProcessULCResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getUlcBytesWritten() {
		return String.valueOf(info.getBytesWritten());
	}
	
	public String getUlcFlush() {
		return String.valueOf(info.getFlush());
	}
	
	public String getUlcFlushFull() {
		return String.valueOf(info.getFlushFull());
	}
	
	public String getUlcUsage() {
		return String.valueOf(info.getUsage()*(1L << 10));
	}
}
