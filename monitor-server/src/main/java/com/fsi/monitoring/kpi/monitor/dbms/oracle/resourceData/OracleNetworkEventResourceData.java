package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement.OracleNetworkEventResult;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement.OracleVirtualCircuitResult;

public class OracleNetworkEventResourceData extends IkrResourceData {
	private List<OracleNetworkEventResult> infos;
	
	public OracleNetworkEventResourceData(List<OracleNetworkEventResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getTotalTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleNetworkEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getTotalTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getWaits() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleNetworkEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getWaits()));
			}
		}
		return values;
	}
	
	public Map<String, String> getAvgwait() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleNetworkEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getAvgwait()));
			}
		}
		return values;
	}
	
	public Map<String, String> getTimeouts() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleNetworkEventResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getTimeouts()));
			}
		}
		return values;
	}
}
