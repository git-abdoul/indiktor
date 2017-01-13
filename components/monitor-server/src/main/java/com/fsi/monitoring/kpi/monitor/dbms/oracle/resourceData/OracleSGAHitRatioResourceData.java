package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement.OracleHitRatioResult;

public class OracleSGAHitRatioResourceData extends IkrResourceData {
	private OracleHitRatioResult info;
	
	public OracleSGAHitRatioResourceData(OracleHitRatioResult info, Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getBufferCacheRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getBufferCache()));
		}
		return values;
	}
	
	public Map<String, String> getDictionaryRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getDataDictionary()));
		}
		return values;
	}
	
	public Map<String, String> getLibraryIoReloadsRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getLibraryIoReloads()));
		}
		return values;
	}
	
	public Map<String, String> getLibraryLockRequestsRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getLibraryLockRequests()));
		}
		return values;
	}
	
	public Map<String, String> getLibraryPinRequestsRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getLibraryPinRequests()));
		}
		return values;
	}
	
	public Map<String, String> getLibraryReparsesRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getLibraryReparses()));
		}
		return values;
	}
}
