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

public class OracleRedoMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleRedoMeasurement.class);
	
	public OracleRedoMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleRedoResult> getRedoResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<OracleRedoResult> redos = new ArrayList<OracleRedoResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select name, value from V$SYSSTAT where name like '%redo%'";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("name");
                double value = rs.getDouble("value");
                redos.add(new OracleRedoResult(name, value));
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
		return redos;
	}		
	
	public class OracleRedoResult extends DbmsResult {
		private double value;

		public OracleRedoResult(String name, double value) {
			super(name);
			this.value = value;
		}

		public double getValue() {
			return value;
		}
	}

}
