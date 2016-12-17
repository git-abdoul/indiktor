package com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsResult;

public class OracleNetworkMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleNetworkMeasurement.class);
	
	public OracleNetworkMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleSharedServerResult> getShareServerResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleSharedServerResult> servers = new ArrayList<OracleSharedServerResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT name, paddr, messages, bytes, breaks, idle, busy, requests " +
            				"FROM v$shared_server";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("name") + "#" + rs.getString("paddr");
                double messages = rs.getDouble("messages");
                double bytes = rs.getDouble("bytes");
                double breaks = rs.getDouble("breaks");
                double idle = rs.getDouble("idle")*10;
                double busy = rs.getDouble("busy")*10;
                double requests = rs.getDouble("requests");
                servers.add(new OracleSharedServerResult(name, messages, bytes, breaks, idle, busy, requests));
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
		return servers;
	}	
	
	public List<OracleDispatcherResult> getDispatcherResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleDispatcherResult> dispatchers = new ArrayList<OracleDispatcherResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT name, network, status, messages, bytes, busy, idle " +
            				"FROM v$dispatcher ";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("name") + "#" + rs.getString("network");
            	String status = rs.getString("status");
                double messages = rs.getDouble("messages");
                double bytes = rs.getDouble("bytes");
                double busy = rs.getDouble("busy")*10;
                double idle = rs.getDouble("idle")*10;
                dispatchers.add(new OracleDispatcherResult(name, status, messages, bytes, idle, busy));
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
		return dispatchers;
	}	
	
	public List<OracleVirtualCircuitResult> getVirtualCircuitResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<OracleVirtualCircuitResult> circuits = new ArrayList<OracleVirtualCircuitResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT circuit, server, status, queue, bytes, breaks FROM v$circuit";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
                String name = rs.getString("circuit") + "#" + rs.getString("server");
                String status = rs.getString("status");
                String queue = rs.getString("queue");
                double bytes = rs.getDouble("bytes");
                double breaks = rs.getDouble("breaks");
                circuits.add(new OracleVirtualCircuitResult(name, status, queue, bytes, breaks));
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
		return circuits;
	}	
	
	public List<OracleNetworkEventResult> getNetworkEventResult()
	throws ConnectorException, FetchException, PersistenceException {		
		List<OracleNetworkEventResult> events = new ArrayList<OracleNetworkEventResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "SELECT event, time_waited, total_waits, average_wait, total_timeouts " +
            				"FROM V$SYSTEM_EVENT " +
            				"WHERE event IN (SELECT name FROM v$EVENT_NAME WHERE name LIKE '%dispatcher%' OR name LIKE '%circuit%' OR name LIKE '%SQL*Net%')" +
            				"ORDER BY EVENT";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String name = rs.getString("event");
                double totalTime = rs.getDouble("time_waited")*10;
                double waits = rs.getDouble("total_waits");
                double avgwait = rs.getDouble("average_wait")*10;
                double timeouts = rs.getDouble("total_timeouts");
                events.add(new OracleNetworkEventResult(name, totalTime, waits, avgwait, timeouts));
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
	
	public class OracleSharedServerResult extends DbmsResult {
		private double messages, bytes, breaks, idle, busy, requests;

		public OracleSharedServerResult(String name, double messages,
				double bytes, double breaks, double idle, double busy,
				double requests) {
			super(name);
			this.messages = messages;
			this.bytes = bytes;
			this.breaks = breaks;
			this.idle = idle;
			this.busy = busy;
			this.requests = requests;
		}

		public double getMessages() {
			return messages;
		}

		public double getBytes() {
			return bytes;
		}

		public double getBreaks() {
			return breaks;
		}

		public double getIdle() {
			return idle;
		}

		public double getBusy() {
			return busy;
		}

		public double getRequests() {
			return requests;
		}
	}
	
	public class OracleDispatcherResult extends DbmsResult {
		private String status;
		private double messages, bytes, idle, busy;
		
		public OracleDispatcherResult(String name, String status,
				double messages, double bytes, double idle, double busy) {
			super(name);
			this.status = status;
			this.messages = messages;
			this.bytes = bytes;
			this.idle = idle;
			this.busy = busy;
		}

		public String getStatus() {
			return status;
		}

		public double getMessages() {
			return messages;
		}

		public double getBytes() {
			return bytes;
		}

		public double getIdle() {
			return idle;
		}

		public double getBusy() {
			return busy;
		}		
	}
	
	public class OracleVirtualCircuitResult extends DbmsResult {
		private String status, queue;
		private double bytes, breaks;
		
		public OracleVirtualCircuitResult(String name, String status,
				String queue, double bytes, double breaks) {
			super(name);
			this.status = status;
			this.queue = queue;
			this.bytes = bytes;
			this.breaks = breaks;
		}

		public String getStatus() {
			return status;
		}

		public String getQueue() {
			return queue;
		}

		public double getBytes() {
			return bytes;
		}

		public double getBreaks() {
			return breaks;
		}
	}
	
	public class OracleNetworkEventResult extends DbmsResult {
		private double totalTime, waits, avgwait, timeouts;

		public OracleNetworkEventResult(String name, double totalTime,
				double waits, double avgwait, double timeouts) {
			super(name);
			this.totalTime = totalTime;
			this.waits = waits;
			this.avgwait = avgwait;
			this.timeouts = timeouts;
		}

		public double getTotalTime() {
			return totalTime;
		}

		public double getWaits() {
			return waits;
		}

		public double getAvgwait() {
			return avgwait;
		}

		public double getTimeouts() {
			return timeouts;
		}		
	}	

}
