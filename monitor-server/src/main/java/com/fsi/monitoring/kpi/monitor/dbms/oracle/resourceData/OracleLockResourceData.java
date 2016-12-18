package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLockMeasurement.OracleLockResult;

public class OracleLockResourceData extends IkrResourceData {
	private List<OracleLockResult> infos;
	
	public OracleLockResourceData(List<OracleLockResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getLockType() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLockResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getLockType()));
			}
		}
		return values;
	}
	
	public Map<String, String> getModeHeld() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLockResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getModeHeld()));
			}
		}
		return values;
	}
	
	public Map<String, String> getModeRequested() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLockResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getModeRequested()));
			}
		}
		return values;
	}
	
	public Map<String, String> getId1() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLockResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getId1()));
			}
		}
		return values;
	}
	
	public Map<String, String> getId2() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleLockResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getId2()));
			}
		}
		return values;
	}
}
