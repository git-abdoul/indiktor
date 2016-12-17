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

public class OracleLockMeasurement extends RdbmsConnectorDAO {

	private static final Logger LOG = Logger.getLogger(OracleLockMeasurement.class);
	
	public OracleLockMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}	
	
	public List<OracleLockResult> getLockResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<OracleLockResult> locks = new ArrayList<OracleLockResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();             
            String query = "select sess.MACHINE, sess.USERNAME, sess.PROGRAM, decode(a.type,'MR', 'Media Recovery', 'RT', 'Redo Thread', 'UN', 'User Name', 'TX', 'Transaction', 'TM', 'DML', " + 
            			   "'UL', 'PL/SQL User Lock', 'DX', 'Distributed Xaction', 'CF', 'Control File', 'IS', 'Instance State',  'FS', 'File Set', 'IR', 'Instance Recovery', " + 
            			   "'ST', 'Disk Space Transaction', 'IR', 'Instance Recovery', 'ST', 'Disk Space Transaction', 'TS', 'Temp Segment', 'IV', 'Library Cache Invalidation', 'LS', 'Log Start or Switch', " + 
            			   "'RW', 'Row Wait', 'SQ', 'Sequence Number', 'TE', 'Extend Table', 'TT', 'Temp Table',  a.type) lock_type, " +
            			   "decode(a.lmode, 0, 'None', 1, 'Null', 2, 'Row-S (SS)', 3, 'Row-X (SX)', 4, 'Share', 5, 'S/Row-X (SSX)', 6, 'Exclusive', to_char(a.lmode)) mode_held, " + 
            			   "decode(a.request, 0, 'None', 1, 'Null', 2, 'Row-S (SS)', 3, 'Row-X (SX)', 4, 'Share', 5, 'S/Row-X (SSX)', 6, 'Exclusive', to_char(a.request)) mode_requested," + 
            			   "to_char(a.id1) lock_id1, to_char(a.id2) lock_id2 " +   
            			   "from v$lock a, v$session sess " +
            			   "where a.sid=sess.sid and (id1,id2) in (select b.id1, b.id2 from v$lock b where b.id1 = a.id1 and b.id2 = a.id2 and b.request > 0) " + 
            			   "order by 1 asc, 2 asc";
            rs = stmt.executeQuery(query);
            while (rs != null && rs.next()) {
            	String machine = rs.getString("MACHINE");
            	String username = rs.getString("USERNAME");
            	String program = rs.getString("PROGRAM");            	
            	String name = machine + "#" + ((username!=null && username.length()>0)?username:program);
                String lockType = rs.getString("lock_type");
                String modeHeld = rs.getString("mode_held");
                String modeRequested = rs.getString("mode_requested");
                String id1 = rs.getString("lock_id1");
                String id2 = rs.getString("lock_id2");
                locks.add(new OracleLockResult(name, lockType, modeHeld, modeRequested, id1, id2));
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
		return locks;
	}	
	
	
	public class OracleLockResult extends DbmsResult {
		private String lockType, modeHeld, modeRequested, id1, id2;

		public OracleLockResult(String name, String lockType, String modeHeld,
				String modeRequested, String id1, String id2) {
			super(name);
			this.lockType = lockType;
			this.modeHeld = modeHeld;
			this.modeRequested = modeRequested;
			this.id1 = id1;
			this.id2 = id2;
		}

		public String getLockType() {
			return lockType;
		}

		public String getModeHeld() {
			return modeHeld;
		}

		public String getModeRequested() {
			return modeRequested;
		}

		public String getId1() {
			return id1;
		}

		public String getId2() {
			return id2;
		}		
	}

}
