package com.fsi.monitoring.query.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;


public class OracleQueryDAO extends AbstractQueryDAO{
	
	public Collection<Long> getIkrDefinitionIdsByIkrInstance(long monitorId, 
		   		 String ikrCategoryGroup,
		   		 String ikrInstance,
		   		 String ikrEnv) 
		   		 throws PersistenceException {		
		
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT def.ID FROM IKR_DEFINITION def, IKR_CATEGORY cat "
						+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND def.MONITOR_ID=? AND cat.IKR_CATEGORY_GROUP=? AND def.IKR_INSTANCE=? AND def.IKR_ENV=?";
		
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
					+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND def.MONITOR_ID=? AND cat.IKR_CATEGORY_GROUP=?";

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
					+ "WHERE def.IKR_CATEGORY_ID=cat.ID AND cat.NAME=? AND def.IKR_INSTANCE like ? AND def.IKR_ENV like ?";

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
	
	
}
