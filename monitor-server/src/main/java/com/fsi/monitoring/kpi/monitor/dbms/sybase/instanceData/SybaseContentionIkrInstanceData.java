package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;

public class SybaseContentionIkrInstanceData extends IkrInstanceData {
	private int info;
	
	public SybaseContentionIkrInstanceData(String dbInstance, int info, Date captureTime) {
		super("Deadlock@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getDeadlock() {
		return String.valueOf(info);
	}
}
