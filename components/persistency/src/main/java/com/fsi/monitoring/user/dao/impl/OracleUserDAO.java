package com.fsi.monitoring.user.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.user.Role;




public class OracleUserDAO 
extends AbstractUserDAO{
	public long createRole(Role role) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		ResultSet rs = null;  
		
		long res = 0;
	    
	    String query = "INSERT INTO ROLE (NAME,DESCRIPTION) VALUES (?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,role.getName());
	        pStmt.setString(2,role.getDescription());
	        
            pStmt.executeUpdate();
            
            int roleId = getIntMaxId("ROLE", con);
            
            insertRoleAccessPerms(roleId,role.getAccessPermIds());
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        return res;
	}
}
