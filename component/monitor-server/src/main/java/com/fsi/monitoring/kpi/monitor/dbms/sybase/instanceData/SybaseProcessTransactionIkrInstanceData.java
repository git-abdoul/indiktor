package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessTransactionResult;

public class SybaseProcessTransactionIkrInstanceData extends IkrInstanceData {
	private SybaseProcessTransactionResult info = null;
	
	public SybaseProcessTransactionIkrInstanceData(String dbInstance, SybaseProcessTransactionResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getTransactionCount() {
		return String.valueOf(info.getCount());
	}
	
	public String getTransactionCommits() {
		return String.valueOf(info.getCommits());
	}
	
	public String getTransactionRollbacks() {
		return String.valueOf(info.getRollbacks());
	}
}
