package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class SybaseEngineMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseEngineMeasurement.class);
	
	public SybaseEngineMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseEngineResult> getEngineResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<SybaseEngineResult> engines = new ArrayList<SybaseEngineResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select EngineNumber, Yields, Connections, Status, ProcessesAffinitied, StartTime, StopTime " +
            				"from master..monEngine";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getInt("EngineNumber") + "";
                int yields = rs.getInt("Yields");
                int connections = rs.getInt("Connections");
                int processesAffinitied = rs.getInt("ProcessesAffinitied");
                String status = rs.getString("Status");                
                DateTime startTime = new DateTime(rs.getTime("StartTime"));
                Time tmp = rs.getTime("StopTime");
                DateTime endTime = (tmp != null) ? new DateTime(tmp) : null;
                engines.add(new SybaseEngineResult(name, yields, connections, processesAffinitied, status, startTime, endTime));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return engines;
	}	
	
	public List<SybaseEngineCpuTimeResult> getEngineCpuTimeResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseEngineCpuTimeResult> engineCpuTimes = new ArrayList<SybaseEngineCpuTimeResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select EngineNumber, CPUTime, SystemCPUTime, UserCPUTime, IdleCPUTime " +
            				"from master..monEngine";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getInt("EngineNumber") + "";
                int global = rs.getInt("CPUTime");
                int system = rs.getInt("SystemCPUTime");
                int user = rs.getInt("UserCPUTime");
                int idle = rs.getInt("IdleCPUTime"); 
                engineCpuTimes.add(new SybaseEngineCpuTimeResult(name, global, system, user, idle));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return engineCpuTimes;
	}	
	
	public List<SybaseEngineDiskIOResult> getEngineDiskIOResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<SybaseEngineDiskIOResult> engineDiskIOs = new ArrayList<SybaseEngineDiskIOResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select EngineNumber, DiskIOChecks, DiskIOPolled, DiskIOCompleted " +
            				"from master..monEngine";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getInt("EngineNumber") + "";
                int checks = rs.getInt("DiskIOChecks");
                int polled = rs.getInt("DiskIOPolled");
                int completed = rs.getInt("DiskIOCompleted");
                engineDiskIOs.add(new SybaseEngineDiskIOResult(name, checks, polled, completed));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return engineDiskIOs;
	}	
	
	public List<SybaseEngineHkGcResult> getEngineHkGcResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<SybaseEngineHkGcResult> engineHkGcs = new ArrayList<SybaseEngineHkGcResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select EngineNumber, HkgcMaxQSize, HkgcPendingItems, HkgcHWMItems, HkgcOverflows " +
            				"from master..monEngine";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getInt("EngineNumber") + "";
                int maxQsize = rs.getInt("HkgcMaxQSize");
                int pendingItems = rs.getInt("HkgcPendingItems");
                int items = rs.getInt("HkgcHWMItems");
                int overflows = rs.getInt("HkgcOverflows"); 
                engineHkGcs.add(new SybaseEngineHkGcResult(name, maxQsize, pendingItems, items, overflows));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return engineHkGcs;
	}	
	
	
	public class SybaseEngineResult extends DbmsResult {
		private int yields, connections, processesAffinitied;
		private String status;
		private DateTime startTime;
		private DateTime endTime;
		
		public SybaseEngineResult(String name, int yields, int connections,
				int processesAffinitied, String status, DateTime startTime,
				DateTime endTime) {
			super(name);
			this.yields = yields;
			this.connections = connections;
			this.processesAffinitied = processesAffinitied;
			this.status = status;
			this.startTime = startTime;
			this.endTime = endTime;
		}
		public int getYields() {
			return yields;
		}
		public int getConnections() {
			return connections;
		}
		public int getProcessesAffinitied() {
			return processesAffinitied;
		}
		public String getStatus() {
			return status;
		}
		public DateTime getStartTime() {
			return startTime;
		}
		public DateTime getEndTime() {
			return endTime;
		}
	}
	
	public class SybaseEngineCpuTimeResult extends DbmsResult {
		private int global, system, user, idle;

		public SybaseEngineCpuTimeResult(String name, int global, int system,
				int user, int idle) {
			super(name);
			this.global = global;
			this.system = system;
			this.user = user;
			this.idle = idle;
		}

		public int getGlobal() {
			return global;
		}

		public int getSystem() {
			return system;
		}

		public int getUser() {
			return user;
		}

		public int getIdle() {
			return idle;
		}
	}
	
	public class SybaseEngineDiskIOResult extends DbmsResult {
		private int checks, polled, completed;

		public SybaseEngineDiskIOResult(String name, int checks, int polled,
				int completed) {
			super(name);
			this.checks = checks;
			this.polled = polled;
			this.completed = completed;
		}

		public int getChecks() {
			return checks;
		}

		public int getPolled() {
			return polled;
		}

		public int getCompleted() {
			return completed;
		}
	}
	
	public class SybaseEngineHkGcResult extends DbmsResult {
		private int maxQsize, pendingItems, items, overflows;

		public SybaseEngineHkGcResult(String name, int maxQsize,
				int pendingItems, int items, int overflows) {
			super(name);
			this.maxQsize = maxQsize;
			this.pendingItems = pendingItems;
			this.items = items;
			this.overflows = overflows;
		}

		public int getMaxQsize() {
			return maxQsize;
		}

		public int getPendingItems() {
			return pendingItems;
		}

		public int getItems() {
			return items;
		}

		public int getOverflows() {
			return overflows;
		}
	}
}
