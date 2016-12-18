package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineHkGcResult;

public class SybaseEngineHkGcIkrInstanceData extends IkrInstanceData {
	private SybaseEngineHkGcResult info = null;
	
	public SybaseEngineHkGcIkrInstanceData(String dbInstance, SybaseEngineHkGcResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getHkgcMaxQsize() {
		return String.valueOf(info.getMaxQsize());
	}
	
	public String getHkgcPendingItems() {
		return String.valueOf(info.getPendingItems());
	}
	
	public String getHkgcItems() {
		return String.valueOf(info.getItems());
	}
	
	public String getHkgcOverflows() {
		return String.valueOf(info.getOverflows());
	}
}
