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

public class SybaseMonitorConfMeasurement 
extends RdbmsConnectorDAO {

//	private static final Logger LOG = Logger.getLogger(SybaseMonitorConfMeasurement.class);
	
	public SybaseMonitorConfMeasurement(RdbmsConnector rdbmsConnector) {
		super(rdbmsConnector);
	}
	
	public List<SybaseMonitorConfResult> getMonitorConfResult()
	throws ConnectorException, FetchException, PersistenceException {	
		List<SybaseMonitorConfResult> monitorConfs = new ArrayList<SybaseMonitorConfResult>();		
		Connection con = null;
		Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	con = rdbmsConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("sp_monitorconfig 'all'");
            while (rs != null && rs.next()) {
                String confOpt = rs.getString("name").trim().replaceAll("\\s+", "_");
                double active = rs.getDouble("Num_active");
                double maxUsed = rs.getDouble("Max_Used");
                double free = rs.getDouble("Num_free");
                double reUse = rs.getDouble("Reuse_cnt");
                double utilizationRatio =rs.getDouble("Pct_act");
                monitorConfs.add(new SybaseMonitorConfResult(confOpt, free, active, utilizationRatio, maxUsed, reUse));
            }
        } catch (Exception e) {
			throw new FetchException(e);
		}        
        finally {
			closeResultSet(rs);
			closeStatement(stmt);
	        closeConnection(con);         
        }  
		return monitorConfs;
	}
	
//	protected double getValue(String metric, String alias, String configOpt) throws Exception {
//        float value = -1;
//        if (alias.equalsIgnoreCase(MAX_USED))
//            value = getMaxUsed(configOpt);
//        else if (alias.equalsIgnoreCase(NUM_REUSED))
//            value = getNumReuse(configOpt);
//        else if (alias.equalsIgnoreCase(NUM_FREE))
//            value = getNumFree(configOpt);
//        else if (alias.equalsIgnoreCase(NUM_ACTIVE))
//            value = getNumActive(configOpt);
//        else
//            value = getPercentActive(configOpt);
//        return value;
//    }
//	
//	private float getNumActive(String configOpt) throws PersistenceException {
//    	float value = -1;
//    	
//    	Connection con = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try
//        {
//    		con = dataSource.getConnection();
//	        stmt = con.createStatement();
//	        rs = stmt.executeQuery("sp_monitorconfig '"+configOpt+"'");
//	        if (rs.next())
//	        	value = rs.getFloat("Num_active");
//        }
//        catch (SQLException e) {
//        	LOG.error(e.getMessage(), e);
//        }
//        finally {    
//        	closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
//        }
//        return value;
//    }
//
//    private float getNumFree(String configOpt)
//    {
//    	float value = -1;
//    	
//    	Connection con = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try
//        {
//    		con = dataSource.getConnection();
//	        stmt = con.createStatement();
//            rs = stmt.executeQuery("sp_monitorconfig '"+configOpt+"'");
//            if (rs.next())
//                value = rs.getFloat("Num_free");
//        }
//        catch (SQLException e) {
//        	LOG.error(e.getMessage(), e);
//        }
//        finally {
//        	try {
//        		if (stmt != null)
//        			stmt.close();
//        		if (rs != null)
//        			rs.close();
//        	} catch (SQLException e) {
//        		LOG.error(e.getMessage(), e);
//			}
//        }
//        return value;
//    }
//
//    private float getNumReuse(String configOpt) throws PersistenceException {
//    	float value = -1;
//    	
//    	Connection con = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try
//        {
//    		con = dataSource.getConnection();
//	        stmt = con.createStatement();
//            rs = stmt.executeQuery("sp_monitorconfig '"+configOpt+"'");
//            try {
//            int col = rs.findColumn("Num_Reuse");
//            if (rs.next()) {
//                return rs.getFloat(col);
//            }
//            } catch (SQLException e) {
//            }
//            int col = rs.findColumn("Reuse");
//            if (rs.next()) {
//                return rs.getFloat(col);
//            }
//        }
//        catch (SQLException e) {
//        	LOG.error(e.getMessage(), e);
//        }
//        finally {
//        	closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
//        }
//        return value;
//    }
//
//    private float getMaxUsed(String configOpt) throws PersistenceException {
//    	float value = -1;    	
//    	Connection con = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try
//        {
//    		con = dataSource.getConnection();
//	        stmt = con.createStatement();
//            rs = stmt.executeQuery("sp_monitorconfig '"+configOpt+"'");
//            if (rs.next())
//                value = rs.getFloat("Max_Used");
//        }
//        catch (SQLException e) {
//        	LOG.error(e.getMessage(), e);
//        }
//        finally {
//        	closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
//        }
//        return value;
//    }
//
//    private float getPercentActive(String configOpt) throws PersistenceException {
//    	float value = -1;
//    	
//    	Connection con = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try
//        {
//    		con = dataSource.getConnection();
//	        stmt = con.createStatement();
//            rs = stmt.executeQuery("sp_monitorconfig '"+configOpt+"'");
//            if (rs.next())
//                value = rs.getFloat("Pct_act");
//        }
//        catch (SQLException e) {
//        	LOG.error(e.getMessage(), e);
//        }
//        finally {
//        	closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
//        }
//        return value;
//    }
    
    public class SybaseMonitorConfResult extends DbmsResult {
    	private double numFree,	numActive, pctAct,	maxUsed, reuseCnt;	
    	
    	public SybaseMonitorConfResult(String name, double numFree, double numActive,
    			double pctAct, double maxUsed, double reuseCnt) {
    		super(name);
    		this.numFree = numFree;
    		this.numActive = numActive;
    		this.pctAct = pctAct;
    		this.maxUsed = maxUsed;
    		this.reuseCnt = reuseCnt;
    	}
    	
    	public double getNumFree() {
    		return numFree;
    	}
    	
    	public double getNumActive() {
    		return numActive;
    	}
    	
    	public double getPctAct() {
    		return pctAct;
    	}
    	
    	public double getMaxUsed() {
    		return maxUsed;
    	}
    	
    	public double getReuseCnt() {
    		return reuseCnt;
    	}
    }
}
