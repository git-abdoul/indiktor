package com.fsi.monitoring.indiktor.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.fwk.util.DateUtil;

public class MySQLMonitorDAO extends AbstractMonitorDAO {

	public void cleanIkrValues(Date beforeDate) throws Exception {
		Connection con = null;
        PreparedStatement pStmt = null;
        
        String query = "DELETE FROM IKR_VALUE WHERE CAPTURE_TIME<=?";
        
        try {
            con = dataSource.getConnection();
            pStmt = con.prepareStatement(query.toUpperCase());
            pStmt.setString(1, DateUtil.getDate(beforeDate));
            pStmt.executeUpdate();           
        } catch(SQLException e) {
        	e.printStackTrace();
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }			
	}
	
}
