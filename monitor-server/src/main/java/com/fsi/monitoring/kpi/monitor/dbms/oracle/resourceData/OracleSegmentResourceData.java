package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSegmentMeasurement.OracleSegmentResult;

public class OracleSegmentResourceData extends IkrResourceData {
	private List<OracleSegmentResult> infos;
	
	public OracleSegmentResourceData(List<OracleSegmentResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getSegmentSize() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getSegmentSize()));
			}
		}
		return values;
	}

	public Map<String, String> getSegmentMaxExtents() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getSegmentMaxExtents()));
			}
		}
		return values;
	}

	public Map<String, String> getSegmentSizeRatio() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSegmentResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getSegmentSizeRatio()));
			}
		}
		return values;
	}

}
