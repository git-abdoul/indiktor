package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;


public class SybaseDeviceMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseDeviceMeasurement.class);
	
	public SybaseDeviceMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseDeviceIOResult> getDeviceIOResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<SybaseDeviceIOResult> deviceIOs = new ArrayList<SybaseDeviceIOResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select LogicalName, Reads, APFReads, Writes, DevSemaphoreRequests, DevSemaphoreWaits, IOTime " +
            				"from master..monDeviceIO";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("LogicalName");
                int reads = rs.getInt("Reads");
                int apfreads = rs.getInt("APFReads");
                int writes = rs.getInt("Writes");
                int semaphoreRequests = rs.getInt("DevSemaphoreRequests");
                int semaphoreWaits =rs.getInt("DevSemaphoreWaits");
                int iotime = rs.getInt("IOTime");
                deviceIOs.add(new SybaseDeviceIOResult(name, reads, apfreads, writes, semaphoreRequests, semaphoreWaits, iotime));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
		return deviceIOs;
	}	
	
	public class SybaseDeviceIOResult extends DbmsResult {
		private int reads, apfreads, writes, semaphoreRequests, semaphoreWaits, iotime;

		public SybaseDeviceIOResult(String name, int reads, int apfreads,
				int writes, int semaphoreRequests, int semaphoreWaits,
				int iotime) {
			super(name);
			this.reads = reads;
			this.apfreads = apfreads;
			this.writes = writes;
			this.semaphoreRequests = semaphoreRequests;
			this.semaphoreWaits = semaphoreWaits;
			this.iotime = iotime;
		}

		public int getReads() {
			return reads;
		}

		public int getApfreads() {
			return apfreads;
		}

		public int getWrites() {
			return writes;
		}

		public int getSemaphoreRequests() {
			return semaphoreRequests;
		}

		public int getSemaphoreWaits() {
			return semaphoreWaits;
		}

		public int getIotime() {
			return iotime;
		}		
	}

}
