package com.fsi.monitoring.kpi.monitor.murex.stp;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryBusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.murex.stp.resourceData.MurexTradeTaskActivityResourceData;

public class MurexTradeTaskActivityMonitor extends
		MurexSQLQueryBusinessMonitorTask {

	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexTradeTaskActivityQuery.xml";
	}
	
	public MurexTradeTaskActivityResourceData fetchMUREX_TRADE_TASKS()
	throws ConnectorException {		
		return new MurexTradeTaskActivityResourceData(queryValues, new Date());
	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String flowName() {
		// TODO Auto-generated method stub
		return "TRADE_TASK";
	}

	@Override
	protected void postFetchs() throws Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected Date getMonitoredEnvCurrentTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
