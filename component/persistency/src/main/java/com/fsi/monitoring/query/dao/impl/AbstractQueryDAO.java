package com.fsi.monitoring.query.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.query.dao.QueryDAO;

public abstract class AbstractQueryDAO 
extends AbstractDataSourceDAO 
implements QueryDAO {	

	protected final static Logger LOG = Logger.getLogger(AbstractQueryDAO.class);	
	
	public Collection<Long> getAlertDefinitions(long ikrDefinitionId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT ALERT_DEFINITION_ID FROM ALERT_CONDITION cond, ALERT_DEFINITION def "
						+ " WHERE def.ID=ALERT_DEFINITION_ID AND def.ENABLE=? AND cond.IKR_DEFINITION_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          pStmt.setBoolean(1, true);
          pStmt.setLong(2, ikrDefinitionId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ALERT_DEFINITION_ID");					
          	res.add(id);
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
	
	public Collection<Long> getMonitorIdsByAgent(long agentId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT ID FROM MONITOR WHERE AGENT_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          pStmt.setLong(1, agentId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ID");					
          	res.add(id);
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
	
	public Collection<Long> getMonitorIdsByLogicalEnv(int logicalEnvId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT ID FROM MONITOR WHERE LOGICAL_ENV_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          pStmt.setInt(1, logicalEnvId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ID");				
          	res.add(id);
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
	
	public Collection<Long> getAlertDefinitionIds()
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT ID FROM ALERT_DEFINITION";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ID");					
          	res.add(id);
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
	
	public Collection<Integer> getIkrCategoryIdsByGroup(String ikrCategoryGroup)
	throws PersistenceException {
		Collection<Integer> res = new ArrayList<Integer>();			
		String query = "SELECT ID FROM IKR_CATEGORY WHERE IKR_CATEGORY_GROUP=?";		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          pStmt.setString(1, ikrCategoryGroup);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	int id = rs.getInt("ID");
     								
          	res.add(id);
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
	
	public Collection<Integer> getIkrCategoryIds()
	throws PersistenceException {	
	

		Collection<Integer> res = new ArrayList<Integer>();
			
		String query = "SELECT DISTINCT ID FROM IKR_CATEGORY";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	int id = rs.getInt("ID");					
          	res.add(id);
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
	
	public Collection<Long> getIkrDefinitionIdsByIkrInstance(long monitorId, 
		 	   										   		 String ikrCategoryGroup,
		 	   										   		 String ikrInstance,
		 	   										   		 String ikrEnv) 
	throws PersistenceException {


		Collection<Long> res = new ArrayList<Long>();

		String query = "SELECT DISTINCT def.ID FROM IKR_DEFINITION def, IKR_CATEGORY cat "
					+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND def.MONITOR_ID=? AND cat.IKR_CATEGORY_GROUP=? AND def.IKR_INSTANCE=? AND def.IKR_ENV=? ORDER BY IKR_INSTANCE, IKR_ENV";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1, monitorId);
			pStmt.setString(2, ikrCategoryGroup);
			pStmt.setString(3, ikrInstance);
			pStmt.setString(4, ikrEnv);
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				long id = rs.getLong("ID");
				res.add(id);
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

	public Collection<Long> getIkrDefinitionIds(long monitorId, String ikrCategoryGroup) 
	throws PersistenceException {


		Collection<Long> res = new ArrayList<Long>();

		String query = "SELECT DISTINCT def.ID FROM IKR_DEFINITION def, IKR_CATEGORY cat "
					+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND def.MONITOR_ID=? AND cat.IKR_CATEGORY_GROUP=? ORDER BY IKR_INSTANCE, IKR_ENV";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1, monitorId);
			pStmt.setString(2, ikrCategoryGroup);
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				long id = rs.getLong("ID");
				res.add(id);
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
	
	public Collection<Long> getIkrDefinitionIds(String ikrCategory, 
												String ikrInstance,
												String ikrEnv) 
	throws PersistenceException {


		Collection<Long> res = new ArrayList<Long>();

		String query = "SELECT DISTINCT def.ID FROM IKR_DEFINITION def, IKR_CATEGORY cat "
					+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND cat.NAME=? AND def.IKR_INSTANCE like ? AND def.IKR_ENV like ? ORDER BY IKR_INSTANCE, IKR_ENV";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setString(1, ikrCategory);
			pStmt.setString(2, "%" + ikrInstance + "%");
			pStmt.setString(3, "%" + ikrEnv + "%");
			rs = pStmt.executeQuery();
		
			while (rs.next()) {       	
				long id = rs.getLong("ID");
				res.add(id);
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
	
	public Collection<Long> getIkrDefinitionIds() 
	throws PersistenceException {


		Collection<Long> res = new ArrayList<Long>();

		String query = "SELECT DISTINCT def.ID FROM IKR_DEFINITION";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				long id = rs.getLong("ID");
				res.add(id);
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
}
