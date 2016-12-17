package com.fsi.monitoring.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;

public abstract class AbstractDAO {	
	
	protected void rollbackConnection(Connection con) throws PersistenceException {		
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}	

	protected void closeResultSet(ResultSet rs) throws PersistenceException {		
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}
	
	protected void closeStatement(Statement stmt) throws PersistenceException  {
		try {
			if (stmt !=null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}	
	
	protected void closeConnection(Connection con) throws PersistenceException  {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}
	
	public String getWhereClause(String idLabel, Collection ids) {
		StringBuffer whereClause = new StringBuffer();
		
		if (ids != null && !ids.isEmpty()) {	
			whereClause.append(" WHERE ");
			whereClause.append(idLabel);
			whereClause.append(" IN (");		
			for (Object id : ids) {
				whereClause.append(id);
				whereClause.append(",");
			}
			whereClause.deleteCharAt(whereClause.length()-1);
			whereClause.append(") ");
		}		
		
		return whereClause.toString();
	}
	
	public String getWhereInClause(Collection ids) {
		StringBuffer whereClause = new StringBuffer();
		if (ids != null && !ids.isEmpty()) {	
			whereClause.append("IN (");		
			for (Object id : ids) {
				whereClause.append(id);
				whereClause.append(',');
			}
			whereClause.deleteCharAt(whereClause.length()-1);
			whereClause.append(')');
		}		
		
		return whereClause.toString();
	}
}
