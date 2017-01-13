package com.fsi.monitoring.kpi.monitor.murex.stp;

public class MurexDatamartMonitor extends MurexBatchMonitor {
	
	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexDatamartQuery.xml";
	}	
	
}
