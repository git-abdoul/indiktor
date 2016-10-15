package com.fsi.monitoring.indiktor.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;

public class OracleDataModelDAO extends AbstractDataModelDAO {	
	
	public OracleDataModelDAO() {
		super();
	}

	public long createMonitor(MonitorConfig monitorConfig) throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;	
	    long monitorId = 0;
	    
	    String query = "INSERT INTO MONITOR (NAME,METRIC_DOMAIN_CONFIG_ID,LOGICAL_ENV_ID,STARTED) VALUES (?,?,?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,monitorConfig.getContext());
	        pStmt.setInt(2,monitorConfig.getMetricDomainConfig().getId());
	        pStmt.setInt(3, monitorConfig.getLogicalEnvId());
	        pStmt.setBoolean(4, monitorConfig.isAutoStart());
	        
            pStmt.executeUpdate();
            
            monitorId = getLongMaxId("MONITOR", con);
          
            monitorConfig.setId(monitorId);
            
            if (monitorConfig.getAttributes() != null) {
            	createMonitorAttributes(monitorId, monitorConfig.getAttributes());
            }
            
            if (monitorConfig.getSchedulerConfig() != null)
            	createMonitorScheduler(monitorId, monitorConfig.getSchedulerConfig());
            
            createMonitorBindings(monitorId, monitorConfig.getConnectorConfigIds());
            
