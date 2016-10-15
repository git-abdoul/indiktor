package com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;

public class SybaseLogsErrorsIkrInstanceData extends IkrInstanceData {
	private String info = null;
	
	public SybaseLogsErrorsIkrInstanceData(String instance, String info, Date captureTime) {
		super(instance,captureTime);
		this.info = info;
	}
	
	protected String getLogMessage() {return info;}
}
