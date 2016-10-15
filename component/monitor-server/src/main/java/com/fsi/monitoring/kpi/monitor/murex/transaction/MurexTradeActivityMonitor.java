package com.fsi.monitoring.kpi.monitor.murex.transaction;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryBusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;
import com.fsi.monitoring.kpi.monitor.murex.transaction.resourceData.MurexTradeActivityResourceData;

public class MurexTradeActivityMonitor extends MurexSQLQueryBusinessMonitorTask {
	private static final Logger LOG = Logger.getLogger(MurexTradeActivityMonitor.class);
	
	@Override
	protected String getQueryConfigFileName() {
		return System.getProperty("distrib") + "/component/monitor-server/sql/murexTradeActivityQuery.xml";
	}
	
	public MurexTradeActivityResourceData fetchMUREX_TRADE()
	throws ConnectorException {		
		LOG.debug("Query Size = " + queryValues.size());
		for (String instance : queryValues.keySet()) {
			LOG.debug("Instance = " + instance);
			List<MurexSQLQueryValue> values = queryValues.get(instance);
			for (MurexSQLQueryValue val : values) {
				for (String key : val.getValues().keySet()) {
					LOG.debug(key + " = " + val.getValue(key));
				}
			}
		}
		return new MurexTradeActivityResourceData(queryValues, new Date());
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
		return "TRADE";
	}
	
	@Override
	protected Date getMonitoredEnvCurrentTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void initConnection() throws Exception {}	

}
