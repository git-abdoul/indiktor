package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleSGAMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleSGAMeasurement.class);
	
	public OracleSGAMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public OracleSGASizeResult getSgaSizeResult()
	throws ConnectorException, FetchException, PersistenceException {	
		double bufferCache=0, sharedPool=0, dataDictionary=0, redoLog=0, libraryCache=0, sqlArea=0, fixedArea=0, freeMemory=0;
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "" +
            		"" +
            		"SELECT pool, name, bytes " +
            				"FROM v$sgastat " +
            				"WHERE pool IS NULL OR pool = 'large pool' OR (pool = 'shared pool' AND (name IN('dictionary cache','enqueue','library cache' " + 
            				",'parameters','processes','sessions','free memory', 'sql area'))) " + 
            				"ORDER BY pool DESC NULLS FIRST, name";
            rs = stmt.executeQuery(query);           
            while (rs != null && rs.next()) {
            	String key = rs.getString("name");
            	double val = rs.getDouble("bytes");
				if ("buffer_cache".equalsIgnoreCase(key)) {
					bufferCache = val;
				} else if ("dictionary cache".equalsIgnoreCase(key)) {
					dataDictionary = val;
				} else if ("log_buffer".equalsIgnoreCase(key)) {
					redoLog = val;
				} else if ("library cache".equalsIgnoreCase(key)) {
					libraryCache = val;
				} else if ("sql area".equalsIgnoreCase(key)) {
					sqlArea = val;
				} else if ("fixed_sga".equalsIgnoreCase(key)) {
					fixedArea = val;
				} else if ("free memory".equalsIgnoreCase(key)) {
					freeMemory = freeMemory + val;
				} 	
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
        
        sharedPool = getSharedPoolSize();
        
		return new OracleSGASizeResult("", bufferCache, sharedPool, dataDictionary, redoLog, libraryCache, sqlArea, fixedArea, freeMemory);
	}
	
	private double getSharedPoolSize()
	throws ConnectorException, FetchException, PersistenceException {		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select sum(bytes) as value " +
            				"from v$sgastat " +
            				"where pool = 'shared pool'";
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
	
	private Map<String, Double> getLibraryCacheHitRatio()
	throws ConnectorException, FetchException, PersistenceException {			
		Map<String, Double> ratios = new HashMap<String, Double>();
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT 'Library Lock Requests' Ratio , ROUND(AVG(gethitratio) * 100, 2) Percentage FROM V$LIBRARYCACHE " +
            				"UNION " +
            				"SELECT 'Library Pin Requests' Ratio , ROUND(AVG(pinhitratio) * 100, 2) Percentage FROM V$LIBRARYCACHE " +
            				"UNION " +
            				"SELECT 'Library I/O Reloads' Ratio , ROUND((SUM(reloads) / SUM(pins)) * 100, 2) Percentage FROM V$LIBRARYCACHE " +
            				"UNION " +
            				"SELECT 'Library Reparses' Ratio , ROUND((SUM(reloads) / SUM(pins)) * 100, 2) Percentage FROM V$LIBRARYCACHE";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) 
            	ratios.put(rs.getString("Ratio"), rs.getDouble("Percentage")/100);
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
		return ratios;
	}
	
	private double getBufferCacheHitRatio()
	throws ConnectorException, FetchException, PersistenceException {			
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT ROUND((1 - ((SELECT SUM(value) FROM V$SYSSTAT WHERE name = 'physical reads') " +
            				"/ ((SELECT SUM(value) FROM V$SYSSTAT WHERE name = 'db block gets') " + 
            				"+ (SELECT SUM(value) FROM V$SYSSTAT WHERE name = 'consistent gets') ))) * 100) Percentage " + 
            				"FROM DUAL";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) 
            	return rs.getDouble("Percentage") / 100;
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
	
	private double getDataDictionaryHitRatio()
	throws ConnectorException, FetchException, PersistenceException {			
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT ROUND((1 - (SUM(GETMISSES)/SUM(GETS))) * 100,2) Percentage " +
            				"FROM V$ROWCACHE";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) 
            	return rs.getDouble("Percentage") / 100;
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
	
	public OracleHitRatioResult getSgaHitRatioResult()
	throws ConnectorException, FetchException, PersistenceException {		
		double bufferCache = getBufferCacheHitRatio();
		double dataDictionary = getDataDictionaryHitRatio();
		Map<String, Double> libraryRatios = getLibraryCacheHitRatio();
		double libraryIoReloads = libraryRatios.get("Library I/O Reloads");
		double libraryLockRequests = libraryRatios.get("Library Lock Requests");
		double libraryPinRequests = libraryRatios.get("Library Pin Requests");
		double libraryReparses = libraryRatios.get("Library Reparses");		
		return new OracleHitRatioResult("", bufferCache, dataDictionary, libraryIoReloads, libraryLockRequests, libraryPinRequests, libraryReparses);
	}	
	
	
	public class OracleSGASizeResult extends DbmsResult {
		private double bufferCache, sharedPool, dataDictionary, redoLog, libraryCache, sqlArea, fixedArea, freeMemory;

		public OracleSGASizeResult(String name, double bufferCache,
				double sharedPool, double dataDictionary, double redoLog,
				double libraryCache, double sqlArea, double fixedArea,
				double freeMemory) {
			super(name);
			this.bufferCache = bufferCache;
			this.sharedPool = sharedPool;
			this.dataDictionary = dataDictionary;
			this.redoLog = redoLog;
			this.libraryCache = libraryCache;
			this.sqlArea = sqlArea;
			this.fixedArea = fixedArea;
			this.freeMemory = freeMemory;
		}

		public double getBufferCache() {
			return bufferCache;
		}

		public double getSharedPool() {
			return sharedPool;
		}

		public double getDataDictionary() {
			return dataDictionary;
		}

		public double getRedoLog() {
			return redoLog;
		}

		public double getLibraryCache() {
			return libraryCache;
		}

		public double getSqlArea() {
			return sqlArea;
		}

		public double getFixedArea() {
			return fixedArea;
		}

		public double getFreeMemory() {
			return freeMemory;
		}
	}
	
	public class OracleHitRatioResult extends DbmsResult {
		private double bufferCache, dataDictionary, libraryIoReloads, libraryLockRequests, libraryPinRequests, libraryReparses;

		public OracleHitRatioResult(String name, double bufferCache,
				double dataDictionary, double libraryIoReloads,
				double libraryLockRequests, double libraryPinRequests,
				double libraryReparses) {
			super(name);
			this.bufferCache = bufferCache;
			this.dataDictionary = dataDictionary;
			this.libraryIoReloads = libraryIoReloads;
			this.libraryLockRequests = libraryLockRequests;
			this.libraryPinRequests = libraryPinRequests;
			this.libraryReparses = libraryReparses;
		}

		public double getBufferCache() {
			return bufferCache;
		}

		public double getDataDictionary() {
			return dataDictionary;
		}

		public double getLibraryIoReloads() {
			return libraryIoReloads;
		}

		public double getLibraryLockRequests() {
			return libraryLockRequests;
		}

		public double getLibraryPinRequests() {
			return libraryPinRequests;
		}

		public double getLibraryReparses() {
			return libraryReparses;
		}		
	}
	
}
