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

public class SybaseProcessMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseProcessMeasurement.class);
	
	public SybaseProcessMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseProcessResult> getProcessResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessResult> processes = new ArrayList<SybaseProcessResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(act.CPUTime) as CPUTime, sum(act.MemUsageKB) as MemUsageKB, sum(act.TableAccesses) as TableAccesses, sum(act.IndexAccesses) as IndexAccesses " +
            				"from master..monProcess p, master..monProcessActivity act " +
            				"where act.KPID = p.KPID and act.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int cpuTime = rs.getInt("CPUTime");
                int memory = rs.getInt("MemUsageKB");
                int indexAccesses = rs.getInt("TableAccesses");
                int tableAccesses = rs.getInt("IndexAccesses");
                processes.add(new SybaseProcessResult(name, cpuTime, memory, indexAccesses, tableAccesses));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	
	public List<SybaseProcessNetworkResult> getProcessNetworkResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessNetworkResult> processes = new ArrayList<SybaseProcessNetworkResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();            
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(net.NetworkPacketSize) as NetworkPacketSize, sum(net.PacketsSent) as PacketsSent, sum(net.PacketsReceived) as PacketsReceived, sum(net.BytesSent) as BytesSent, sum(net.BytesReceived) as BytesReceived " +
            				"from master..monProcess p, master..monProcessNetIO net " +
            				"where net.KPID = p.KPID and net.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int packetsSize = rs.getInt("NetworkPacketSize");
                int packetsSent = rs.getInt("PacketsSent");
                int packetsReceived = rs.getInt("PacketsReceived");
                int bytesSent = rs.getInt("BytesSent");
                int bytesReceived = rs.getInt("BytesReceived");
                processes.add(new SybaseProcessNetworkResult(name, packetsSize, packetsSent, packetsReceived, bytesSent, bytesReceived));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	
	public List<SybaseProcessContentionResult> getProcessContentionResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessContentionResult> processes = new ArrayList<SybaseProcessContentionResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(act.WaitTime) as WaitTime, sum(act.LocksHeld) as LocksHeld " +
            				"from master..monProcess p, master..monProcessActivity act " +
            				"where act.KPID = p.KPID and act.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int waitTime = rs.getInt("WaitTime");
                int locksHeld = rs.getInt("LocksHeld");
                processes.add(new SybaseProcessContentionResult(name, waitTime, locksHeld));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }
		return processes;
	}	
	
	public List<SybaseProcessTransactionResult> getProcessTransactionResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessTransactionResult> processes = new ArrayList<SybaseProcessTransactionResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select p.DBName, p.EngineNumber, p.Command, act.Transactions, act.Commits, act.Rollbacks " +
            				"from master..monProcess p, master..monProcessActivity act " +
            				"where act.KPID = p.KPID and act.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int count = rs.getInt("Transactions");
                int commits = rs.getInt("Commits");
                int rollbacks = rs.getInt("Rollbacks");
                processes.add(new SybaseProcessTransactionResult(name, count, commits, rollbacks));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	
	public List<SybaseProcessULCResult> getProcessULCResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessULCResult> processes = new ArrayList<SybaseProcessULCResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(act.ULCBytesWritten) as ULCBytesWritten, sum(act.ULCFlushes) as ULCFlushes, sum(act.ULCFlushFull) as ULCFlushFull, sum(act.ULCCurrentUsage) as ULCCurrentUsage " +
            				"from master..monProcess p, master..monProcessActivity act " +
            				"where act.KPID = p.KPID and act.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int bytesWritten = rs.getInt("ULCBytesWritten");
                int flush = rs.getInt("ULCFlushes");
                int flushFull = rs.getInt("ULCFlushFull");
                int usage = rs.getInt("ULCCurrentUsage");
                processes.add(new SybaseProcessULCResult(name, bytesWritten, flush, flushFull, usage));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	
	public List<SybaseProcessThreadResult> getProcessThreadResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessThreadResult> processes = new ArrayList<SybaseProcessThreadResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(thread.ThreadsActive) as ThreadsActive, sum(thread.ParallelQueries) as ParallelQueries, sum(thread.PlansAltered) as PlansAltered " +
            				"from master..monProcess p, master..monProcessWorkerThread thread " +
            				"where thread.KPID = p.KPID and thread.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int activeCount = rs.getInt("ThreadsActive");
                int parallelQueries = rs.getInt("ParallelQueries");
                int plansAltered = rs.getInt("PlansAltered");
                processes.add(new SybaseProcessThreadResult(name, activeCount, parallelQueries, plansAltered));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	
	public List<SybaseProcessIOResult> getProcessIOResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseProcessIOResult> processes = new ArrayList<SybaseProcessIOResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select p.DBName, p.EngineNumber, p.Command, sum(act.PhysicalReads) as PhysicalReads, sum(act.LogicalReads) as LogicalReads, sum(act.PagesRead) as PagesRead, sum(act.PhysicalWrites) as PhysicalWrites, sum(act.PagesWritten) as PagesWritten " +
            				"from master..monProcess p, master..monProcessActivity act " +
            				"where act.KPID = p.KPID and act.SPID = p.SPID " +
            				"group by p.DBName, p.EngineNumber, p.Command";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "#" + rs.getString("EngineNumber") + "#" + rs.getString("Command");
                int physicalReads = rs.getInt("PhysicalReads");
                int logicalReads = rs.getInt("LogicalReads");
                int pagesRead = rs.getInt("PagesRead");
                int physicalWrites = rs.getInt("PhysicalWrites");
                int pagesWritten = rs.getInt("PagesWritten");
                processes.add(new SybaseProcessIOResult(name, physicalReads, logicalReads, pagesRead, physicalWrites, pagesWritten));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return processes;
	}	
	public class SybaseProcessResult extends DbmsResult {
		private int cpuTime, memory, indexAccesses, tableAccesses;

		public SybaseProcessResult(String name, int cpuTime, int memory,
				int indexAccesses, int tableAccesses) {
			super(name);
			this.cpuTime = cpuTime;
			this.memory = memory;
			this.indexAccesses = indexAccesses;
			this.tableAccesses = tableAccesses;
		}

		public int getCpuTime() {
			return cpuTime;
		}

		public int getMemory() {
			return memory;
		}

		public int getIndexAccesses() {
			return indexAccesses;
		}

		public int getTableAccesses() {
			return tableAccesses;
		}
	}
	
	public class SybaseProcessNetworkResult extends DbmsResult {
		private int packetsSize, packetsSent, packetsReceived, bytesSent, bytesReceived;

		public SybaseProcessNetworkResult(String name, int packetsSize,
				int packetsSent, int packetsReceived, int bytesSent,
				int bytesReceived) {
			super(name);
			this.packetsSize = packetsSize;
			this.packetsSent = packetsSent;
			this.packetsReceived = packetsReceived;
			this.bytesSent = bytesSent;
			this.bytesReceived = bytesReceived;
		}

		public int getPacketsSize() {
			return packetsSize;
		}

		public int getPacketsSent() {
			return packetsSent;
		}

		public int getPacketsReceived() {
			return packetsReceived;
		}

		public int getBytesSent() {
			return bytesSent;
		}

		public int getBytesReceived() {
			return bytesReceived;
		}
	}
	
	public class SybaseProcessContentionResult extends DbmsResult {
		private int waitTime, locksHeld;

		public SybaseProcessContentionResult(String name, int waitTime,
				int locksHeld) {
			super(name);
			this.waitTime = waitTime;
			this.locksHeld = locksHeld;
		}

		public int getWaitTime() {
			return waitTime;
		}

		public int getLocksHeld() {
			return locksHeld;
		}
	}
	
	public class SybaseProcessIOResult extends DbmsResult {
		private int physicalReads, logicalReads, pagesRead, physicalWrites, pagesWritten;

		public SybaseProcessIOResult(String name, int physicalReads,
				int logicalReads, int pagesRead, int physicalWrites,
				int pagesWritten) {
			super(name);
			this.physicalReads = physicalReads;
			this.logicalReads = logicalReads;
			this.pagesRead = pagesRead;
			this.physicalWrites = physicalWrites;
			this.pagesWritten = pagesWritten;
		}

		public int getPhysicalReads() {
			return physicalReads;
		}

		public int getLogicalReads() {
			return logicalReads;
		}

		public int getPagesRead() {
			return pagesRead;
		}

		public int getPhysicalWrites() {
			return physicalWrites;
		}

		public int getPagesWritten() {
			return pagesWritten;
		}
	}
	
	public class SybaseProcessTransactionResult extends DbmsResult {
		private int count, commits, rollbacks;

		public SybaseProcessTransactionResult(String name, int count,
				int commits, int rollbacks) {
			super(name);
			this.count = count;
			this.commits = commits;
			this.rollbacks = rollbacks;
		}

		public int getCount() {
			return count;
		}

		public int getCommits() {
			return commits;
		}

		public int getRollbacks() {
			return rollbacks;
		}
	}
	
	public class SybaseProcessULCResult extends DbmsResult {
		private int bytesWritten, flush, flushFull, usage;

		public SybaseProcessULCResult(String name, int bytesWritten,
				int flush, int flush_full, int usage) {
			super(name);
			this.bytesWritten = bytesWritten;
			this.flush = flush;
			this.flushFull = flush_full;
			this.usage = usage;
		}

		public int getBytesWritten() {
			return bytesWritten;
		}

		public int getFlush() {
			return flush;
		}

		public int getFlushFull() {
			return flushFull;
		}

		public int getUsage() {
			return usage;
		}
	}
	
	public class SybaseProcessThreadResult extends DbmsResult {
		private int activeCount, parallelQueries, plansAltered;

		public SybaseProcessThreadResult(String name, int activeCount,
				int parallelQueries, int plansAltered) {
			super(name);
			this.activeCount = activeCount;
			this.parallelQueries = parallelQueries;
			this.plansAltered = plansAltered;
		}

		public int getActiveCount() {
			return activeCount;
		}

		public int getParallelQueries() {
			return parallelQueries;
		}

		public int getPlansAltered() {
			return plansAltered;
		}
	}
}
