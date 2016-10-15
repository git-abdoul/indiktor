package com.fsi.monitoring.histo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.fwk.util.DateUtil;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class MySQLHistoDAO
extends AbstractHistoDAO {
	
	public Map<Long, List<IkrValue>> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		Map<Long, List<IkrValue>> res = new HashMap<Long, List<IkrValue>>();
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {
			String query = "SELECT CAPTURE_TIME, IKR_CATEGORY_ID, IKR_DEFINITION_ID, VALUE FROM IKR_VALUE val, IKR_DEFINITION def " +
							"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
							" AND CAPTURE_TIME>=?";		
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=?";
	
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
	
			try {
				con = dataSource.getConnection();
				pStmt = con.prepareStatement(query.toUpperCase());
				pStmt.setString(1, DateUtil.getDate(fromDate));
				if (toDate != null)
					pStmt.setString(2, DateUtil.getDate(toDate));
				rs = pStmt.executeQuery();
	
				while (rs.next()) {      
					Date captureTime = new Date(rs.getTimestamp("CAPTURE_TIME").getTime());
					long ikrDefinitionId = rs.getLong("IKR_DEFINITION_ID");
					String valueStr = rs.getString("VALUE");
					int ikrCategoryId = rs.getInt("IKR_CATEGORY_ID");
					
					List<IkrValue> values = res.get(ikrDefinitionId);
					if(values == null) {
						values = new ArrayList<IkrValue>();
						res.put(ikrDefinitionId, values);
					}
					
					IkrValue ikrValue = new IkrValue();
					ikrValue.setCaptureTime(captureTime);
					ikrValue.setIkrCategoryId(ikrCategoryId);
					ikrValue.setIkrDefinitionId(ikrDefinitionId);
					ikrValue.setValue(valueStr);
					values.add(ikrValue);
				}
			} catch(SQLException e) {
				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
			} finally {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			}			
		}
		return res;
	}	
	
	public Collection<Long> getIkrValueIds(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {		
			String query = "SELECT val.ID FROM IKR_VALUE val, IKR_DEFINITION def " +
							"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
							" AND CAPTURE_TIME>=?";	
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=?";
	
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
			
			try {
				con = dataSource.getConnection();
				pStmt = con.prepareStatement(query.toUpperCase());
				pStmt.setString(1, DateUtil.getDate(fromDate));
				if (toDate != null)
					pStmt.setString(2, DateUtil.getDate(toDate));
				rs = pStmt.executeQuery();
	
				while (rs.next()) {      
					long id = rs.getLong("val.ID");
					res.add(id);
				}
			} catch(SQLException e) {
				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
			} finally {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			}			
		}
		return res;
	}	
	
	public Collection<IkrValue> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate, int prefetch) throws PersistenceException {
		Collection<IkrValue> res = new ArrayList<IkrValue>();
		
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {		
			String query = "SELECT val.ID, CAPTURE_TIME, IKR_CATEGORY_ID, IKR_DEFINITION_ID, VALUE FROM IKR_VALUE val, IKR_DEFINITION def " +
							"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
							" AND CAPTURE_TIME>=?";		
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=?";
	
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
			
			try {
				con = dataSource.getConnection();
				pStmt = con.prepareStatement(query.toUpperCase());
				pStmt.setString(1, DateUtil.getDate(fromDate));
				if (toDate != null)
					pStmt.setString(2, DateUtil.getDate(toDate));
				rs = pStmt.executeQuery();
	
				while (rs.next()) {      
					long id = rs.getLong("val.ID");
					Date captureTime = new Date(rs.getTimestamp("CAPTURE_TIME").getTime());
					long ikrDefinitionId = rs.getLong("IKR_DEFINITION_ID");
					String valueStr = rs.getString("VALUE");
					int ikrCategoryId = rs.getInt("IKR_CATEGORY_ID");
					
					IkrValue ikrValue = new IkrValue();
					ikrValue.setId(id);
					ikrValue.setCaptureTime(captureTime);
					ikrValue.setIkrCategoryId(ikrCategoryId);
					ikrValue.setIkrDefinitionId(ikrDefinitionId);
					ikrValue.setValue(valueStr);
					res.add(ikrValue);
				}
			} catch(SQLException e) {
				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
			} finally {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			}			
		}
		return res;
	}

	public List<Long> getLastIkrValuesId(long ikrDefinitionId, int nbOfValues) throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		String query = "SELECT val.ID, IKR_DEFINITION_ID FROM IKR_VALUE val, IKR_DEFINITION def " +
						"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID = ? " +
						"ORDER BY CAPTURE_TIME DESC " + 
						"LIMIT ?";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, ikrDefinitionId);
			pStmt.setInt(2, nbOfValues);
			rs = pStmt.executeQuery();

			while (rs.next()) {      
				long id = rs.getLong("val.ID");				
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
