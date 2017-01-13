package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRedoMeasurement.OracleRedoResult;

public class OracleRedoIkrInstanceData extends IkrInstanceData {
	private OracleRedoResult info;
	
	public OracleRedoIkrInstanceData(OracleRedoResult info, String dbInstance, Date captureTime) {
		super(info.getName()+"@"+dbInstance,captureTime);
		this.info = info;
	}
	
	public String getRedoLogBuffer() {
		return String.valueOf(info.getValue());
	}
}
