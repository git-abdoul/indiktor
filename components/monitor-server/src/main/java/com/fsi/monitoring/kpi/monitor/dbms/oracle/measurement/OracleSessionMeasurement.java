package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleSessionMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleSessionMeasurement.class);
	
	public OracleSessionMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleSessionPerfResult> getSessionPerfResult()
	throws ConnectorException, FetchException, PersistenceException {		
		Map<String, Properties> values = new HashMap<String, Properties>();
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select a.MACHINE, a.USERNAME, a.PROGRAM, c.NAME, sum(b.VALUE) as value " +
            				"from v$session a, v$sesstat b, v$statname c " +
            				"where a.SID = b.SID and b.STATISTIC# = c.STATISTIC# and b.value !=0 and (name in ('parse time elapsed', 'recursive cpu usage', " + 
            				"'physical reads', 'sorts (memory)', 'physical writes', 'session logical reads', 'user commits', 'opened cursors current') or name like 'table scans%') " + 
            				"group by a.MACHINE, a.USERNAME, a.PROGRAM, c.NAME";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String machine = rs.getString("MACHINE");
            	String username = rs.getString("USERNAME");
            	String program = rs.getString("PROGRAM");            	
            	String key = machine + "#" + ((username!=null && username.length()>0)?username:program);
                Properties prop = null;
                if (!values.containsKey(key)) 
                	prop = new Properties();
                else 
                	prop = values.get(key);
                prop.put(rs.getString("NAME"), rs.getDouble("value"));
                values.put(key, prop); 
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
		return buildSessionPrefResults(values);
	}
	
	private List<OracleSessionPerfResult> buildSessionPrefResults(Map<String, Properties> values) {
		List<OracleSessionPerfResult> sessions = new ArrayList<OracleSessionPerfResult>();	
		for (String key : values.keySet()) {
			double elapsedTime=0, cpuUsed=0, memorySorts=0, tablesScans=0, physicalReads=0, physicalWrites=0, logicalReads=0, commit=0, cursor=0;
			Properties prop = values.get(key);
			for (Object id : prop.keySet()) {
				String propKey = (String)id;
				double val = (Double) prop.get(propKey);
				if ("parse time elapsed".equalsIgnoreCase(propKey)) {
					elapsedTime = val;
				} else if ("recursive cpu usage".equalsIgnoreCase(propKey)) {
					cpuUsed = val;
				} else if ("physical reads".equalsIgnoreCase(propKey)) {
					physicalReads = val;
				} else if ("sorts (memory)".equalsIgnoreCase(propKey)) {
					memorySorts = val;
				} else if ("physical writes".equalsIgnoreCase(propKey)) {
					physicalWrites = val;
				} else if ("session logical reads".equalsIgnoreCase(propKey)) {
					logicalReads = val;
				} else if ("user commits".equalsIgnoreCase(propKey)) {
					commit = val;
				} else if ("opened cursors current".equalsIgnoreCase(propKey)) {
					cursor = val;
				} else if (propKey.toLowerCase().equalsIgnoreCase("table scans")) {
					tablesScans = tablesScans + val;
				}			
			}
			sessions.add(new OracleSessionPerfResult(key, elapsedTime, cpuUsed, memorySorts, tablesScans, physicalReads, physicalWrites, logicalReads, commit, cursor));
		}		
		return sessions;
	}
	
	public List<OracleSessionWaitResult> getSessionWaitResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<OracleSessionWaitResult> sessions = new ArrayList<OracleSessionWaitResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select a.MACHINE, a.USERNAME, a.PROGRAM, a.LOGON_TIME, a.STATE, a.EVENT, sum(a.WAIT_TIME) as wait_time, sum(a.SECONDS_IN_WAIT) as second_in_wait " +
            				"from v$session a " +
            				"group by a.MACHINE, a.USERNAME, a.PROGRAM, a.LOGON_TIME, a.STATE, a.EVENT";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String machine = rs.getString("MACHINE");
            	String username = rs.getString("USERNAME");
            	String program = rs.getString("PROGRAM");            	
            	String name = machine + "#" + ((username!=null && username.length()>0)?username:program);
            	String status = rs.getString("STATE");
            	String event = rs.getString("EVENT");
                double waitTime = rs.getDouble("wait_time");
                double waitInSecond = rs.getDouble("second_in_wait");
                Date logon = rs.getDate("LOGON_TIME");
                long logonTime = (logon != null) ? logon.getTime() : -1;
                sessions.add(new OracleSessionWaitResult(name, status, event, waitTime, waitInSecond, logonTime));
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
		return sessions;
	}	
	
	
	public class OracleSessionPerfResult extends DbmsResult {
		private double elapsedTime, cpuUsed, memorySorts, tablesScans, physicalReads, physicalWrites, logicalReads, commit, cursor;

		public OracleSessionPerfResult(String name, double elapsedTime,
				double cpuUsed, double memorySorts, double tablesScans,
				double physicalReads, double physicalWrites,
				double logicalReads, double commit, double cursor) {
			super(name);
			this.elapsedTime = elapsedTime;
			this.cpuUsed = cpuUsed;
			this.memorySorts = memorySorts;
			this.tablesScans = tablesScans;
			this.physicalReads = physicalReads;
			this.physicalWrites = physicalWrites;
			this.logicalReads = logicalReads;
			this.commit = commit;
			this.cursor = cursor;
		}

		public double getElapsedTime() {
			return elapsedTime;
		}

		public double getCpuUsed() {
			return cpuUsed;
		}

		public double getMemorySorts() {
			return memorySorts;
		}

		public double getTablesScans() {
			return tablesScans;
		}

		public double getPhysicalReads() {
			return physicalReads;
		}

		public double getPhysicalWrites() {
			return physicalWrites;
		}

		public double getLogicalReads() {
			return logicalReads;
		}

		public double getCommit() {
			return commit;
		}

		public double getCursor() {
			return cursor;
		}		
	}
	
	public class OracleSessionWaitResult extends DbmsResult {
		private String status, event;
		private double waitTime, waitInSecond;
		private long logonTime;
		
		public OracleSessionWaitResult(String name, String status,
				String event, double waitTime, double waitInSecond,
				long logonTime) {
			super(name);
			this.status = status;
			this.event = event;
			this.waitTime = waitTime;
			this.waitInSecond = waitInSecond;
			this.logonTime = logonTime;
		}

		public String getStatus() {
			return status;
		}

		public String getEvent() {
			return event;
		}

		public double getWaitTime() {
			return waitTime;
		}

		public double getWaitInSecond() {
			return waitInSecond;
		}

		public long getLogonTime() {
			return logonTime;
		}
	}

}
