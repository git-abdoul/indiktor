package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement.OracleSGASizeResult;

public class OracleSGASizeResourceData extends IkrResourceData {
	private OracleSGASizeResult info;
	
	public OracleSGASizeResourceData(OracleSGASizeResult info, Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getBufferCacheSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getBufferCache()));
		}
		return values;
	}
	
	public Map<String, String> getSharedPoolSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getSharedPool()));
		}
		return values;
	}
	
	public Map<String, String> getDictionarySize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getDataDictionary()));
		}
		return values;
	}
	
	public Map<String, String> getRedoLogSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getRedoLog()));
		}
		return values;
	}
	
	public Map<String, String> getLibraryCacheSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getLibraryCache()));
		}
		return values;
	}
	
	public Map<String, String> getSqlAreaSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getSqlArea()));
		}
		return values;
	}
	
	public Map<String, String> getFixedAreaSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getFixedArea()));
		}
		return values;
	}
	
	public Map<String, String> getFreeMemory() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!=null) {
			values.put(info.getName(),String.valueOf(info.getFreeMemory()));
		}
		return values;
	}
}
