package com.fsi.monitoring.report.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.report.ReportConfig;
import com.fsi.monitoring.report.ReportType;
import com.fsi.monitoring.report.dao.ReportDAO;

public abstract class AbstractReportDAO
extends AbstractDataSourceDAO 
implements ReportDAO {
	
	public List<ReportType> getReportTypes()
	throws PersistenceException {
		List<ReportType> res = new ArrayList<ReportType>();
			
		String query = "SELECT * FROM REPORT_TYPE";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ID");
          	String name = rs.getString("NAME");
          	String description = rs.getString("DESCRIPTION");       	
          	
          	ReportType reportType = new ReportType(id,
          										   name,
          										   description);
          								
          	res.add(reportType);
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
	
	public List<ReportConfig> getReportConfigs(long reportTypeId)
	throws PersistenceException {
		List<ReportConfig> res = new ArrayList<ReportConfig>();
			
		String query = "SELECT * FROM REPORT_CONFIG WHERE REPORT_TYPE_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase()); 
          pStmt.setLong(1, reportTypeId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ID");
          	String name = rs.getString("NAME");
          	String description = rs.getString("DESCRIPTION");       	
          	
          	ReportConfig reportConfig = new ReportConfig(id,
          												 name,
          												 description);
          								
          	res.add(reportConfig);
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
