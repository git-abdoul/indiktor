package com.fsi.monitoring.kpi.monitor.murex.stp;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryBusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.murex.stp.resourceData.MurexPaymentTaskActivityResourceData;

public class MurexPaymentTaskActivityMonitor extends
		MurexSQLQueryBusinessMonitorTask {

	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexPaymentTaskActivityQuery.xml";
	}
	
	public MurexPaymentTaskActivityResourceData fetchMUREX_PAYMENT_TASKS()
	throws ConnectorException {		
		return new MurexPaymentTaskActivityResourceData(queryValues, new Date());
	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String flowName() {
		// TODO Auto-generated method stub
		return "PAYMENT_TASK";
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
