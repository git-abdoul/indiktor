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

public class OracleIOMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleIOMeasurement.class);
	
	public OracleIOMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleIOStorageResult> getStorageResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleIOStorageResult> storages = new ArrayList<OracleIOStorageResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select x.tablespace_name Tablespace, x.file_name FileName, x.bytes Allocated, " +
            				"(x.bytes-sum(y.bytes)) Used, " +
            				"x.bytes - (x.bytes-sum(y.bytes)) Unused, " + 
            				"((x.bytes-sum(y.bytes))/x.bytes) Pct_Used, x.status status " +
            				"from sys.dba_data_files x , sys.dba_free_space  y " + 
            				"where x.file_id = y.file_id " + 
            				"group by substr(to_char(x.file_id,999), 1,4), x.file_name, x.tablespace_name, x.bytes, x.status " + 
            				"order by 1,2,3";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("Tablespace");
                double allocated = rs.getDouble("Allocated");
                double used = rs.getDouble("Used");
                double unused = rs.getDouble("Unused");
                double percentUsed = rs.getDouble("Pct_Used");
                String status = rs.getString("status");
                storages.add(new OracleIOStorageResult(name, allocated, used, unused, percentUsed, status));
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
		return storages;
	}	
	
	public List<OracleIOResult> getIOResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleIOResult> ios = new ArrayList<OracleIOResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT ts.name AS ts, fs.phyrds Reads, fs.phywrts Writes, fs.phyblkrd AS br, fs.phyblkwrt AS bw, fs.readtim RTime, fs.writetim WTime " +
            				"FROM v$tablespace ts, v$datafile df, v$filestat fs " +
            				"WHERE ts.ts# = df.ts# AND df.file# = fs.file# " + 
            				"UNION " +
            				"SELECT ts.name AS ts, ts.phyrds Reads, ts.phywrts Writes, ts.phyblkrd AS br, ts.phyblkwrt AS bw, ts.readtim RTime, ts.writetim WTime " + 
            				"FROM v$tablespace ts, v$tempfile tf, v$tempstat ts " + 
            				"WHERE ts.ts# = tf.ts# AND tf.file# = ts.file# " + 
            				"ORDER BY 1";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("ts");
                double physicalReads = rs.getDouble("Reads");
                double physicalWrites = rs.getDouble("Writes");
                double blockReads = rs.getDouble("br");
                double blockWrites = rs.getDouble("bw");
                double readTime = rs.getDouble("RTime")*10;
                double writeTime = rs.getDouble("WTime")*10;
                ios.add(new OracleIOResult(name, physicalReads, physicalWrites, blockReads, blockWrites, readTime, writeTime));
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
		return ios;
	}	
	
	
	public class OracleIOStorageResult extends DbmsResult {
		private double allocated, used, unused, percentUsed;
		private String status;
		
		public OracleIOStorageResult(String name, double allocated,
				double used, double unused, double percentUsed, String status) {
			super(name);
			this.allocated = allocated;
			this.used = used;
			this.unused = unused;
			this.percentUsed = percentUsed;
			this.status = status;
		}

		public double getAllocated() {
			return allocated;
		}

		public double getUsed() {
			return used;
		}

		public double getUnused() {
			return unused;
		}

		public double getPercentUsed() {
			return percentUsed;
		}

		public String getStatus() {
			return status;
		}
	}
	
	public class OracleIOResult extends DbmsResult {
		private double physicalReads, physicalWrites, blockReads, blockWrites, readTime, writeTime;

		public OracleIOResult(String name, double physicalReads,
				double physicalWrites, double blockReads, double blockWrites,
				double readTime, double writeTime) {
			super(name);
			this.physicalReads = physicalReads;
			this.physicalWrites = physicalWrites;
			this.blockReads = blockReads;
			this.blockWrites = blockWrites;
			this.readTime = readTime;
			this.writeTime = writeTime;
		}

		public double getPhysicalReads() {
			return physicalReads;
		}

		public double getPhysicalWrites() {
			return physicalWrites;
		}

		public double getBlockReads() {
			return blockReads;
		}

		public double getBlockWrites() {
			return blockWrites;
		}

		public double getReadTime() {
			return readTime;
		}

		public double getWriteTime() {
			return writeTime;
		}		
	}

}
