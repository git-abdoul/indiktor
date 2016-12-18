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

public class OracleLatchMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleLatchMeasurement.class);
	
	public OracleLatchMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleLatchResult> getLacthResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleLatchResult> latches = new ArrayList<OracleLatchResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT name Latch, gets, misses, TO_CHAR(ROUND(misses/gets,2),'99990.00') MissRate, immediate_gets, immediate_misses " +
            				"FROM v$latch " +
            				"WHERE gets!=0 AND misses! =0 " + 
            				"ORDER BY 4 DESC";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("Latch");
                double gets = rs.getDouble("gets");
                double misses = rs.getDouble("misses");
                double missRate = rs.getDouble("MissRate");
                double immediateGets = rs.getDouble("immediate_gets");
                double immediateMisses = rs.getDouble("immediate_misses");
                latches.add(new OracleLatchResult(name, gets, misses, missRate, immediateGets, immediateMisses));
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
		return latches;
	}	
	
	public List<OracleLatchPerfResult> getLatchPerfResult()
	throws ConnectorException, FetchException, PersistenceException {
		List<OracleLatchPerfResult> perfs = new ArrayList<OracleLatchPerfResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT name Latch, ROUND(wait_time/1000) WaitTime, ROUND((wait_time/(SELECT SUM(wait_time) FROM v$latch))*100,0) Percent, sleeps " +
            				"FROM v$latch " +
            				"WHERE wait_time! =0 " +
            				"ORDER BY 3 DESC";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("Latch");
                double waitTime = rs.getDouble("WaitTime");
                double percent = rs.getDouble("Percent");
                double sleep = rs.getDouble("sleeps");
                perfs.add(new OracleLatchPerfResult(name, waitTime, percent, sleep));
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
		return perfs;
	}	
	
	
	public class OracleLatchResult extends DbmsResult {
		private double gets, misses, missRate, immediateGets, immediateMisses;

		public OracleLatchResult(String name, double gets, double misses,
				double missRate, double immediateGets, double immediateMisses) {
			super(name);
			this.gets = gets;
			this.misses = misses;
			this.missRate = missRate;
			this.immediateGets = immediateGets;
			this.immediateMisses = immediateMisses;
		}

		public double getGets() {
			return gets;
		}

		public double getMisses() {
			return misses;
		}

		public double getMissRate() {
			return missRate;
		}

		public double getImmediateGets() {
			return immediateGets;
		}

		public double getImmediateMisses() {
			return immediateMisses;
		}		
	}
	
	public class OracleLatchPerfResult extends DbmsResult {
		private double waitTime, percent, sleep;

		public OracleLatchPerfResult(String name, double waitTime,
				double percent, double sleep) {
			super(name);
			this.waitTime = waitTime;
			this.percent = percent;
			this.sleep = sleep;
		}

		public double getWaitTime() {
			return waitTime;
		}

		public double getPercent() {
			return percent;
		}

		public double getSleep() {
			return sleep;
		}		
	}
	

}
