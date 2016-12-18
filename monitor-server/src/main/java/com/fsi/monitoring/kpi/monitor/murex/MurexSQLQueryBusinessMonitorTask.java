package com.fsi.monitoring.kpi.monitor.murex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.kpi.monitor.BusinessMonitorTask;

public abstract class MurexSQLQueryBusinessMonitorTask extends BusinessMonitorTask {
	private static final Logger LOG = Logger.getLogger(MurexSQLQueryBusinessMonitorTask.class);
	
	protected Map<String, RdbmsConnector> rdbmsConnectors;
	protected Map<String, List<MurexSQLQueryValue>> queryValues;	
	protected MurexSQLQueryCollector queryCollector;

	@Override
	protected void initConnector() {
		rdbmsConnectors = new HashMap<String, RdbmsConnector>();
		for (Connector connector : getConnectors()) {
			if (connector instanceof RdbmsConnector)
				rdbmsConnectors.put(connector.getName(), (RdbmsConnector)connector);
		}
		
		queryCollector = new MurexSQLQueryCollector();
		queryCollector.init(rdbmsConnectors, getQueryConfigFileName());		
	}
	
	@Override
	protected void preFetchs() throws Exception {
		queryValues = new HashMap<String, List<MurexSQLQueryValue>>();
		
		List<Map<String, String>> collectedValues = queryCollector.collect();
		for (Map<String, String> values : collectedValues) {
			if (!acceptBusinessComponent(values)) 
				continue;
			
			String instance = getInstance(values);
			List<MurexSQLQueryValue> sqlResults = queryValues.get(instance);
			if (sqlResults == null) {
				sqlResults = new ArrayList<MurexSQLQueryValue>();
				queryValues.put(instance, sqlResults);
			}
			sqlResults.add(new MurexSQLQueryValue(values));
		}		
	}
	
	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		Map<String, String> queryValue = (Map<String, String>)businessObject;		
		return queryValue.get(componentType);
	}
	
	protected abstract String getQueryConfigFileName();

}
