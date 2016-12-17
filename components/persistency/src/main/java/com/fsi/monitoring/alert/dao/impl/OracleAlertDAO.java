package com.fsi.monitoring.alert.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;



public class OracleAlertDAO extends AbstractAlertDAO {	
	
	@Override
	public Collection<Long> getLastAlertDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM ALERT_DEFINITION WHERE ROWNUM<?";
		
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

}
