package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStorageMeasurement.OracleStorageResult;

public class OracleStorageResourceData extends IkrResourceData {
	private List<OracleStorageResult> infos;
	
	public OracleStorageResourceData(List<OracleStorageResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public  Map<String, String> getFree() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleStorageResult info : infos) {
				values.put("storage",String.valueOf(info.getFree()*(1L << 20)));
			}
		}
		return values;
	}
	
	public  Map<String, String> getUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleStorageResult info : infos) {
				values.put("storage",String.valueOf(info.getUsed()*(1L << 20)));
			}
		}
		return values;
	}
	
	public  Map<String, String> getSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleStorageResult info : infos) {
				values.put("storage",String.valueOf(info.getTotal()*(1L << 20)));
			}
		}
		return values;
	}
	
	public  Map<String, String> getPercentFree() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleStorageResult info : infos) {
				values.put("storage",String.valueOf(info.getPercentFree()));
			}
		}
		return values;
	}
	
	public  Map<String, String> getPercentUsed() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleStorageResult info : infos) {
				values.put("storage",String.valueOf(info.getPercentUsed()));
			}
		}
		return values;
	}
}
