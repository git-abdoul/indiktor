package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRedoMeasurement.OracleRedoResult;

public class OracleRedoResourceData extends IkrResourceData {
	private List<OracleRedoResult> infos;
	
	public OracleRedoResourceData(List<OracleRedoResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getRedoLogBuffer() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRedoResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getValue()));
			}
		}
		return values;
	}
}
