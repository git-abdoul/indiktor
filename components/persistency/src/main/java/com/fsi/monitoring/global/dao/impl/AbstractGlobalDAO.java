package com.fsi.monitoring.global.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.global.dao.GlobalDAO;

public class AbstractGlobalDAO
extends AbstractDataSourceDAO
implements GlobalDAO {

	protected final static Logger LOG = Logger.getLogger(AbstractGlobalDAO.class);
	
	public long getNextId(int batchSize, String objectType) 
	throws PersistenceException {
		long res = 0;
		
		Connection con = null;
        PreparedStatement pStmt = null;
        PreparedStatement pStmt2 = null;
        ResultSet rs = null;
        
        String query = "SELECT ID FROM INDIKTOR_ID WHERE OBJECT_TYPE=? FOR UPDATE";
        String query2 = "UPDATE INDIKTOR_ID SET ID=? WHERE OBJECT_TYPE=?";
        
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            
            pStmt = con.prepareStatement(query.toUpperCase());
            pStmt.setString(1, objectType);
            rs = pStmt.executeQuery();
		      
		    if (rs.next()) {       		 
		    	res = rs.getLong("ID");
		    }
		    
		    pStmt2 = con.prepareStatement(query2.toUpperCase());
		    pStmt2.setLong(1, res+batchSize);
		    pStmt2.setString(2, objectType);
		    pStmt2.executeUpdate();
		    
		    con.commit();
		    
        } catch(SQLException e) {
        	rollbackConnection(con);
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeStatement(pStmt2);
            closeConnection(con);
           
        }
        
        return res;
	}
	
	public void updateId(long id, String objectType) throws PersistenceException {
		Connection con = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        String query = "UPDATE INDIKTOR_ID SET ID=? WHERE OBJECT_TYPE=?";
        
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            
            pStmt = con.prepareStatement(query.toUpperCase());
            pStmt.setLong(1, id);
            pStmt.setString(2, objectType);            
            pStmt.executeUpdate();
		    
		    con.commit();
		    
        } catch(SQLException e) {
        	rollbackConnection(con);
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
           
        }
	}
}
