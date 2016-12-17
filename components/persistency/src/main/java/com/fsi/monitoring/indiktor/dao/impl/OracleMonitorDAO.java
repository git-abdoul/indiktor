package com.fsi.monitoring.indiktor.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.jdbc.driver.OraclePreparedStatement;
import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.fwk.util.DateUtil;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.msd.StaticData;


public class OracleMonitorDAO extends AbstractMonitorDAO {
	
	public Map<Long,AbstractIkrDefinition> getIkrDefinitions(Collection<Long> ids) 
	throws PersistenceException {	
		
		if (ids != null && ids.size()>=1000)
			return getBigIkrDefinitions(new ArrayList<Long>(ids));
		
		Map<Long,AbstractIkrDefinition> res = new HashMap<Long,AbstractIkrDefinition>();
	
		String query = "SELECT * FROM IKR_DEFINITION";
		String whereClause = null;
		if (ids != null) {
			whereClause = getWhereClause("ID", ids);
		} else {
			whereClause = "";
		}
			
		query = query + whereClause + " ORDER BY IKR_INSTANCE";
	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
	    
	
			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();
	
			while (rs.next()) {
				AbstractIkrDefinition ikrDefinition = mapIkrDefiniton(rs);
				 if (ikrDefinition != null) {
					 res.put(ikrDefinition.getId(),ikrDefinition);
				 }
			}
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	
		return res;	
	}
	
	private Map<Long,AbstractIkrDefinition> getBigIkrDefinitions(List<Long> ids) throws PersistenceException {	
		Map<Long,AbstractIkrDefinition> res = new HashMap<Long,AbstractIkrDefinition>();
		
		String query = "SELECT * FROM IKR_DEFINITION WHERE ID IN (SELECT * FROM TABLE (SELECT CAST(? AS IKR_TABLE_NUMBER) FROM DUAL)) ORDER BY IKR_INSTANCE";
	
		long longArray[] = new long[ids.size()];
        for(int i = 0; i < ids.size(); i++) {
            longArray[i] = ids.get(i);
        }

		Connection con = null;
		OraclePreparedStatement pStmt = null;
		OracleResultSet rs = null;
		
		try {
			con = DriverManager.getConnection(cpmsDS.getUrl(), cpmsDS.getUsername(), cpmsDS.getPassword());
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor("IKR_TABLE_NUMBER", con);
			ARRAY array_to_pass = new ARRAY(desc, con, longArray);
			pStmt = (OraclePreparedStatement)con.prepareStatement(query.toUpperCase());
			pStmt.setARRAY(1, array_to_pass);
			rs = (OracleResultSet)pStmt.executeQuery();	
			while (rs.next()) {
				AbstractIkrDefinition ikrDefinition = mapIkrDefiniton(rs);
				 if (ikrDefinition != null) {
					 res.put(ikrDefinition.getId(),ikrDefinition);
				 }
			}
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	
		return res;	
	}

	@Override
	public Collection<Long> getLastIkrValueIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM IKR_VALUE WHERE ROWNUM<? ORDER BY CAPTURE_TIME DESC";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,nbIds);

			rs = pStmt.executeQuery();

			while (rs.next()) {      
				res.add(rs.getLong("ID"));
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			

		return res;
	}
	
	public void cleanIkrValues(Date beforeDate) throws Exception {
		Connection con = null;
        PreparedStatement pStmt = null;
        
        String query = "DELETE FROM IKR_VALUE WHERE CAPTURE_TIME<=TO_DATE(?,?)";
        
        try {
            con = dataSource.getConnection();
            pStmt = con.prepareStatement(query.toUpperCase());
            pStmt.setString(1, DateUtil.getDate(beforeDate));
			pStmt.setString(2,  DateUtil.getOracleSQLDateDefaultPattern());
			pStmt.executeUpdate();           
        } catch(SQLException e) {
        	e.printStackTrace();
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }			
	}
	
	@Override
	public Collection<Long> getLastIkrDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM IKR_DEFINITION WHERE ROWNUM<?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,nbIds);

			rs = pStmt.executeQuery();

			while (rs.next()) {      
				res.add(rs.getLong("ID"));
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			

		return res;
	}
	
