package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;


public class SybaseCacheMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseCacheMeasurement.class);
	
	public SybaseCacheMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseDataCacheResult> getDataCacheResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseDataCacheResult> datacaches = new ArrayList<SybaseDataCacheResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();
            String query = "select CacheName, BufferPools, CacheSearches, PhysicalReads, LogicalReads, PhysicalWrites, Stalls, CachePartitions " +
            				"from master..monDataCache";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("CacheName");
                int bufferPools = rs.getInt("BufferPools");
                int searches = rs.getInt("CacheSearches");
                int physicalReads = rs.getInt("PhysicalReads");
                int logicalReads = rs.getInt("LogicalReads");
                int physicalWrites =rs.getInt("PhysicalWrites");
                int stalls = rs.getInt("Stalls");
                int cachePartitions =rs.getInt("CachePartitions");
                datacaches.add(new SybaseDataCacheResult(name, bufferPools, searches, physicalReads, logicalReads, physicalWrites, stalls, cachePartitions));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }     
		return datacaches;
	}
	
	public List<SybaseCachedObjectResult> getCachedObjectResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseCachedObjectResult> cachedObjects = new ArrayList<SybaseCachedObjectResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();
            String query = "select DBName, CacheName, ObjectName, CachedKB, TotalSizeKB " +
            				"from master..monCachedObject " +
            				"order by DBName";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String dbname = rs.getString("DBName");
            	String cacheName = rs.getString("CacheName");
            	String objectName = rs.getString("ObjectName");
            	if (objectName == null || objectName.length()<1)
            		continue;
                String name = dbname + "#" + cacheName + "#" +objectName;
                int cachedMemory = rs.getInt("CachedKB");
                int size = rs.getInt("TotalSizeKB");
                cachedObjects.add(new SybaseCachedObjectResult(name, cachedMemory, size));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return cachedObjects;
	}
	
	public List<SybaseCachePoolResult> getCachePoolResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseCachePoolResult> cachePools = new ArrayList<SybaseCachePoolResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select CacheName, IOBufferSize, AllocatedKB, PhysicalReads, Stalls, PagesTouched, PagesRead, BuffersToMRU, BuffersToLRU " +
            				"from master..monCachePool";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("CacheName");
                int ioBufferSize = rs.getInt("IOBufferSize");
                int memory = rs.getInt("AllocatedKB");
                int physicalReads = rs.getInt("PhysicalReads");
                int stalls = rs.getInt("Stalls");
                int pagesTouched =rs.getInt("PagesTouched");
                int pagesRead = rs.getInt("PagesRead");
                int bufferToMRU =rs.getInt("BuffersToMRU");
                int bufferToLRU =rs.getInt("BuffersToLRU");
                cachePools.add(new SybaseCachePoolResult(name, ioBufferSize, memory, physicalReads, stalls, pagesTouched, pagesRead, bufferToMRU, bufferToLRU));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }     
		return cachePools;
	}
	
	public SybaseProcedureCacheResult getProcedureCacheResult()
	throws ConnectorException, FetchException, PersistenceException {	
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;        
        try
        {
        	Map<String, Integer> memories = new HashMap<String, Integer>();
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();                 
            String query = "select DBName, sum(MemUsageKB) as memory " +
            				"from master..monCachedProcedures " +
            				"group by DBName";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString(1);
                int memory = rs.getInt(2);
                memories.put(name, memory);
            }
            
            rs.close();
            stmt.close();
            
            stmt = con.createStatement();             
            query = "select Requests, Loads, Writes, Stalls " +
            				"from master..monProcedureCache";
            rs = stmt.executeQuery(query);
            if (rs != null && rs.next()) {
                int requests = rs.getInt("Requests");
                int loads = rs.getInt("Loads");
                int writes = rs.getInt("Writes");
                int stalls = rs.getInt("Stalls");   
               return new SybaseProcedureCacheResult("CacheProc", memories, requests, loads, writes, stalls);
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }        
		return null;
	}
	
	public class SybaseDataCacheResult extends DbmsResult {
		private int bufferPools, searches, physicalReads, logicalReads, physicalWrites, stalls, cachePartitions;
		
		public SybaseDataCacheResult(String name, int bufferPools, int searches,
				int physicalReads, int logicalReads, int physicalWrites,
				int stalls, int cachePartitions) {
			super(name);
			this.bufferPools = bufferPools;
			this.searches = searches;
			this.physicalReads = physicalReads;
			this.logicalReads = logicalReads;
			this.physicalWrites = physicalWrites;
			this.stalls = stalls;
			this.cachePartitions = cachePartitions;
		}

		public int getBufferPools() {
			return bufferPools;
		}

		public int getSearches() {
			return searches;
		}

		public int getPhysicalReads() {
			return physicalReads;
		}

		public int getLogicalReads() {
			return logicalReads;
		}

		public int getPhysicalWrites() {
			return physicalWrites;
		}

		public int getStalls() {
			return stalls;
		}

		public int getCachePartitions() {
			return cachePartitions;
		}
		
	}
	
	public class SybaseCachedObjectResult extends DbmsResult {
		private int cachedMemory, size;

		public SybaseCachedObjectResult(String name, int cachedMemory, int size) {
			super(name);
			this.cachedMemory = cachedMemory;
			this.size = size;
		}

		public int getCachedMemory() {
			return cachedMemory;
		}

		public int getSize() {
			return size;
		}		
	}
	
	public class SybaseCachePoolResult extends DbmsResult {
		private int ioBufferSize, memory, physicalReads, stalls, pagesTouched, pagesRead, bufferToMRU, bufferToLRU;

		public SybaseCachePoolResult(String name, int ioBufferSize, int memory,
				int physicalReads, int stalls, int pagesTouched, int pagesRead,
				int bufferToMRU, int bufferToLRU) {
			super(name);
			this.ioBufferSize = ioBufferSize;
			this.memory = memory;
			this.physicalReads = physicalReads;
			this.stalls = stalls;
			this.pagesTouched = pagesTouched;
			this.pagesRead = pagesRead;
			this.bufferToMRU = bufferToMRU;
			this.bufferToLRU = bufferToLRU;
		}

		public int getIoBufferSize() {
			return ioBufferSize;
		}

		public int getMemory() {
			return memory;
		}

		public int getPhysicalReads() {
			return physicalReads;
		}

		public int getStalls() {
			return stalls;
		}

		public int getPagesTouched() {
			return pagesTouched;
		}

		public int getPagesRead() {
			return pagesRead;
		}

		public int getBufferToMRU() {
			return bufferToMRU;
		}

		public int getBufferToLRU() {
			return bufferToLRU;
		}		
	}
	
	public class SybaseProcedureCacheResult extends DbmsResult {
		private Map<String, Integer> memories;  
		private int requests, loads, writes, stalls;

		public SybaseProcedureCacheResult(String name, Map<String, Integer> memories,
				int requests, int loads, int writes, int stalls) {
			super(name);
			this.memories = memories;
			this.requests = requests;
			this.loads = loads;
			this.writes = writes;
			this.stalls = stalls;
		}

		public Map<String, Integer> getMemories() {
			return memories;
		}

		public int getRequests() {
			return requests;
		}

		public int getLoads() {
			return loads;
		}

		public int getWrites() {
			return writes;
		}

		public int getStalls() {
			return stalls;
		}
	}

}
