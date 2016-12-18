package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleStatementMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleStatementMeasurement.class);
	
	public OracleStatementMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleSQLStatsResult> getSQLStatsResult()
	throws ConnectorException, FetchException, PersistenceException {		
		Map<String, OracleSQLStatsResult> results = new HashMap<String, OracleSQLStatsResult>();
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select sql_text, sorts, fetches, executions, loads, invalidations, parse_calls, disk_reads, buffer_gets, rows_processed, cpu_time, elapsed_time from v$sqlarea";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("sql_text");
                OracleSQLStatsResult sqlStatsRes = results.get(name); 
                if (sqlStatsRes == null)
                	sqlStatsRes = new OracleSQLStatsResult(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                sqlStatsRes.sorts = sqlStatsRes.sorts + rs.getDouble("sorts");
                sqlStatsRes.fetches = sqlStatsRes.fetches + rs.getDouble("fetches");
                sqlStatsRes.executions = sqlStatsRes.executions + rs.getDouble("executions");
                sqlStatsRes.loads = sqlStatsRes.loads + rs.getDouble("loads");
                sqlStatsRes.invalidations = sqlStatsRes.invalidations + rs.getDouble("invalidations");
                sqlStatsRes.parseCalls = sqlStatsRes.parseCalls + rs.getDouble("parse_calls");
                sqlStatsRes.diskReads = sqlStatsRes.diskReads + rs.getDouble("disk_reads");
                sqlStatsRes.bufferGets = sqlStatsRes.bufferGets + rs.getDouble("buffer_gets");
                sqlStatsRes.rowsProcessed = sqlStatsRes.rowsProcessed + rs.getDouble("rows_processed");
                sqlStatsRes.cpuTime = sqlStatsRes.cpuTime + rs.getDouble("cpu_time");
                sqlStatsRes.elapsedTime = sqlStatsRes.elapsedTime + rs.getDouble("elapsed_time");
                results.put(name, sqlStatsRes);
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
		return new ArrayList<OracleSQLStatsResult>(results.values());
	}	
	
	public OracleSQLGlobalStatsMemoryResult getSQLGlobalStatsMemoryResult()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSQLGlobalStatsMemoryResult globalStat = null;		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select name, value from v$sysstat " +
            				"where name in ('parse time cpu', 'parse time elapsed', 'parse count (total)', 'parse count (hard)', 'parse count (failures)', 'execute count')";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) {
            	double cpuParsetime=0, elapsedParseTime=0, totalParseCount=0, hardParseCount=0, failedParseCount=0, ExecuteCount=0;
            	String key = rs.getString("name");
            	double val = rs.getDouble("value");
				if ("parse time cpu".equalsIgnoreCase(key)) {
					cpuParsetime = val;
				} else if ("parse time elapsed".equalsIgnoreCase(key)) {
					elapsedParseTime = val;
				} else if ("parse count (total)".equalsIgnoreCase(key)) {
					totalParseCount = val;
				} else if ("parse count (hard)".equalsIgnoreCase(key)) {
					hardParseCount = val;
				} else if ("parse count (failures)".equalsIgnoreCase(key)) {
					failedParseCount = val;
				} else if ("execute count".equalsIgnoreCase(key)) {
					ExecuteCount = val;
				}
                globalStat = new OracleSQLGlobalStatsMemoryResult("", cpuParsetime, elapsedParseTime, totalParseCount, hardParseCount, failedParseCount, ExecuteCount);
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
		return globalStat;
	}	
	
	
	public class OracleSQLStatsResult extends DbmsResult {
		private double sorts, fetches, executions, loads, invalidations, parseCalls, diskReads, bufferGets, rowsProcessed, cpuTime, elapsedTime;

		public OracleSQLStatsResult(String name, double sorts, double fetches,
				double executions, double loads, double invalidations,
				double parseCalls, double diskReads, double bufferGets,
				double rowsProcessed, double cpuTime, double elapsedTime) {
			super(name);
			this.sorts = sorts;
			this.fetches = fetches;
			this.executions = executions;
			this.loads = loads;
			this.invalidations = invalidations;
			this.parseCalls = parseCalls;
			this.diskReads = diskReads;
			this.bufferGets = bufferGets;
			this.rowsProcessed = rowsProcessed;
			this.cpuTime = cpuTime;
			this.elapsedTime = elapsedTime;
		}

		public double getSorts() {
			return sorts;
		}

		public double getFetches() {
			return fetches;
		}

		public double getExecutions() {
			return executions;
		}

		public double getLoads() {
			return loads;
		}

		public double getInvalidations() {
			return invalidations;
		}

		public double getParseCalls() {
			return parseCalls;
		}

		public double getDiskReads() {
			return diskReads;
		}

		public double getBufferGets() {
			return bufferGets;
		}

		public double getRowsProcessed() {
			return rowsProcessed;
		}

		public double getCpuTime() {
			return cpuTime;
		}

		public double getElapsedTime() {
			return elapsedTime;
		}		
	}
	
	public class OracleSQLGlobalStatsMemoryResult extends DbmsResult {
		private double cpuParsetime, elapsedParseTime, totalParseCount, hardParseCount, failedParseCount, ExecuteCount;

		public OracleSQLGlobalStatsMemoryResult(String name,
				double cpuParsetime, double elapsedParseTime,
				double totalParseCount, double hardParseCount,
				double failedParseCount, double executeCount) {
			super(name);
			this.cpuParsetime = cpuParsetime;
			this.elapsedParseTime = elapsedParseTime;
			this.totalParseCount = totalParseCount;
			this.hardParseCount = hardParseCount;
			this.failedParseCount = failedParseCount;
			ExecuteCount = executeCount;
		}

		public double getCpuParsetime() {
			return cpuParsetime;
		}

		public double getElapsedParseTime() {
			return elapsedParseTime;
		}

		public double getTotalParseCount() {
			return totalParseCount;
		}

		public double getHardParseCount() {
			return hardParseCount;
		}

		public double getFailedParseCount() {
			return failedParseCount;
		}

		public double getExecuteCount() {
			return ExecuteCount;
		}
	}
}
