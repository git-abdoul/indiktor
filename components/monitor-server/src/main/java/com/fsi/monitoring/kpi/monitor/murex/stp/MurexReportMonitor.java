package com.fsi.monitoring.kpi.monitor.murex.stp;

public class MurexReportMonitor extends MurexBatchMonitor {
	
	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexReportQuery.xml";
	}	
	
}
