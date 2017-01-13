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

public class OracleRollbackSegmentMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleRollbackSegmentMeasurement.class);
	
	public OracleRollbackSegmentMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleRollbackSegmentResult> getRollbackSegmentResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleRollbackSegmentResult> rolls = new ArrayList<OracleRollbackSegmentResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = 	"select b.NAME, a.STATUS, a.RSSIZE, a.EXTENTS, a.WRITES, a.GETS, a.WAITS, a.HWMSIZE, a.SHRINKS, a.WRAPS, a.EXTENDS " +
							"from v$rollstat a, v$rollname b " +
							"where a.USN = b.USN";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("NAME");
            	String status = rs.getString("STATUS");
                double size = rs.getDouble("RSSIZE");
                double extents = rs.getDouble("EXTENTS");
                double writes = rs.getDouble("WRITES");
                double gets = rs.getDouble("GETS");
                double waits = rs.getDouble("WAITS");
                double hwmSize = rs.getDouble("HWMSIZE");
                double shrinks = rs.getDouble("SHRINKS");
                double wraps = rs.getDouble("WRAPS");
                double extend = rs.getDouble("EXTENDS");
                rolls.add(new OracleRollbackSegmentResult(name, status, size, extents, writes, gets, waits, hwmSize, shrinks, wraps, extend));
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
		return rolls;
	}	
	
	
	public class OracleRollbackSegmentResult extends DbmsResult {
		private String status;
		private double size, extents, writes, gets, waits, hwmSize, shrinks, wraps, extend;
		
		public OracleRollbackSegmentResult(String name, String status,
				double size, double extents, double writes, double gets,
				double waits, double hwmSize, double shrinks, double wraps,
				double extend) {
			super(name);
			this.status = status;
			this.size = size;
			this.extents = extents;
			this.writes = writes;
			this.gets = gets;
			this.waits = waits;
			this.hwmSize = hwmSize;
			this.shrinks = shrinks;
			this.wraps = wraps;
			this.extend = extend;
		}

		public String getStatus() {
			return status;
		}

		public double getSize() {
			return size;
		}

		public double getExtents() {
			return extents;
		}

		public double getWrites() {
			return writes;
		}

		public double getGets() {
			return gets;
		}

		public double getWaits() {
			return waits;
		}

		public double getHwmSize() {
			return hwmSize;
		}

		public double getShrinks() {
			return shrinks;
		}

		public double getWraps() {
			return wraps;
		}

		public double getExtend() {
			return extend;
		}
	}
	
}
