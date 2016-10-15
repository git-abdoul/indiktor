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

public class SybasePartitionMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybasePartitionMeasurement.class);
	
	public SybasePartitionMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybasePartitionResult> getPartitionResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybasePartitionResult> partitions = new ArrayList<SybasePartitionResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select DBName, PartitionName, LogicalReads, PhysicalReads, APFReads, PagesRead, PhysicalWrites, PagesWritten, RowsInserted, RowsDeleted, RowsUpdated " +
            				"from master..monOpenPartitionActivity";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("DBName") + "." + rs.getString("PartitionName");
                int logicalReads = rs.getInt("LogicalReads");
                int physicalReads = rs.getInt("PhysicalReads");
                int apfReads = rs.getInt("APFReads");
                int pagesRead = rs.getInt("PagesRead");
                int physicalWrites =rs.getInt("PhysicalWrites");
                int pagesWritten = rs.getInt("PagesWritten");
                int rowsInserted = rs.getInt("RowsInserted");
                int rowsDeleted =rs.getInt("RowsDeleted");
                int rowsUpdated = rs.getInt("RowsUpdated");
                partitions.add(new SybasePartitionResult(name, logicalReads, physicalReads, apfReads, pagesRead, physicalWrites, pagesWritten, rowsInserted, rowsDeleted, rowsUpdated));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        } 
		return partitions;
	}	
	
	public class SybasePartitionResult extends DbmsResult {
		private int logicalReads, physicalReads, apfReads, pagesRead, physicalWrites, pagesWritten;
		private int rowsInserted, rowsDeleted, rowsUpdated;	
		
		public SybasePartitionResult(String name, int logicalReads,
				int physicalReads, int apfReads, int pagesRead,
				int physicalWrites, int pagesWritten, int rowsInserted,
				int rowsDeleted, int rowsUpdated) {
			super(name);
			this.logicalReads = logicalReads;
			this.physicalReads = physicalReads;
			this.apfReads = apfReads;
			this.pagesRead = pagesRead;
			this.physicalWrites = physicalWrites;
			this.pagesWritten = pagesWritten;
			this.rowsInserted = rowsInserted;
			this.rowsDeleted = rowsDeleted;
			this.rowsUpdated = rowsUpdated;
		}

		public int getLogicalReads() {
			return logicalReads;
		}

		public int getPhysicalReads() {
			return physicalReads;
		}

		public int getApfReads() {
			return apfReads;
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

		public int getRowsInserted() {
			return rowsInserted;
		}

		public int getRowsDeleted() {
			return rowsDeleted;
		}

		public int getRowsUpdated() {
			return rowsUpdated;
		}
	}
}
