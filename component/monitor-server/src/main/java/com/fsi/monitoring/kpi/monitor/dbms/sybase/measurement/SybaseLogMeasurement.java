package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;

public class SybaseLogMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseLogMeasurement.class);
	
	public SybaseLogMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public Map<String, Integer> getLogSeverityResult()
	throws ConnectorException, FetchException, PersistenceException {		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	Map<String, Integer> severityCounts = new HashMap<String, Integer>();
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();            
            String query = "select EngineNumber, Severity, count(*) " +
            				"from master..monErrorLog " +
            				"group by EngineNumber, Severity";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("EngineNumber") + "#" + rs.getInt("Severity");
                int count = rs.getInt(3);
                severityCounts.put(name, count); 
            }
            
            return severityCounts;            
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
	}
	
	public Map<String, String> getLogMessageResult()
	throws ConnectorException, FetchException, PersistenceException {		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	Map<String, String> errors = new HashMap<String, String>();
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();          
            String query = "select select EngineNumber, ErrorNumber, Severity,  ErrorMessage " +
            				"from master..monErrorLog";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("EngineNumber") + "#" + rs.getInt("ErrorNumber") + "#" + rs.getInt("Severity");
                String error = rs.getString("ErrorMessage");
                errors.put(name, error); 
            }
            
            return errors;            
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
	}
}