	protected Collection<Long> getCrossCompute(long defId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		if (defId == 6874)
			System.out.println("");
		
		String query = "SELECT DEF1.ID as TRIG,DEF2.ID as COMPUTED, DEF2.CROSS_COMPUTE as FORMULA FROM IKR_DEFINITION DEF1 INNER JOIN IKR_DEFINITION DEF2"
					 + " ON INSTR(DEF2.CROSS_COMPUTE, CONCAT('M',DEF1.ID))>0 AND DEF1.ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
		    con = dataSource.getConnection();
		
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setLong(1,defId);
		    rs = pStmt.executeQuery();
		    
		    while (rs.next()) {    
		    	long trig = rs.getLong("TRIG");
		    	long computed = rs.getLong("COMPUTED");
		    	
		    	String formula = rs.getString("FORMULA");
		    	
				Pattern p = Pattern.compile("M\\d+");
				Matcher m = p.matcher(formula);
				boolean b = false;
				while(b = m.find()) {
					String formulaId = m.group();
					
					String defIdStr = formulaId.substring(1);
					long tmpDefId = Long.valueOf(defIdStr);
					
					if (tmpDefId == trig) {
						res.add(computed);
					}
				}
		    }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
		return res;
	}	
	
	public long createCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition)
	throws PersistenceException {	
		long res = 0;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "INSERT INTO IKR_DEFINITION (LOGICAL_ENV_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,CROSS_COMPUTE,ENABLE,MONITOR_ID) VALUES (?,?,?,?,?,?,?)";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, crossComputeDefinition.getLogicalEnvId());
			pStmt.setInt(2, crossComputeDefinition.getIkrCategoryId());
			pStmt.setString(3, crossComputeDefinition.getIkrInstance());
			pStmt.setString(4, crossComputeDefinition.getIkrCompute().name());
			pStmt.setString(5, crossComputeDefinition.getCrossComputation());
			pStmt.setBoolean(6, crossComputeDefinition.isActivated());
			pStmt.setLong(7, 0);

			pStmt.executeUpdate();
			
            res = getLongMaxId("IKR_DEFINITION", "MONITOR_ID = 0", con);

		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
		return res;
	}
	
	public long createStaticDataDefinition(StaticData staticData)
	throws PersistenceException {	
		long res = 0;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "INSERT INTO IKR_DEFINITION (LOGICAL_ENV_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,SD_VALUE,ENABLE,MONITOR_ID) VALUES (?,?,?,?,?,?,?)";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, staticData.getLogicalEnvId());
			pStmt.setInt(2, staticData.getIkrCategoryId());
			pStmt.setString(3, staticData.getIkrInstance());
			pStmt.setString(4, staticData.getIkrCompute().name());
			pStmt.setString(5, staticData.getValue());
			pStmt.setBoolean(6, staticData.isActivated());
			pStmt.setLong(7, -1);

			pStmt.executeUpdate();
			
            res = getLongMaxId("IKR_DEFINITION", "MONITOR_ID = -1", con);

		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
		return res;
	}	
	
	public long createIkrDefinition(IkrDefinition ikrDefinition) 
	throws PersistenceException{
		
		long res = 0;
		 	
		Connection con = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        String query = "INSERT INTO IKR_DEFINITION (MONITOR_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,ENABLE,LOGICAL_ENV_ID) VALUES (?,?,?,?,?,?)";
        try {
            con = dataSource.getConnection();

            pStmt = con.prepareStatement(query.toUpperCase());            
            pStmt.setLong(1, ikrDefinition.getMonitorId());
        	pStmt.setInt(2, ikrDefinition.getIkrCategoryId());
        	pStmt.setString(3, ikrDefinition.getIkrInstance());
        	
        	String metricComputeName = ikrDefinition.getIkrCompute() == null ? null : ikrDefinition.getIkrCompute().name();
        	pStmt.setString(4, metricComputeName);
        	
        	pStmt.setBoolean(5, ikrDefinition.isActivated());
        	pStmt.setInt(6,0);
        	pStmt.executeUpdate(); 
        	
        	res = getLongMaxId("IKR_DEFINITION", "MONITOR_ID NOT IN (0, -1)", con);
        	
        } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }	
        
        return res;
	}
}
