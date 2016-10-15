package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class SybaseStatementMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseStatementMeasurement.class);
	
	public SybaseStatementMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseStatementResult> getStatementResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseStatementResult> processes = new ArrayList<SybaseStatementResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement(); 
            String query = "select txt.SQLText, stmt.CpuTime, stmt.MemUsageKB, stmt.PlansAltered, stmt.StartTime, stmt.EndTime, stmt.PhysicalReads, stmt.LogicalReads, stmt.PagesModified, stmt.PacketsSent, stmt.PacketsReceived, stmt.NetworkPacketSize, stmt.WaitTime " +
            				"from master..monSysStatement stmt, master..monSysSQLText txt " +
            				"where stmt.SPID = txt.SPID and stmt.KPID = txt.KPID and stmt.BatchID = txt.BatchID";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("SQLText");
                int cpuTime = rs.getInt("CpuTime");
                int memory = rs.getInt("MemUsageKB");
                DateTime startTime = new DateTime(rs.getTime("StartTime"));
                DateTime endTime = new DateTime(rs.getTime("EndTime"));
                int duration = (int)((endTime != null) ? endTime.getMillis() : new DateTime().getMillis() - startTime.getMillis());
                int plansAltered = rs.getInt("PlansAltered");                
                int physicalReads = rs.getInt("PhysicalReads");
                int logicalReads = rs.getInt("LogicalReads");
                int pagesModified = rs.getInt("PagesModified");
                int packetsSent = rs.getInt("PacketsSent");
                int packetsReceived = rs.getInt("PacketsReceived");
                int packetsSize = rs.getInt("NetworkPacketSize");
                int waitTime = rs.getInt("WaitTime");                
                SybaseStatementIOResult io = new SybaseStatementIOResult(name, physicalReads,logicalReads, pagesModified);        				
                SybaseStatementContentionResult contention = new SybaseStatementContentionResult(name, waitTime);        		
                SybaseStatementNetworkResult network = new SybaseStatementNetworkResult(name, packetsSent, packetsReceived, packetsSize);
                processes.add(new SybaseStatementResult(name, cpuTime, memory, duration, plansAltered, startTime, endTime, io, contention, network));
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
	
	public class SybaseStatementResult extends DbmsResult {
		private int cpuTime, memory, duration, plansAltered;
		private DateTime startTime, endTime;
		private SybaseStatementIOResult io;
		private SybaseStatementContentionResult contention;
		private SybaseStatementNetworkResult network;	

		public SybaseStatementResult(String name, int cpuTime, int memory,
				int duration, int plansAltered, DateTime startTime,
				DateTime endTime, SybaseStatementIOResult io,
				SybaseStatementContentionResult contention,
				SybaseStatementNetworkResult network) {
			super(name);
			this.cpuTime = cpuTime;
			this.memory = memory;
			this.duration = duration;
			this.plansAltered = plansAltered;
			this.startTime = startTime;
			this.endTime = endTime;
			this.io = io;
			this.contention = contention;
			this.network = network;
		}

		public int getCpuTime() {
			return cpuTime;
		}

		public int getMemory() {
			return memory;
		}

		public DateTime getStartTime() {
			return startTime;
		}

		public DateTime getEndTime() {
			return endTime;
		}

		public int getDuration() {
			return duration;
		}

		public int getPlansAltered() {
			return plansAltered;
		}

		public SybaseStatementIOResult getIo() {
			return io;
		}

		public SybaseStatementContentionResult getContention() {
			return contention;
		}

		public SybaseStatementNetworkResult getNetwork() {
			return network;
		}
	}
	
	public class SybaseStatementIOResult extends DbmsResult {
		private int physicalReads, logicalReads, pagesModified;

		public SybaseStatementIOResult(String name, int physicalReads,
				int logicalReads, int pagesModified) {
			super(name);
			this.physicalReads = physicalReads;
			this.logicalReads = logicalReads;
			this.pagesModified = pagesModified;
		}

		public int getPhysicalReads() {
			return physicalReads;
		}

		public int getLogicalReads() {
			return logicalReads;
		}

		public int getPagesModified() {
			return pagesModified;
		}
	}
	
	public class SybaseStatementContentionResult extends DbmsResult {
		private int waitTime;

		public SybaseStatementContentionResult(String name, int waitTime) {
			super(name);
			this.waitTime = waitTime;
		}

		public int getWaitTime() {
			return waitTime;
		}		
	}
	
	public class SybaseStatementNetworkResult extends DbmsResult {
		private int packetsSent, packetsReceived, packetsSize;

		public SybaseStatementNetworkResult(String name, int packetsSent,
				int packetsReceived, int packetsSize) {
			super(name);
			this.packetsSent = packetsSent;
			this.packetsReceived = packetsReceived;
			this.packetsSize = packetsSize;
		}

		public int getPacketsSent() {
			return packetsSent;
		}

		public int getPacketsReceived() {
			return packetsReceived;
		}

		public int getPacketsSize() {
			return packetsSize;
		}
	}
}