            List<MetricDomainConfigResource> resources = monitorConfig.getMetricDomainConfig().getResources();
            List<Integer> ikrCategoriesProvided = null;
            String strIds = monitorConfig.getAttribute(MonitorConfigAttributeKey.CATEGORY_RESOURCE);
            if (strIds!=null&&strIds.length()>0) {
            	String[] ids = strIds.split(",");
            	ikrCategoriesProvided = new ArrayList<Integer>();
            	for (String id : ids) {
            		ikrCategoriesProvided.add(Integer.parseInt(id));
            	}
            }
            for (MetricDomainConfigResource resource : resources) {
            	createMonitorActivities(monitorConfig.getId(), resource.getResource().getId(),ikrCategoriesProvided);
            }
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        return monitorId;
	}
	
	public void deleteMonitor(long monitorId)
	throws PersistenceException {
		
		MonitorConfig config = loadMonitorConfig(monitorId);
		if (config.getSchedulerConfig()!=null)
			deleteMonitorSchedulerTimes(config.getSchedulerConfig().getId());
		
		String query = "DELETE FROM MONITOR WHERE ID=?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,monitorId);
			pStmt.executeUpdate();			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
	public void deleteJobScheduler(int taskId) throws PersistenceException {
		
		IkrJobSchedulerConfig config = getJobSchedulerConfig(taskId);
		if (config !=null)
			deleteJobSchedulerTimes(config.getId());
		
		String query = "DELETE FROM JOB_SCHEDULER WHERE ID=?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();			
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1,taskId);
			pStmt.executeUpdate();		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	public void createMonitorScheduler(long monitorId, IkrMonitorSchedulerConfig config)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
	    int schedId = 0;
		String query = "INSERT INTO MONITOR_SCHEDULER (MONITOR_ID,CAPTURE_DELAY,SCHEDULING_TYPE,SCHEDULING_MODE) VALUES (?,?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,monitorId);
	        pStmt.setLong(2, config.getDelay());
	        pStmt.setString(3,config.getType());
	        pStmt.setString(4,config.getMode());		       
            pStmt.executeUpdate();	    
            
            schedId = getIntMaxId("MONITOR_SCHEDULER", con);
            
            if (schedId > 0) {
	            //save start time
	            Calendar startCal = config.getStartTime();
	            if (startCal != null) {
	            	int day = 0;
	            	if (IkrMonitorSchedulerConfig.WEEKLY.equals(config.getMode()))
	    				day = startCal.get(Calendar.DAY_OF_WEEK);
	    			else if (IkrMonitorSchedulerConfig.MONTHLY.equals(config.getMode()))
	    				day = startCal.get(Calendar.DAY_OF_MONTH);;
	            	createMonitorSchedulerTime(schedId, IkrMonitorSchedulerConfig.START_TIME, day, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE));
	            }
	            
	            //save end time
	            Calendar endCal = config.getEndTime();
	            if (endCal != null) {
	            	int day = 0;
	            	if (IkrMonitorSchedulerConfig.WEEKLY.equals(config.getMode()))
	    				day = endCal.get(Calendar.DAY_OF_WEEK);
	    			else if (IkrMonitorSchedulerConfig.MONTHLY.equals(config.getMode()))
	    				day = endCal.get(Calendar.DAY_OF_MONTH);;
	    				createMonitorSchedulerTime(schedId, IkrMonitorSchedulerConfig.END_TIME, day, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE));
	            }
            }
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }   
	}	
	
	public int createJobScheduler(IkrJobSchedulerConfig config)
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;  		
		int res = 0;	    
		String query = "INSERT INTO JOB_SCHEDULER (NAME,LOGICAL_ENV_ID,JOB_SCHEDULER_STATIC_DOMAIN_ID,SCHEDULING_MODE,DESCRIPTION,ACTIVE) VALUES (?,?,?,?,?,?)";      
		   
		try {
			con = dataSource.getConnection();	        	
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setString(1,config.getName());
		    pStmt.setInt(2,config.getLogicalEnvId());
//		    pStmt.setString(3, config.getAction());
		    pStmt.setInt(3, config.getJobStaticDomainId());
		    pStmt.setString(4,config.getMode());
		    pStmt.setString(5, config.getDescription());
		    pStmt.setBoolean(6, config.isActive());	        
		    pStmt.executeUpdate(); 
		    
		    res = getIntMaxId("JOB_SCHEDULER", con);		    
		    config.setId(res);        
		    
		    if (config.getAttributes() != null) {
		    	createJobSchedulerAttributes(res, config.getAttributes());
		    }
		    
		    createJobSchedulerTimes(config);
		    
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
		    closeStatement(pStmt);
		    closeConnection(con);
		}
		
		return res;
	}
	
	public void deleteIkrStaticDomain(int id, int level) throws PersistenceException {
		Connection con = null;
		PreparedStatement selectPStmt = null;	
		PreparedStatement deletePStmt = null;
		ResultSet rs = null;
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		try {
			con = dataSource.getConnection();
			switch(level) {
				case IKR_CATEGORY_LEVEL-2:
					String query = "select metricDom.ID as DOM_ID, metricCat.ID as CAT_Id FROM IKR_STATIC_DOMAIN domType" +
							" LEFT JOIN IKR_STATIC_DOMAIN metricDom on metricDom.PARENT_ID=domType.ID" +
							" LEFT JOIN IKR_STATIC_DOMAIN metricCat on metricCat.PARENT_ID=metricDom.ID" +
							" WHERE domType.ID=?";
					selectPStmt = con.prepareStatement(query.toUpperCase());
					selectPStmt.setInt(1,id);
					rs = selectPStmt.executeQuery();
					while (rs.next()) {   
						int domId = rs.getInt("DOM_ID");
						int catId = rs.getInt("CAT_Id");
						if (!ids.contains(domId))
							ids.add(domId);
			        	ids.add(catId);
			        }						
					break;
					
				case IKR_CATEGORY_LEVEL-1:
					selectPStmt = con.prepareStatement("SELECT ID FROM IKR_STATIC_DOMAIN WHERE PARENT_ID=?");
					selectPStmt.setInt(1,id);
					rs = selectPStmt.executeQuery();
			        while (rs.next()) {       	          	
			        	ids.add(rs.getInt("ID"));
			        }				
					break;	
			}			
			
			String query ="DELETE FROM IKR_STATIC_DOMAIN " + getWhereClause("ID", ids);  
			deletePStmt = con.prepareStatement(query.toUpperCase());
			deletePStmt.executeUpdate();				
			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(selectPStmt);
			closeStatement(deletePStmt);
			closeConnection(con);
		}	
	}
	
	public List<Integer> getLastIkrStaticDomainIds(int maxSize) throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		String query = "SELECT ID FROM IKR_STATIC_DOMAIN WHERE ROWNUM<?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,maxSize);
			rs = pStmt.executeQuery();

			while (rs.next()) {      
				res.add(rs.getInt("ID"));
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

	public List<Long> getLastMonitorConfigIds(int maxSize) throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM MONITOR WHERE ROWNUM<?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,maxSize);
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

	public List<Integer> getLastLogicalenvIds(int maxSize) 	throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		
		String query = "SELECT ID FROM LOGICAL_ENV WHERE ROWNUM<?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,maxSize);
			rs = pStmt.executeQuery();

			while (rs.next()) {      
				res.add(rs.getInt("ID"));
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

	public List<Integer> getLastConnectorConfigIds(int maxSize) throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		
		String query = "SELECT ID FROM CONNECTOR_CONFIG WHERE ROWNUM<?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,maxSize);
			rs = pStmt.executeQuery();

			while (rs.next()) {      
				res.add(rs.getInt("ID"));
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
