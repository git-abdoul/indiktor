package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;

public class SybaseLogsSeverityIkrInstanceData extends IkrInstanceData {
	private int info = 0;
	
	public SybaseLogsSeverityIkrInstanceData(String instance, int info, Date captureTime) {
		super(instance,captureTime);
		this.info = info;
	}
	
	public String getLogSeverity() {
		return String.valueOf(info);
	}
}
