package com.fsi.monitoring.kpi.monitor.murex.stp;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryBusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.murex.stp.resourceData.MurexBatchReportResourceData;

public class MurexBatchMonitor extends MurexSQLQueryBusinessMonitorTask {

	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexBatchQuery.xml";
	}	
	
	public MurexBatchReportResourceData fetchBATCH()
	throws ConnectorException {		
		return new MurexBatchReportResourceData(queryValues, new Date());
	}

	@Override
	protected void postFetchs() throws Exception {
		// NOTHING TO DO		
	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		// NOTHING TO DO		
	}	

	@Override
	protected String flowName() {
		return "BATCH";
	}

	@Override
	protected Date getMonitoredEnvCurrentTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
