package com.fsi.monitoring.kpi.monitor.murex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.sqlQuery.QueryConfigModel;
import com.fsi.monitoring.sqlQuery.QueryItemModel;

public class MurexSQLQueryExecutor extends RdbmsConnectorDAO {
	private static final Logger log = Logger.getLogger(MurexSQLQueryExecutor.class);
	
	private QueryConfigModel queryConfig;
	private RdbmsConnector connector;
	
	public MurexSQLQueryExecutor(QueryConfigModel queryConfig, RdbmsConnector connector) {
		super(connector);		
		this.queryConfig = queryConfig;
		this.connector = connector;
	}
	
	public List<Map<String, String>> execute() throws Exception {
		List<Map<String, String>> values = new ArrayList<Map<String,String>>();
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try {
        	con = connector.getConnection();
            stmt = con.createStatement();  
            rs = stmt.executeQuery(queryConfig.getQuery());   
            log.debug("Executed Query <"+queryConfig.getQuery()+">");
            while (rs != null && rs.next()) { 
            	Map<String, String> metricValues = new HashMap<String, String>();
            	for (QueryItemModel item : queryConfig.getQueryItems()) {
            		Object obj = null;
            		try {
            			obj = rs.getObject(item.getQueryField());
            		}
            		catch (SQLException e) {
						obj = item.getQueryField();
					}
            		String val = null;
            		if (obj !=null) {
            			val = obj.toString();
            			metricValues.put(item.getItem(), val);
            		}
            	}            	
            	values.add(metricValues);
            }         
        } catch(Exception exc) {
        	if (exc instanceof ConnectorException) {
        		throw (ConnectorException)exc;
        	}
        	if (exc instanceof SQLException) {
        		log.error("Problem with the Query <"+queryConfig.getQuery()+">");
        		rdbmsConnector.reportFailure(exc);
        	} 
        	throw new FetchException(exc);
        }     
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
        
        log.debug("Query Results <"+queryConfig.getQuery()+">. Number : " + values.size());
        
        return values;
	}	
}
