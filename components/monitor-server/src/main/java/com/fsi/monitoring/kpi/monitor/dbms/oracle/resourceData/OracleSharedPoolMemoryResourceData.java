package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSharedPoolMeasurement.OracleSharedPoolMemoryResult;

public class OracleSharedPoolMemoryResourceData extends IkrResourceData {
	private List<OracleSharedPoolMemoryResult> infos;
	
	public OracleSharedPoolMemoryResourceData(List<OracleSharedPoolMemoryResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getPinMemory() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getInuseSize()));
			}
		}
		return values;
	}
	
	public Map<String, String> getObjectsPinnedcount() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getInuseCount()));
			}
		}
		return values;
	}
	
	public Map<String, String> getUnpinMemory() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getFreeableSize()));
			}
		}
		return values;
	}
	
	public Map<String, String> getObjectsUnpinnedCount() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolMemoryResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getFreeableCount()));
			}
		}
		return values;
	}
}
