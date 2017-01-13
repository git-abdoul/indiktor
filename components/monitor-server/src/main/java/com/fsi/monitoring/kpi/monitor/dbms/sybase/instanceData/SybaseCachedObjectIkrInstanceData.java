package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseCachedObjectResult;

public class SybaseCachedObjectIkrInstanceData extends IkrInstanceData {
	private SybaseCachedObjectResult info;
	
	public SybaseCachedObjectIkrInstanceData(String dbInstance, SybaseCachedObjectResult info, Date captureTime) {
		super(info.getName() + "@" + dbInstance,captureTime);
		this.info = info;
	}
	
	public String getCachedMemory() {
		return String.valueOf(info.getCachedMemory()*(1L << 10));
	}
	
	public String getObjectSize() {
		return String.valueOf(info.getSize()*(1L << 10));
	}
}
