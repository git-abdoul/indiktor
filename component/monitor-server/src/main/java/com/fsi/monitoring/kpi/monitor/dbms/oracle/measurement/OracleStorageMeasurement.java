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

public class OracleStorageMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleStorageMeasurement.class);
	
	public OracleStorageMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleStorageResult> getStorageResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<OracleStorageResult> storages = new ArrayList<OracleStorageResult>();
		getDbStorageResult(storages);
		getTableSpaceStorageResult(storages);
		return storages;
	}
	
	private List<OracleStorageResult> getDbStorageResult(List<OracleStorageResult> storages)
	throws ConnectorException, FetchException, PersistenceException {		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT round(sum(round(sum(nvl(fs.bytes/1024/1024,0)))) / sum(round(sum(nvl(fs.bytes/1024/1024,0))) " + 
            				"+ round(df.bytes/1024/1024 - sum(nvl(fs.bytes/1024/1024,0)))) * 100, 0) percent_free " +
            				",round(sum(round(df.bytes/1024/1024 -  sum(nvl(fs.bytes/1024/1024,0)))) / sum(round(sum " +
            				"(nvl(fs.bytes/1024/1024,0))) + round(df.bytes/1024/1024 - sum(nvl(fs.bytes/1024/1024,0)))) * 100, 0) percent_used " +
            				",sum(round(sum(nvl(fs.bytes/1024/1024,0)))) free, sum(round(df.bytes/1024/1024 - sum(nvl(fs.bytes/1024/1024,0)))) used " +
            				",sum(round(sum(nvl(fs.bytes/1024/1024,0))) + round(df.bytes/1024/1024 - sum(nvl(fs.bytes/1024/1024,0)))) db_size " +
            				"FROM dba_free_space fs, dba_data_files df " +
            				"WHERE fs.file_id(+) = df.file_id " +
            				"GROUP BY df.tablespace_name, df.file_id, df.bytes, df.autoextensible " + 
            				"ORDER BY df.file_id";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	double percentFree = rs.getDouble("percent_free")/100;
                double percentUsed = rs.getDouble("percent_used")/100;
                double free = rs.getDouble("free");
                double used = rs.getDouble("used");
                double total = rs.getDouble("db_size");
                storages.add(new OracleStorageResult("Global", percentFree, percentUsed, free, used, total));
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
	
	private List<OracleStorageResult> getTableSpaceStorageResult(List<OracleStorageResult> storages)
	throws ConnectorException, FetchException, PersistenceException {		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT df.tablespace_name Tablespace, round((sum(nvl(fs.bytes,0))/ (df.bytes)) * 100) percent_free " + 
            				",round(((df.bytes - sum(nvl(fs.bytes,0)))/ (df.bytes) ) * 100) percent_used " +
            				",round(sum(nvl(fs.bytes/1024/1024,0))) free, round(df.bytes/1024/1024 - sum(nvl(fs.bytes/1024/1024,0))) used " +
            				"FROM dba_free_space fs, dba_data_files df " +
            				"WHERE fs.file_id(+) = df.file_id " +
            				"GROUP BY df.tablespace_name, df.file_id, df.bytes, df.autoextensible " +
            				"ORDER BY df.file_id";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("Tablespace");
            	double percentFree = rs.getDouble("percent_free")/100;
                double percentUsed = rs.getDouble("percent_used")/100;
                double free = rs.getDouble("free");
                double used = rs.getDouble("used");
                storages.add(new OracleStorageResult(name, percentFree, percentUsed, free, used, -1));
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
	
	public class OracleStorageResult extends DbmsResult {
		private double percentFree, percentUsed, free, used, total;

		public OracleStorageResult(String name, double percentFree,
				double percentUsed, double free, double used, double total) {
			super(name);
			this.percentFree = percentFree;
			this.percentUsed = percentUsed;
			this.free = free;
			this.used = used;
			this.total = total;
		}

		public double getPercentFree() {
			return percentFree;
		}

		public double getPercentUsed() {
			return percentUsed;
		}

		public double getFree() {
			return free;
		}

		public double getUsed() {
			return used;
		}

		public double getTotal() {
			return total;
		}
	}

}
