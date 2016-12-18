package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRollbackSegmentMeasurement.OracleRollbackSegmentResult;

public class OracleRollbackSegmentResourceData extends IkrResourceData {
	private List<OracleRollbackSegmentResult> infos;
	
	public OracleRollbackSegmentResourceData(List<OracleRollbackSegmentResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
	
	public Map<String, String> getCurrentSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getSize()));
			}
		}
		return values;
	}
	
	public Map<String, String> getExtents() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getExtents()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWrites() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWrites()));
			}
		}
		return values;
	}
	
	public Map<String, String> getGets() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getGets()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWaits() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWaits()));
			}
		}
		return values;
	}
	
	public Map<String, String> getHmwSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getHwmSize()));
			}
		}
		return values;
	}
	
	public Map<String, String> getShrinks() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getShrinks()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWraps() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWraps()));
			}
		}
		return values;
	}
	
	public Map<String, String> getExtends() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleRollbackSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getExtend()));
			}
		}
		return values;
	}
}
