package com.fsi.monitoring.kpi.monitor.sqlQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.sqlQuery.resourceData.GenericSQLQueryResourceData;

public class GenericSQLQueryCollector extends MonitorTask {
	private static final Logger logger = Logger.getLogger(GenericSQLQueryCollector.class);
	
	private String queryToExecute = "";
	
	@Override
	protected void initConnection() throws Exception {
		queryToExecute = "select " + getAttribute(MonitorConfigAttributeKey.SQL_SELECT) + " " +
						 "from " + getAttribute(MonitorConfigAttributeKey.SQL_FROM) + " ";
		
		String whereClause = getAttribute(MonitorConfigAttributeKey.SQL_WHERE);
		if (whereClause!=null&&whereClause.length()>0)
			queryToExecute = queryToExecute + "where " + whereClause + " ";
		
		String groupByClause = getAttribute(MonitorConfigAttributeKey.SQL_GROUP_BY);
		if (groupByClause!=null&&groupByClause.length()>0)
			queryToExecute = queryToExecute + "group by " + groupByClause + " ";
		
		String orderByClause = getAttribute(MonitorConfigAttributeKey.SQL_ORDER_BY);
		if (orderByClause!=null&&orderByClause.length()>0)
			queryToExecute = queryToExecute + "order by " + orderByClause + " ";
	}

	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {}
	
	public GenericSQLQueryResourceData fetchSQL_QUERY() throws ConnectorException, FetchException {
		RdbmsConnector rdbmsConnector = (RdbmsConnector)getConnector(RdbmsConnectorConfig.TYPE);
		Map<String, Map<String, Object>> values = new HashMap<String, Map<String,Object>>();
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        MetricDomainConfigResource configResource = monitorConfig.getMetricDomainConfig().getResources().get(0);
        List<IkrCategoryResource> categoryResources = activities.get(configResource.getResource().getId());
        int selectSz = getAttribute(MonitorConfigAttributeKey.SQL_SELECT).split(",").length;
        int instanceSize = selectSz - categoryResources.size();
        try {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            rs = stmt.executeQuery(queryToExecute);
            while (rs != null && rs.next()) {
            	String instance = "IKR_INSTANCE";
            	if (instanceSize > 0) {
            		instance = instance + "[";
            		for (int i=1; i<instanceSize+1; i++) {
                		instance = instance + rs.getString(i);
                		if (i<instanceSize) 
                			instance = instance + ",";
                	}
                	instance = instance + "]";
            	}
            	else if (instanceSize < 0){
            		throw new FetchException("SQL QUERY Fetch Error");
            	}
            	
            	for (IkrCategoryResource resource : categoryResources) {
            		String resourceName = resource.getName();
            		Object val = rs.getObject(resourceName.toUpperCase());            		
            		Map<String, Object> instanceValues = values.get(resourceName);
            		if (instanceValues==null) {
            			instanceValues = new HashMap<String, Object>();
            			values.put(resourceName, instanceValues);
            		}
            		instanceValues.put(instance, String.valueOf(val));
            	}
            }
        } catch(Exception exc) {
        	logger.error(queryToExecute + " : " + exc.getMessage(), exc);
        	status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, queryToExecute + " : " + exc.getMessage()));
			notifyEventLog();
        	if (exc instanceof ConnectorException) {
        		throw (ConnectorException)exc;
        	}
        	if (exc instanceof SQLException) {
        		rdbmsConnector.reportFailure(exc);
        	} 
        	throw new FetchException(exc);
        }       
        finally {        	
        	try {
				rdbmsConnector.closeResultSet(rs);
				rdbmsConnector.closeStatement(stmt);
	        	rdbmsConnector.closeConnection(con);    
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}        	    
        }
		
		return new GenericSQLQueryResourceData(new Date(), values);
	}

	@Override
	protected void postFetchs() throws Exception {}

}
