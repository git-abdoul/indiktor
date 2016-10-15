package com.fsi.monitoring.histo.dao.impl;

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

import oracle.jdbc.driver.OraclePreparedStatement;
import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.fwk.util.DateUtil;
import com.fsi.monitoring.kpi.metrics.IkrValue;


public class OracleHistoDAO extends AbstractHistoDAO {
	
	public Map<Long, List<IkrValue>> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		Map<Long, List<IkrValue>> res = new HashMap<Long, List<IkrValue>>();
		
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {
			
			if (ikrDefinitionIds.size()>=1000)
				return getIkrValuesBulkIds(ikrDefinitionIds, fromDate, toDate);
			
			String query = "SELECT CAPTURE_TIME, IKR_CATEGORY_ID, IKR_DEFINITION_ID, VALUE FROM IKR_VALUE val, IKR_DEFINITION def " +
						   "WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
						   " AND CAPTURE_TIME>=TO_DATE(?,?)";	
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=TO_DATE(?,?)";
			
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
			
			try {
				con = dataSource.getConnection();
				pStmt = con.prepareStatement(query.toUpperCase());
				pStmt.setString(1, DateUtil.getDate(fromDate));
				pStmt.setString(2,  DateUtil.getOracleSQLDateDefaultPattern());
				if (toDate != null) {
					pStmt.setString(3, DateUtil.getDate(toDate));
					pStmt.setString(4,  DateUtil.getOracleSQLDateDefaultPattern());
				}
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
	
	public Map<Long, List<IkrValue>> getIkrValuesBulkIds(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		Map<Long, List<IkrValue>> res = new HashMap<Long, List<IkrValue>>();
		String query = "SELECT CAPTURE_TIME, IKR_CATEGORY_ID, IKR_DEFINITION_ID, VALUE FROM IKR_VALUE val, IKR_DEFINITION def " +
						"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID IN (SELECT * FROM TABLE (SELECT CAST(? AS IKR_TABLE_NUMBER) FROM DUAL)) AND CAPTURE_TIME>=TO_DATE(?,?)";	
		
		if (toDate != null)
			query = query + " AND CAPTURE_TIME<=TO_DATE(?,?)";
		
		Connection con = null;
		OraclePreparedStatement pStmt = null;
		OracleResultSet rs = null;
		
		long longArray[] = new long[ikrDefinitionIds.size()];
        for(int i = 0; i < ikrDefinitionIds.size(); i++) {
            longArray[i] = ikrDefinitionIds.get(i);
        }
		
		try {
			con = DriverManager.getConnection(cpmsDS.getUrl(), cpmsDS.getUsername(), cpmsDS.getPassword());
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor("IKR_TABLE_NUMBER", con);
			ARRAY array_to_pass = new ARRAY(desc, con, longArray);
			pStmt = (OraclePreparedStatement)con.prepareStatement(query.toUpperCase());
			pStmt.setARRAY(1, array_to_pass);
			pStmt.setString(2, DateUtil.getDate(fromDate));
			pStmt.setString(3,  DateUtil.getOracleSQLDateDefaultPattern());
			if (toDate != null) {
				pStmt.setString(4, DateUtil.getDate(toDate));
				pStmt.setString(5,  DateUtil.getOracleSQLDateDefaultPattern());
			}
			rs = (OracleResultSet)pStmt.executeQuery();
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
		return res;
	}	
	
	public Collection<Long> getIkrValueIds(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {
			String query = "SELECT ID FROM IKR_VALUE " +
							"WHERE IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
							" AND CAPTURE_TIME>=TO_DATE(?,?)";	
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=TO_DATE(?,?)";
	
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
			
			try {
				con = dataSource.getConnection();
				pStmt = con.prepareStatement(query.toUpperCase());
				pStmt.setString(1, DateUtil.getDate(fromDate));
				pStmt.setString(2,  DateUtil.getOracleSQLDateDefaultPattern());
				if (toDate != null) {
					pStmt.setString(3, DateUtil.getDate(toDate));
					pStmt.setString(4,  DateUtil.getOracleSQLDateDefaultPattern());
				}
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
		}
		return res;
	}	
	
	public Collection<IkrValue> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate, int prefetch) throws PersistenceException {
		Collection<IkrValue> res = new ArrayList<IkrValue>();
		
		if (ikrDefinitionIds!=null && ikrDefinitionIds.size()>0) {		
			String query = "SELECT CAPTURE_TIME, IKR_CATEGORY_ID, IKR_DEFINITION_ID, VALUE FROM IKR_VALUE val, IKR_DEFINITION def " +
							"WHERE val.IKR_DEFINITION_ID=def.ID AND val.IKR_DEFINITION_ID " + getWhereInClause(ikrDefinitionIds) +
							" AND CAPTURE_TIME>=TO_DATE(?,?)";	
			
			if (toDate != null)
				query = query + " AND CAPTURE_TIME<=TO_DATE(?,?)";
	
			Connection con = null;
			PreparedStatement pStmt = null;
			ResultSet rs = null;
			
			try {
				con = dataSource.getConnection();
				System.out.println("OracleHistoDAO  -- Execute Query Start");
				long dt0 = System.currentTimeMillis();
				pStmt = con.prepareStatement(query.toUpperCase());		
				pStmt.setString(1, DateUtil.getDate(fromDate));
				pStmt.setString(2,  DateUtil.getOracleSQLDateDefaultPattern());
				if (toDate != null) {
					pStmt.setString(3, DateUtil.getDate(toDate));
					pStmt.setString(4,  DateUtil.getOracleSQLDateDefaultPattern());
				}
				pStmt.setFetchSize(prefetch);
				rs = pStmt.executeQuery();
				long dt1 = System.currentTimeMillis();				
				System.out.println("OracleHistoDAO  -- Execute Query End - " + (dt1 - dt0));
				System.out.println("OracleHistoDAO  -- ResulstSet RowCount - " + rs.getRow());
				System.out.println("OracleHistoDAO  -- prefetch " + pStmt.getFetchSize());				
				System.out.println("OracleHistoDAO  -- Build Object Start");
				while (rs.next()) {      
					IkrValue ikrValue = new IkrValue();
					ikrValue.setCaptureTime(new Date(rs.getTimestamp("CAPTURE_TIME").getTime()));
					ikrValue.setIkrCategoryId(rs.getInt("IKR_CATEGORY_ID"));
					ikrValue.setIkrDefinitionId(rs.getLong("IKR_DEFINITION_ID"));
					ikrValue.setValue(rs.getString("VALUE"));
					res.add(ikrValue);
				}
				long dt2 = System.currentTimeMillis();
				System.out.println("OracleHistoDAO  -- Build Object End - " + (dt2 - dt0));
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
		String query = "SELECT ID, IKR_DEFINITION_ID FROM IKR_VALUE " +
						"WHERE IKR_DEFINITION_ID = ? AND ROWNUM < ? ORDER BY CAPTURE_TIME DESC " ;		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			int nb = nbOfValues + 1;
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, ikrDefinitionId);
			pStmt.setInt(2, nb);
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
