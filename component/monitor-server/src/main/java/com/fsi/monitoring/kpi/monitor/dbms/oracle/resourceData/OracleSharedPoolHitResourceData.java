package com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSharedPoolMeasurement.OracleSharedPoolHitResult;

public class OracleSharedPoolHitResourceData extends IkrResourceData {
	private List<OracleSharedPoolHitResult> infos;
	
	public OracleSharedPoolHitResourceData(List<OracleSharedPoolHitResult> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getGets() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getGets()));
			}
		}
		return values;
	}
	
	public Map<String, String> getGetHits() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getGetHits()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPins() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPins()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPinHits() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getPinHits()));
			}
		}
		return values;
	}
	
	public Map<String, String> getReloads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getReloads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getInvalidations() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (OracleSharedPoolHitResult info : infos) {
				values.put(info.getName(),String.valueOf(info.getInvalidations()));
			}
		}
		return values;
	}
}
