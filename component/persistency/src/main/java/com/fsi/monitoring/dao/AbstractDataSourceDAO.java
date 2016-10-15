package com.fsi.monitoring.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fsi.monitoring.indiktor.dao.MonitorDAO;


public abstract class AbstractDataSourceDAO 
extends AbstractDAO {
	
	protected DataSource dataSource = null;	
	protected DriverManagerDataSource cpmsDS;
	
	public void setCpmsDS(DriverManagerDataSource cpmsDS) {
		this.cpmsDS = cpmsDS;
	}	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}	
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	protected long getLongMaxId(String objectTable, String whereclause, Connection con) {
		String query = "SELECT MAX(ID) AS ID FROM " + objectTable;
		if (whereclause!=null && whereclause.length()>0){
			query = query + " WHERE " + whereclause;
		}
		
		PreparedStatement pStmt = null;
		long maxId = 0;
		try {
			pStmt = con.prepareStatement(query.toUpperCase()); 
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {    
				maxId = rs.getLong("ID");
			}		
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return maxId;		
	}
	
	protected long getLongMaxId(String objectTable, Connection con) {
		String query = "SELECT MAX(ID) AS ID FROM " + objectTable;
		PreparedStatement pStmt = null;
		long maxId = 0;
		try {
			pStmt = con.prepareStatement(query.toUpperCase()); 
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {    
				maxId = rs.getLong("ID");
			}		
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return maxId;		
	}
	
	protected int getIntMaxId(String objectTable, Connection con) {
		String query = "SELECT MAX(ID) AS ID FROM " + objectTable;
		PreparedStatement pStmt = null;
		int maxId = 0;
		try {
			pStmt = con.prepareStatement(query.toUpperCase()); 
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {    
				maxId = rs.getInt("ID");
			}		
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return maxId;		
	}
}
