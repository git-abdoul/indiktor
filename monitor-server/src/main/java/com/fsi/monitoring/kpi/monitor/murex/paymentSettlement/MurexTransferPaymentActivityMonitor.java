package com.fsi.monitoring.kpi.monitor.murex.paymentSettlement;

import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryBusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.murex.paymentSettlement.resourceData.MurexPaymentSettlementActivityResourceData;

public class MurexTransferPaymentActivityMonitor extends
		MurexSQLQueryBusinessMonitorTask {

	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexPaymentActivityQuery.xml";
	}
	
	public MurexPaymentSettlementActivityResourceData fetchPAYMENT()
	throws ConnectorException {		
		return new MurexPaymentSettlementActivityResourceData(queryValues, new Date());
	}
	
//	public MurexPaymentSettlementActivityResourceData fetchTRANSFER()
//	throws ConnectorException {		
//		return null;
//	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String flowName() {
		// TODO Auto-generated method stub
		return "PAYMENT";
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
