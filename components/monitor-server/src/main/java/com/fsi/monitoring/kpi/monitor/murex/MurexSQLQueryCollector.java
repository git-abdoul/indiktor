package com.fsi.monitoring.kpi.monitor.murex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.SystemException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.sqlQuery.QueryConfigModel;
import com.fsi.monitoring.sqlQuery.XmlSQLQueryParser;

public class MurexSQLQueryCollector {	
	private static final Logger LOG = Logger.getLogger(MurexSQLQueryCollector.class);
	
	private List<QueryConfigModel> queryConfigs;
	private List<MurexSQLQueryExecutor> queryExecutors;
	
	public void init(Map<String, RdbmsConnector> rdbmsConnectors, String queryConfigFilename) {		
		try {
			XmlSQLQueryParser parser = (new XmlSQLQueryParser()).parse(queryConfigFilename);
			queryConfigs = parser.getQueryConfigs();
		} catch (SystemException e) {
			queryConfigs = new ArrayList<QueryConfigModel>();
			LOG.error(e.getMessage(), e);
		}		
		
		queryExecutors = new ArrayList<MurexSQLQueryExecutor>();
		for (QueryConfigModel queryConfig : queryConfigs) {
			RdbmsConnector connector = rdbmsConnectors.get(queryConfig.getConnector());
			if (connector == null) {
				LOG.error("No Connector available for queryConfig : " + queryConfig.getQuery());
				continue;
			}
			
			queryExecutors.add(new MurexSQLQueryExecutor(queryConfig, connector));
		}
	}

	public List<Map<String, String>> collect() {
		List<Map<String, String>> queryValues = Collections.synchronizedList(new ArrayList<Map<String, String>>());
//		for (final SQLQueryExecutor executor : queryExecutors) {			
//			Thread worker = new Thread(new Runnable() {				
//				public void run() {
//					try {
//						Map<String, String> values = executor.execute();
//						queryValues.putAll(values);
//					}
//					catch (Exception e) {
//						LOG.error(e.getMessage(), e);
//					}					
//				}
//			});
//			worker.start();			
//		}
		for (MurexSQLQueryExecutor executor : queryExecutors) {			
			try {
				List<Map<String, String>> values = executor.execute();
				queryValues.addAll(values);
			}
			catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}					
		}
		
		return queryValues;
	}

}
