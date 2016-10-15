package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleWaitEventMeasurement extends RdbmsConnectorDAO {
	
	public OracleWaitEventMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleWaitEventResult> getWaitEventResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleWaitEventResult> events = new ArrayList<OracleWaitEventResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();  
            String idleEvent = "SELECT name FROM v$event_name " + 
            					"WHERE name LIKE '%null%' OR name LIKE '%timer%' OR name LIKE '%SQL*Net%' OR name LIKE '%rdbms ipc%' " + 
            					"OR name LIKE '%ispatcher%' OR name LIKE '%virtual circuit%' OR name LIKE '%PX%' OR name LIKE '%pipe%' " +
            					"OR name LIKE '%message%' OR name LIKE 'jobq%'";
            
            String query = "SELECT event, total_waits Waits, time_waited Total_Time, TO_CHAR((time_waited /(SELECT SUM(time_waited) FROM v$system_event WHERE event not in " +
            				"(" + idleEvent + ")))*100, 990.99) Percentage " +
            				"FROM v$system_event " + "" +
            				"WHERE event not in " + "(" + idleEvent + ") " +
            				"ORDER BY event" ;
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("event");
                double waits = rs.getDouble("Waits");
                double totalTime = rs.getDouble("Total_Time")*10;
                double percentage = rs.getDouble("Percentage")/100;
                events.add(new OracleWaitEventResult(name, waits, totalTime, percentage));
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
		return events;
	}		
	
	public class OracleWaitEventResult extends DbmsResult {
		private double waits, totalTime, percentage;

		public OracleWaitEventResult(String name, double waits,
				double totalTime, double percentage) {
			super(name);
			this.waits = waits;
			this.totalTime = totalTime;
			this.percentage = percentage;
		}

		public double getWaits() {
			return waits;
		}

		public double getTotalTime() {
			return totalTime;
		}

		public double getPercentage() {
			return percentage;
		}		
	}
}
