package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleSegmentMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(OracleDatabaseMeasurement.class);
	
	public OracleSegmentMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<OracleSegmentResult> getSegmentData() 
	throws ConnectorException, FetchException, PersistenceException {
		List<OracleSegmentResult> res = new ArrayList<OracleSegmentResult>();
		
		Date fetchDate = new Date();
		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();  
            String query = "SELECT SEGMENT_NAME,BYTES,MAX_EXTENTS from USER_SEGMENTS";
            rs = stmt.executeQuery(query);            
            while (rs.next()) {
            	String tableName = rs.getString("SEGMENT_NAME");
            	double size = rs.getDouble("BYTES");
                double maxSize = rs.getDouble("MAX_EXTENTS");
               res.add(new OracleSegmentResult(tableName,size,maxSize));
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
		return res;
	}
	
	public class OracleSegmentResult extends DbmsResult {
		private double segmentSize;
		private double segmentMaxExtents;
		
		public OracleSegmentResult(String tableName, 
											double segmentSize,
											double segmentMaxExtents) {
			super(tableName);
			this.segmentSize = segmentSize;
			this.segmentMaxExtents = segmentMaxExtents;
		}

		public String getSegmentSize() {
			return String.valueOf(segmentSize);
		}

		public String getSegmentMaxExtents() {
			return String.valueOf(segmentMaxExtents);
		}

		public String getSegmentSizeRatio() {
			double ratio = segmentSize/segmentMaxExtents;	
			return String.valueOf(ratio);
		}
	}
	

}
