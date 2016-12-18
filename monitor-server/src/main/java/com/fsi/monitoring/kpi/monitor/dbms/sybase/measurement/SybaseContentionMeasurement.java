package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;

public class SybaseContentionMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseContentionMeasurement.class);
	
	public SybaseContentionMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public int getDeadLockNumber()
	throws ConnectorException, FetchException, PersistenceException {	
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select count(*) from master..monDeadLock";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) {
               return rs.getInt("1");
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return 0;
	}

}
