package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleDatabaseMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(OracleDatabaseMeasurement.class);
	
	public OracleDatabaseMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public OracleDatabaseResult getDbInfoResult() 
	throws ConnectorException, FetchException, PersistenceException {
		OracleDatabaseResult db = null;		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try {
        	String name = "";
        	long creationDate = 0;
    		String logMode="", openMode="";
    		double currentConnections=0;
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();  
            String query = "select name, created, log_mode, open_mode from v$database";
            rs = stmt.executeQuery(query);            
            if (rs != null && rs.next()) {
            	name = rs.getString("name");
            	creationDate = rs.getDate("created").getTime();
                logMode = rs.getString("log_mode");
                openMode = rs.getString("open_mode");                   
            }
            currentConnections = getCurrentConnections();
            db = new OracleDatabaseResult(name, creationDate, logMode, openMode, currentConnections);            
        } catch(Exception exc) {
        	if (exc instanceof ConnectorException) {
        		throw (ConnectorException)exc;
        	}
        	if (exc instanceof SQLException) {
        		rdbmsConnector.reportFailure(exc);
        	} 
        	throw new FetchException(exc);
        }     
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
		return db;
	}		
	
	private double getCurrentConnections()
	throws ConnectorException, FetchException, PersistenceException {	
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select name, value from v$sysstat where name in ('logons current')";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) 
            	return rs.getDouble("value");
        } catch(Exception exc) {
        	if (exc instanceof ConnectorException) {
        		throw (ConnectorException)exc;
        	}
        	if (exc instanceof SQLException) {
        		rdbmsConnector.reportFailure(exc);
        	} 
        	throw new FetchException(exc);
        }       
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
		return -1;
	}
	
	public class OracleDatabaseResult extends DbmsResult {
		private long creationDate;
		private String logMode, openMode;
		private double currentConnections;
		
		public OracleDatabaseResult(String name, long creationDate,
				String logMode, String openMode, double currentConnections) {
			super(name);
			this.creationDate = creationDate;
			this.logMode = logMode;
			this.openMode = openMode;
			this.currentConnections = currentConnections;
		}

		public long getCreationDate() {
			return creationDate;
		}

		public String getLogMode() {
			return logMode;
		}

		public String getOpenMode() {
			return openMode;
		}

		public double getCurrentConnections() {
			return currentConnections;
		}
	}
	

}
