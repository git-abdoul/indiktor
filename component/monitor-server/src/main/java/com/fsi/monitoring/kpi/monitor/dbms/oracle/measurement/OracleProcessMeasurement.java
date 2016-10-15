package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleProcessMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleProcessMeasurement.class);
	
	public OracleProcessMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleProcessMemoryResult> getProcessMemoryResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleProcessMemoryResult> processes = new ArrayList<OracleProcessMemoryResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select username, program, pga_used_mem, pga_alloc_mem, pga_freeable_mem from v$process";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String usr = rs.getString("username");
            	String program = rs.getString("program");
            	String name = (usr != null && usr.length()>0) ? usr + "." + program : program;
                double memoryUsed = rs.getDouble("pga_used_mem");
                double memoryAllocated = rs.getDouble("pga_alloc_mem");
                double memoryFreeable = rs.getDouble("pga_freeable_mem");
                processes.add(new OracleProcessMemoryResult(name, memoryUsed, memoryAllocated, memoryFreeable));
            }
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
		return processes;
	}	
	
	
	public class OracleProcessMemoryResult extends DbmsResult {
		private double memoryUsed, memoryAllocated, memoryFreeable;

		public OracleProcessMemoryResult(String name, double memoryUsed,
				double memoryAllocated, double memoryFreeable) {
			super(name);
			this.memoryUsed = memoryUsed;
			this.memoryAllocated = memoryAllocated;
			this.memoryFreeable = memoryFreeable;
		}

		public double getMemoryUsed() {
			return memoryUsed;
		}

		public double getMemoryAllocated() {
			return memoryAllocated;
		}

		public double getMemoryFreeable() {
			return memoryFreeable;
		}
		
	}
}
