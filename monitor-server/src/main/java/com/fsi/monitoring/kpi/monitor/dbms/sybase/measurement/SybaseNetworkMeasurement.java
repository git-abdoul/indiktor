package com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class SybaseNetworkMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseNetworkMeasurement.class);
	
	public SybaseNetworkMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public SybaseNetworkResult getNetworkResult()
	throws ConnectorException, FetchException, PersistenceException {	
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select PacketsSent, PacketsReceived, BytesSent, BytesReceived " +
            				"from master..monNetworkIO";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                int packetsSent = rs.getInt("PacketsSent");
                int packetsReceived = rs.getInt("PacketsReceived");
                int bytesSent = rs.getInt("BytesSent");
                int bytesReceived = rs.getInt("BytesReceived");
                return new SybaseNetworkResult("", packetsSent, packetsReceived, bytesSent, bytesReceived);
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
	
	public class SybaseNetworkResult extends DbmsResult {
		private int packetsSent, packetsReceived, bytesSent, bytesReceived;

		public SybaseNetworkResult(String name, int packetsSent,
				int packetsReceived, int bytesSent, int bytesReceived) {
			super(name);
			this.packetsSent = packetsSent;
			this.packetsReceived = packetsReceived;
			this.bytesSent = bytesSent;
			this.bytesReceived = bytesReceived;
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

}
