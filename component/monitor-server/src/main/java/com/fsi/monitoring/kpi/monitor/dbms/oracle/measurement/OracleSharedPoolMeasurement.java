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

public class OracleSharedPoolMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleSharedPoolMeasurement.class);
	
	public OracleSharedPoolMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleSharedPoolHitResult> getSharedPoolHitResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleSharedPoolHitResult> pools = new ArrayList<OracleSharedPoolHitResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select namespace, gets, gethits, pins, pinhits, reloads, invalidations FROM V$LIBRARYCACHE";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("namespace");
                double gets = rs.getDouble("gets");
                double getHits = rs.getDouble("gethits");
                double pins = rs.getDouble("pins");
                double pinHits = rs.getDouble("pinhits");
                double reloads = rs.getDouble("reloads");
                double invalidations = rs.getDouble("invalidations");
                pools.add(new OracleSharedPoolHitResult(name, gets, getHits, pins, pinHits, reloads, invalidations));
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
		return pools;
	}	
	
	public List<OracleSharedPoolMemoryResult> getSharedPoolMemoryResult()
	throws ConnectorException, FetchException, PersistenceException {			
		List<OracleSharedPoolMemoryResult> memories = new ArrayList<OracleSharedPoolMemoryResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select lc_namespace, lc_inuse_memory_objects, lc_inuse_memory_size, lc_freeable_memory_objects, lc_freeable_memory_size from v$library_cache_memory";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("lc_namespace");
                double inuseSize = rs.getDouble("lc_inuse_memory_size")*1000000;
                double inuseCount = rs.getDouble("lc_inuse_memory_objects");
                double freeableSize = rs.getDouble("lc_freeable_memory_size")*1000000;
                double freeableCount = rs.getDouble("lc_freeable_memory_objects");
                memories.add(new OracleSharedPoolMemoryResult(name, inuseSize, inuseCount, freeableSize, freeableCount));
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
		return memories;
	}	
	
	
	public class OracleSharedPoolHitResult extends DbmsResult {
		private double gets, getHits, pins, pinHits, reloads, invalidations;

		public OracleSharedPoolHitResult(String name, double gets, double getHits,
				double pins, double pinHits, double reloads,
				double invalidations) {
			super(name);
			this.gets = gets;
			this.getHits = getHits;
			this.pins = pins;
			this.pinHits = pinHits;
			this.reloads = reloads;
			this.invalidations = invalidations;
		}

		public double getGets() {
			return gets;
		}

		public double getGetHits() {
			return getHits;
		}

		public double getPins() {
			return pins;
		}

		public double getPinHits() {
			return pinHits;
		}

		public double getReloads() {
			return reloads;
		}

		public double getInvalidations() {
			return invalidations;
		}		
	}
	
	public class OracleSharedPoolMemoryResult extends DbmsResult {
		private double inuseSize, inuseCount, freeableSize, freeableCount;

		public OracleSharedPoolMemoryResult(String name, double inuseSize,
				double inuseCount, double freeableSize, double freeableCount) {
			super(name);
			this.inuseSize = inuseSize;
			this.inuseCount = inuseCount;
			this.freeableSize = freeableSize;
			this.freeableCount = freeableCount;
		}

		public double getInuseSize() {
			return inuseSize;
		}

		public double getInuseCount() {
			return inuseCount;
		}

		public double getFreeableSize() {
			return freeableSize;
		}

		public double getFreeableCount() {
			return freeableCount;
		}
	}
}
