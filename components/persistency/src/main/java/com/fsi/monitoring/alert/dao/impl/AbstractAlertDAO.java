package com.fsi.monitoring.alert.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertCompute;
import com.fsi.monitoring.alert.AlertComputeResolution;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertDomain;
import com.fsi.monitoring.alert.AlertGroup;
import com.fsi.monitoring.alert.AlertSubDomain;
import com.fsi.monitoring.alert.AlertValidity;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.alert.action.AbstractAlertAction;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.condition.ValueAlertCondition;
import com.fsi.monitoring.alert.dao.AlertDAO;
import com.fsi.monitoring.alert.workflow.AlertCommentEvent;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.snmp.SnmpConfig;
import com.fsi.monitoring.utils.IkrInputCalendar;


public abstract class AbstractAlertDAO 
extends AbstractDataSourceDAO 
implements AlertDAO {

	protected final static Logger LOG = Logger.getLogger(AbstractAlertDAO.class);	
	
	protected AbstractAlertDAO() {}

	public Map<String, Integer> getAlertLevels() throws PersistenceException {
		Map<String, Integer> levels = new HashMap<String, Integer>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT NAME, ALERT_VALUE FROM ALERT_LEVEL";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
            rs = pStmt.executeQuery();
            while (rs.next()) {  
 	          	String name = rs.getString("NAME");
 	          	int value = rs.getInt("ALERT_VALUE"); 	          	
 	          	levels.put(name, value);
            }
	    }catch(Exception e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }
		return levels;
	}
	
	public Collection<Long> loadAlertDefinitionIds() throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM ALERT_DEFINITION";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
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
	
	public Collection<Long> getAlertDefinitions(long ikrDefinitionId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DISTINCT ALERT_DEFINITION_ID FROM ALERT_CONDITION cond, ALERT_DEFINITION def "
						+ " WHERE def.ID=ALERT_DEFINITION_ID AND def.ENABLE=? AND cond.IKR_DEFINITION_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          pStmt.setBoolean(1, true);
          pStmt.setLong(2, ikrDefinitionId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
          	long id = rs.getLong("ALERT_DEFINITION_ID");					
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
	
	
	public Collection<Long> getLastAlertDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM ALERT_DEFINITION LIMIT ?";
		
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
	
	public Map<Integer, AlertDomain> getAllAlertDomains() throws PersistenceException {
		Map<Integer, AlertDomain> values = new HashMap<Integer, AlertDomain>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID, ALERT_GROUP_ID, DOMAIN_VALUE FROM ALERT_DOMAIN ";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	int alertGroupId = rs.getInt("ALERT_GROUP_ID");
 	          	String value = rs.getString("DOMAIN_VALUE");        	
 	          	values.put(id, new AlertDomain(id, alertGroupId, value));
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);       
        }
		return values;
	}
	
	public Map<Integer, AlertDomain> getAllAlertDomainsByGroupId(int groupId) 
	throws PersistenceException {
		Map<Integer, AlertDomain> values = new HashMap<Integer, AlertDomain>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID, ALERT_GROUP_ID, DOMAIN_VALUE FROM ALERT_DOMAIN WHERE ALERT_GROUP_ID = ?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1, groupId);
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	int alertGroupId = rs.getInt("ALERT_GROUP_ID");
 	          	String value = rs.getString("DOMAIN_VALUE");        	
 	          	values.put(id, new AlertDomain(id, alertGroupId, value));
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);           
        }
		return values;
	}
	
	public List<String> getAlertEnvs() throws PersistenceException {
		List<String> envs = new ArrayList<String>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT distinct ENVIRONMENT FROM ALERT_DEFINITION";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
            rs = pStmt.executeQuery();
            while (rs.next()) {  
 	          	String name = rs.getString("ENVIRONMENT");        	
 	          	envs.add(name);
            }
	    } catch(SQLException e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);           
        }
		return envs;
	}
	
	public Map<Integer, String> getExistingAlertGroups(int logicalEnvId)
	throws PersistenceException {
		Map<Integer,String> res = new HashMap<Integer,String>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    
	    String query = "SELECT DISTINCT grp.ID,grp.GROUP_VALUE FROM ALERT_DEFINITION al, ALERT_GROUP grp"
	    		+ " WHERE al.ALERT_GROUP_ID=grp.ID";

	    if (logicalEnvId != 0) {		
	    	query += " AND al.LOGICAL_ENV_ID=?";
	    }
	    
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
		    if (logicalEnvId != 0) {	
	        	pStmt.setInt(1, logicalEnvId);
	        }
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
 	          	String value = rs.getString("GROUP_VALUE");        	
 	          	res.put(id,value);
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);        
        }
		return res;
	}	
	
	public  Map<Integer, String> getExistingAlertDomains(int logicalEnvId, int group)
	throws PersistenceException {
		Map<Integer,String> res = new HashMap<Integer,String>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT DISTINCT dom.ID,dom.DOMAIN_VALUE"
	    		+ " FROM ALERT_DEFINITION al, ALERT_GROUP grp,ALERT_DOMAIN dom WHERE dom.ALERT_GROUP_ID=grp.ID"
	    		+ " AND al.ALERT_GROUP_ID=grp.ID AND al.DOMAIN_ID=dom.ID";
	    				
	    if (logicalEnvId != 0) {			
	    	query += " AND al.LOGICAL_ENV_ID=?";
	    }		
	    
	    if (group > 0) {		
	    	query += " AND grp.ID=?";
	    }
	    
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
		    if (logicalEnvId != 0) {			
		    	pStmt.setInt(1, logicalEnvId);
		    }
	        if (logicalEnvId != 0 && group > 0) {		
	        	pStmt.setInt(2, group);
	        } else if (logicalEnvId == 0 && group > 0) {
	        	pStmt.setInt(1, group);
	        }
	
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	String value = rs.getString("DOMAIN_VALUE");        	
 	          	res.put(id,value);
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);          
        }
		return res;
	}
	
	public Map<Integer, String> getExistingAlertSubDomains(int logicalEnvId, int group, int domain)
	throws PersistenceException {
		Map<Integer,String> res = new HashMap<Integer,String>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query= "SELECT DISTINCT sdo.ID,sdo.SUB_DOMAIN_VALUE "
	    	+ " FROM ALERT_DEFINITION al, ALERT_GROUP grp,ALERT_DOMAIN dom,ALERT_SUB_DOMAIN sdo "
	    	+ " WHERE dom.ALERT_GROUP_ID=grp.ID AND sdo.ALERT_DOMAIN_ID=dom.ID"
	    	+ " AND al.ALERT_GROUP_ID=grp.ID AND al.DOMAIN_ID=dom.ID AND al.SUB_DOMAIN_ID=sdo.ID";
	   
	    if (logicalEnvId != 0) {		
	    	query += " AND al.LOGICAL_ENV_ID=?";
	    }	
	    if (group > 0) {		
	    	query += " AND grp.ID=?";
	    }
	    if (domain > 0) {		
	    	query += " AND dom.ID=?";
	    }
	    
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        if (logicalEnvId != 0) {		
	        	pStmt.setInt(1, logicalEnvId);        
	        	if (group > 0) {
	        		pStmt.setInt(2, group);
	        		if (domain > 0) {
	        			pStmt.setInt(3, domain);
	        		}
	        		
	        	} else if (domain > 0) {
	        		pStmt.setInt(2, domain);
	        	}
	        } else {
	        	if (group > 0) {
	        		pStmt.setInt(1, group);
	        		if (domain > 0) {
	        			pStmt.setInt(2, domain);
	        		}
	        	} else if (domain > 0) {
	        		pStmt.setInt(1, domain);
	        	}
	        }
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	String value = rs.getString("SUB_DOMAIN_VALUE");        	
 	          	res.put(id,value);
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);           
        }
		return res;
	}		
	
	public Map<Integer, AlertGroup> getAllAlertGroups()
	throws PersistenceException {
		Map<Integer, AlertGroup> values = new HashMap<Integer, AlertGroup>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID, GROUP_VALUE FROM ALERT_GROUP ";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
 	          	String value = rs.getString("GROUP_VALUE");        	
 	          	values.put(id, new AlertGroup(id, value));
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);           
        }
		return values;
	}
	
	public Map<Integer, AlertSubDomain> getAllAlertSubDomains()
	throws PersistenceException {
		Map<Integer, AlertSubDomain> values = new HashMap<Integer, AlertSubDomain>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID, ALERT_DOMAIN_ID, SUB_DOMAIN_VALUE FROM ALERT_SUB_DOMAIN";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	int alertDomainId = rs.getInt("ALERT_DOMAIN_ID");
 	          	String value = rs.getString("SUB_DOMAIN_VALUE");        	
 	          	values.put(id, new AlertSubDomain(id, alertDomainId, value));
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);            
        }
		return values;
	}
	
	public Collection<AlertSubDomain> getAllAlertSubDomainsByDomainId(int domainId)
	throws PersistenceException {
		Collection<AlertSubDomain> values = new ArrayList<AlertSubDomain>();
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID, ALERT_DOMAIN_ID, SUB_DOMAIN_VALUE FROM ALERT_SUB_DOMAIN WHERE ALERT_DOMAIN_ID=?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1, domainId);
            rs = pStmt.executeQuery();
            while (rs.next()) {  
            	int id = rs.getInt("ID");
            	int alertDomainId = rs.getInt("ALERT_DOMAIN_ID");
 	          	String value = rs.getString("SUB_DOMAIN_VALUE");        	
 	          	values.add(new AlertSubDomain(id, alertDomainId, value));
            }
	    }catch(Exception e) {
        	LOG.error(e.getMessage(), e);
        } finally {
			closeResultSet(rs);
			closeStatement(pStmt);
	        closeConnection(con);          
        }
		return values;
	}

	public Collection<Long> getAlertDefinitionIdsByLabel(String label)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();

		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT ID FROM ALERT_DEFINITION WHERE NAME like ?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1, "%" + label + "%");
            rs = pStmt.executeQuery();
            while (rs.next()) {  
 	          	long id = rs.getLong("ID");
 	          	
 	          	res.add(id);
            }
	    }catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			
		return res;	
	}
	
	public Collection<Long> getAlertDefinitionIdsByLabelAndEnv(String label, String env)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();	
		if(env!=null && env.length() > 0) {		
			Connection con = null;
		    PreparedStatement pStmt = null;
		    ResultSet rs = null;
	
		    String query = "SELECT al.ID FROM ALERT_DEFINITION al, LOGICAL_ENV env WHERE env.ID=al.LOGICAL_ENV_ID AND al.NAME=? AND env.NAME=?";
		    try {
		    	con = dataSource.getConnection();
	
		        pStmt = con.prepareStatement(query.toUpperCase());
		        pStmt.setString(1, label);
		        pStmt.setString(2, env);
	            rs = pStmt.executeQuery();
	            while (rs.next()) {  
	 	          	long id = rs.getLong("ID"); 	          	
	 	          	res.add(id);
	            }
		    }catch(SQLException e) {
	        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
	        } finally {
	        	closeResultSet(rs);
	            closeStatement(pStmt);
	            closeConnection(con);
	        }	
		}
		else {
			res = getAlertDefinitionIdsByLabel(label);
		}
		return res;	
	}
		
	public AlertDefinition getAlertDefinition(long alertDefinitionId) throws PersistenceException {
		AlertDefinition alertDefinition = null;
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT * FROM ALERT_DEFINITION WHERE ID=?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1, alertDefinitionId);
            rs = pStmt.executeQuery();
            
            Map<Long, AlertDefinition> res = new HashMap<Long,AlertDefinition>();
            
            if (rs.next()) {  
 	          	long id = rs.getLong("ID");
 	          	String name = rs.getString("NAME");
 	          	int group = rs.getInt("ALERT_GROUP_ID");
 	          	int domain = rs.getInt("DOMAIN_ID");
 	          	int subdomain = rs.getInt("SUB_DOMAIN_ID");
 	          	int logicalEnvId = rs.getInt("LOGICAL_ENV_ID");
 	          	String description = rs.getString("DESCRIPTION");
 	          	String howTo = rs.getString("HOW_TO");
 	          	boolean enable = rs.getBoolean("ENABLE");
 	          	long raisingDelay = rs.getLong("RAISING_DELAY");
 	          	
 	          	Date creationDate = new Date(rs.getTimestamp("CREATION_DATE").getTime());
 	          	Date lastUpdateDate = new Date(rs.getTimestamp("LAST_UPDATE_DATE").getTime());
 	          	
 	          	alertDefinition = new AlertDefinition(id,
													  name,
													  group,
													  domain,
													  subdomain,
													  logicalEnvId,
													  description,
													  howTo,
													  enable,
													  creationDate,
													  lastUpdateDate,
													  raisingDelay); 	          	
 				
	          	res.put(id, alertDefinition);
	 			loadAlertConditions(res);
	 			loadAlertActions(res);
	 			loadAlertComputes(res);
	 			loadAlertValidities(res);
	 			loadAlertWorkflowFilters(res);
            }
	    }catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			
		return alertDefinition;
	}

	public Map<Long, AlertDefinition> getAlertDefinitions(Collection<Long> ids)
	throws PersistenceException {
		Map<Long, AlertDefinition> res = new HashMap<Long,AlertDefinition>();
		
		String query = "SELECT * FROM ALERT_DEFINITION def";
		
		String whereClause = getWhereClause("def.ID", ids);
		
		query += whereClause;
		
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
 	          	int group = rs.getInt("ALERT_GROUP_ID");
 	          	int domain = rs.getInt("DOMAIN_ID");
 	          	int subdomain = rs.getInt("SUB_DOMAIN_ID");
 	          	int logicalEnvId = rs.getInt("LOGICAL_ENV_ID");
 	          	String description = rs.getString("DESCRIPTION");
 	          	String howTo = rs.getString("HOW_TO");
 	          	boolean enable = rs.getBoolean("ENABLE");
 	          	long raisingDelay = rs.getLong("RAISING_DELAY");
 	          	Date creationDate = new Date(rs.getTimestamp("CREATION_DATE").getTime());
 	          	Date lastUpdateDate = new Date(rs.getTimestamp("LAST_UPDATE_DATE").getTime());
 	                   	
 	          	
 	          	AlertDefinition alertDefinition = new AlertDefinition(id,
																	  name,
																	  group,
																	  domain,
																	  subdomain,
																	  logicalEnvId,
																	  description,
																	  howTo,
																	  enable,
																	  creationDate,
																	  lastUpdateDate,
																	  raisingDelay); 	    
 	          	res.put(id,alertDefinition);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		loadAlertConditions(res);
 		loadAlertActions(res);
 		loadAlertComputes(res);
 		loadAlertValidities(res);
 		loadAlertWorkflowFilters(res);

		return res;
	}	
	
	protected void loadAlertValidities(Map<Long, AlertDefinition> alertDefinitions)
	throws PersistenceException {
		String query = "SELECT * FROM ALERT_VALIDITY";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		Map<Long, Map<Integer, AlertValidity>> validities = new HashMap<Long, Map<Integer,AlertValidity>>();

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());  
			rs = pStmt.executeQuery();
			while (rs.next()) {
				long alertId = rs.getLong("ALERT_DEFINITION_ID");
				int valId = rs.getInt("ALERT_VALIDITY_ID");
				String type = rs.getString("VALIDITY_TIME_TYPE");
				int hour = rs.getInt("VALIDITY_HOUR");
				int min = rs.getInt("VALIDITY_MIN");
				
				IkrInputCalendar inputTime = new IkrInputCalendar();
				inputTime.setHour(hour);
				inputTime.setMinute(min);
				
				Map<Integer, AlertValidity> maps = validities.get(alertId);
				if (maps == null) {
					maps = new HashMap<Integer, AlertValidity>();
					validities.put(alertId, maps);
				}
				
				AlertValidity validity = maps.get(valId);
				if (validity == null) {
					validity = new AlertValidity();
					maps.put(valId, validity);
				}
				
				if ("START".equalsIgnoreCase(type)) {
					validity.setStart(inputTime);
				}
				else if ("END".equalsIgnoreCase(type)) {
					validity.setEnd(inputTime);
				}				
			}			
			
			for (long id : validities.keySet()) {
				AlertDefinition alertDefinition = alertDefinitions.get(id);
				if (alertDefinition != null) {
					Map<Integer, AlertValidity> myValidities = validities.get(id);
					alertDefinition.setAlertValidities(new ArrayList<AlertValidity>(myValidities.values()));
				}
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}
	
	protected void loadAlertConditions(Map<Long, AlertDefinition> alertDefinitions)
	throws PersistenceException {
		String query = "SELECT * FROM ALERT_CONDITION";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());  
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long alertId = rs.getLong("ALERT_DEFINITION_ID");
				
				AlertDefinition alertDefinition = alertDefinitions.get(alertId);
				if (alertDefinition != null) {
				
	 	          	int id = rs.getInt("ID");
	 	          	long ikrDefinitionId = rs.getLong("IKR_DEFINITION_ID");
	 	          	String value = rs.getString("COND_VALUE");
	 	          	String operator = rs.getString("COND_OPERATOR");
	 	          	String unitType = rs.getString("COND_UNIT_TYPE");
	 	          	String unit = rs.getString("COND_UNIT");
	 	          	boolean enable = rs.getBoolean("ENABLE");
	 	          	
	 	          	IkrUnitType ikrUnitType = null;	
	 	          	IkrUnit ikrUnit = null;
	 	           	try {	 	           		
	 	           		ikrUnitType = IkrUnitType.valueOf(unitType);	 	           	
		 	            if (ikrUnitType != null && unit != null) {
		 	            	ikrUnit = ikrUnitType.getIkrUnit(unit);
		 	            }
	 	           	} catch (Exception exc) {
	 	           		LOG.error("Impossible to create category because of wrong IkrUnitType: " + unitType);
	 	           		throw new PersistenceException("Impossible to create category because of wrong IkrUnitType: " + unitType, exc, BaseException.EXCEPTION);
	 	           	}

	 	          	AlertCondition alertCondition = new ValueAlertCondition(id,enable,ikrDefinitionId,value,ikrUnitType,ikrUnit);
	 	          	alertCondition.setOperator(AlertConditionOperator.valueOf(operator));

	 	          	alertDefinition.addAlertCondition(alertCondition);
				}
 	          
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}
	
	
	protected void loadAlertComputes(Map<Long, AlertDefinition> alertDefinitions)
	throws PersistenceException {
		String query = "SELECT * FROM ALERT_COMPUTE";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());  
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long alertDefinitionId = rs.getLong("ALERT_DEFINITION_ID");
				
				AlertDefinition alertDefinition = alertDefinitions.get(alertDefinitionId);
				if (alertDefinition != null) {

					int severity = rs.getInt("SEVERITY");
	 	          	String label = rs.getString("LABEL");
	 	          	String cause = rs.getString("CAUSE");
	 	          	boolean enable = rs.getBoolean("ENABLE");
	 	          	
	 	          	AlertCompute alertCompute = new AlertCompute(AlertWorkflow.getStateBySeverity(severity),
	 	          												 label,
	 	          												 cause,
	 	          												 enable);
	 	          	alertDefinition.addAlertCompute(alertCompute);
				}
 	          
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}		
	
	protected void loadAlertWorkflowFilters(Map<Long, AlertDefinition> alertDefinitions)
	throws PersistenceException {
		String query = "SELECT * FROM ALERT_WORKFLOW_FILTER";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());  
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long alertDefinitionId = rs.getLong("ALERT_DEFINITION_ID");
				
				AlertDefinition alertDefinition = alertDefinitions.get(alertDefinitionId);
				if (alertDefinition != null) {
					String workflow = rs.getString("WORKFLOW");
	 	          	
					AlertWorkflow alertWorkflow = AlertWorkflow.getStateByName(workflow);
	 	          	alertDefinition.addAlertWorkflowFilter(alertWorkflow);
				}
 	          
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}		
	
	
	protected void loadAlertActions(Map<Long, AlertDefinition> alertDefinitions)
	throws PersistenceException {
		String query = "SELECT * FROM ALERT_ACTION";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();
			while (rs.next()) {

				long alertDefinitionId = rs.getLong("ALERT_DEFINITION_ID");

				AlertDefinition alertDefinition = alertDefinitions.get(alertDefinitionId);
				if (alertDefinition != null) {
				
	 	          	int id = rs.getInt("ID");
	 	          	String actionTypes = rs.getString("ACTION_TYPE");
	 	          	
	 	          	Collection<AlertActionType> types = new ArrayList<AlertActionType>();
	 	          	
	 	          	boolean userAction = false;
	 	          	
	 	          	// SMS AND EMAIL use the same config
	 	          	if (actionTypes!=null && actionTypes.contains(AlertActionType.SMS.name())) {
	 	          		types.add(AlertActionType.SMS);
	 	          		userAction = true;
	 	          	}
	 	          	
	 	          	if (actionTypes!=null && actionTypes.contains(AlertActionType.MAIL.name())) {
	 	          		types.add(AlertActionType.MAIL);
	 	          		userAction = true;
	 	          	}	 	          	
	 	          	
	 	          	if (userAction) {
	 	          		UserAlertAction userAlertAction = new UserAlertAction(id, alertDefinitionId, types);	 	          	
		 	          	loadUserAlertAction(userAlertAction);
		 	          	alertDefinition.addAlertAction(userAlertAction);
	 	          	}
	 	          	
		 	       	if (actionTypes!=null && actionTypes.contains(AlertActionType.SNMP.name())) {
	 	          		types.add(AlertActionType.SNMP);
			 	       	SnmpAlertAction snmpAlertAction = new SnmpAlertAction(id, alertDefinitionId, types);	 	          	
		 	          	loadSnmpAlertAction(snmpAlertAction);	 	          	
		 	          	alertDefinition.addAlertAction(snmpAlertAction);
	 	          	}
				}
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}	
		
	protected void loadUserAlertAction(UserAlertAction userAlertAction)
	throws PersistenceException {
		
		String query = "SELECT * FROM USER_ALERT_ACTION WHERE ID=? AND ALERT_DEFINITION_ID=?";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,userAlertAction.getId());
			pStmt.setLong(2, userAlertAction.getAlertDefinitionId());
			rs = pStmt.executeQuery();
			while (rs.next()) {
				long recipientId = rs.getLong("RECIPIENT_ID");
				String recipientType = rs.getString("RECIPIENT_TYPE");
				if(recipientType.equals("USER")){
					userAlertAction.addUserId(recipientId);
				} 	          
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}	
	
	protected void loadSnmpAlertAction(SnmpAlertAction snmpAlertAction)
	throws PersistenceException {
		
		String query = "SELECT * FROM SNMP_ALERT_ACTION WHERE ID=? AND ALERT_DEFINITION_ID=?";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1,snmpAlertAction.getId());
			pStmt.setLong(2, snmpAlertAction.getAlertDefinitionId());
			rs = pStmt.executeQuery();
			while (rs.next()) {
				long snmpConfigId = rs.getLong("SNMP_CONFIG_ID");
				snmpAlertAction.addSnmpConfigId(snmpConfigId);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}	
	
	public List<AlertEvent> getAlertEvents(long alertDefinitionId, String eventType) throws PersistenceException {
		List<AlertEvent> events = new ArrayList<AlertEvent>();
		if ("COMMENT".equals(eventType))
			events = getAlertCommentEvents(alertDefinitionId);
		else if ("COMPUTE".equals(eventType))
			events = getAlertComputeEvents(alertDefinitionId);
		return events;
	}
	
	private List<AlertEvent> getAlertCommentEvents (long alertDefinitionId) throws PersistenceException {
		String query = "SELECT evt.ID,evt.EVENT_DATE,evt.OLD_WORKFLOW,evt.NEW_WORKFLOW,cmt.COMMENTS,cmt.USER_ID " + 
					   "FROM ALERT_EVENT evt, ALERT_EVENT_COMMENT cmt " +
					   "WHERE evt.ID=cmt.ALERT_EVENT_ID AND evt.EVENT_TYPE='COMMENT' AND evt.ALERT_DEFINITION_ID=?";
		
		List<AlertEvent> events = new ArrayList<AlertEvent>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,alertDefinitionId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				long id = rs.getLong("ID");
				Date eventDate = new Date(rs.getTimestamp("EVENT_DATE").getTime());
				String oldWorkflow = rs.getString("OLD_WORKFLOW");
				String newWorkflow = rs.getString("NEW_WORKFLOW");
				String comment = rs.getString("COMMENTS");
				long userId = rs.getLong("USER_ID");
				AlertCommentEvent event = new AlertCommentEvent(eventDate, AlertWorkflow.getStateByName(oldWorkflow), AlertWorkflow.getStateByName(newWorkflow), comment, userId);
				event.setId(id);
				events.add(event);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
		
		return events;
	}
	
	private List<AlertEvent> getAlertComputeEvents (long alertDefinitionId) throws PersistenceException {
		String query = "SELECT evt.ID,evt.EVENT_DATE,evt.OLD_WORKFLOW,evt.NEW_WORKFLOW,cmpt.SEVERITY,cmpt.STATUS,cmpt.IKR_VALUE_IDS " + 
					   "FROM ALERT_EVENT evt " +
					   "LEFT JOIN ALERT_EVENT_COMPUTE cmpt ON evt.ID=cmpt.ALERT_EVENT_ID "+
					   "WHERE evt.EVENT_TYPE='COMPUTE' AND evt.ALERT_DEFINITION_ID=?"; 
		
		Map<Long, AlertComputeEvent> eventMap = new HashMap<Long, AlertComputeEvent>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,alertDefinitionId);
			rs = pStmt.executeQuery();			
			while (rs.next()) {
				long id = rs.getLong("ID");
				Date eventDate = new Date(rs.getTimestamp("EVENT_DATE").getTime());
				String oldWorkflow = rs.getString("OLD_WORKFLOW");
				String newWorkflow = rs.getString("NEW_WORKFLOW");
				
				AlertComputeEvent event = eventMap.get(id);
				if (event == null) {
					event = new AlertComputeEvent(eventDate, AlertWorkflow.getStateByName(oldWorkflow), AlertWorkflow.getStateByName(newWorkflow));
					event.setId(id);
					eventMap.put(id, event);
				}
				
				Integer severity = rs.getInt("SEVERITY");
				String status = rs.getString("STATUS");
				String ikrValueIdsStr = rs.getString("IKR_VALUE_IDS");
				if (severity!=null && status!=null && ikrValueIdsStr!=null) {
					int lg = ikrValueIdsStr.length();
					ikrValueIdsStr = ikrValueIdsStr.substring(1,lg-1);
					Set<Long> ikrValueIds = new HashSet<Long>();
					for(String valId : ikrValueIdsStr.split(",")) {
						if (valId!=null&&valId.length()>0)
							ikrValueIds.add(Long.parseLong(valId.trim()));
					}				
					AlertComputeResolution acr = new AlertComputeResolution(new AlertCompute(AlertWorkflow.getStateBySeverity(severity)), ComputeStatus.valueOf(status), ikrValueIds);
					event.addAlertComputeResolution(acr);
				}
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
		
		return new ArrayList<AlertEvent>(eventMap.values());
	}
	
	public void createAlertEvent(long alertDefinitionId, AlertEvent event)
	throws PersistenceException {
		
		String query = "INSERT INTO ALERT_EVENT (ID,ALERT_DEFINITION_ID,EVENT_DATE,EVENT_TYPE,OLD_WORKFLOW,NEW_WORKFLOW) VALUES (?,?,?,?,?,?)";
		
		String queryComment = "INSERT INTO ALERT_EVENT_COMMENT (ALERT_EVENT_ID,USER_ID,COMMENTS) VALUES (?,?,?)";
		String queryCompute = "INSERT INTO ALERT_EVENT_COMPUTE (ALERT_EVENT_ID,SEVERITY,STATUS,IKR_VALUE_IDS) VALUES (?,?,?,?)";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt2 = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			
			pStmt = con.prepareStatement(query.toUpperCase());
			
			pStmt.setLong(1,event.getId());
			pStmt.setLong(2,alertDefinitionId);
			pStmt.setTimestamp(3,new java.sql.Timestamp(event.getEventDate().getTime()));
			pStmt.setString(4, event instanceof AlertCommentEvent? "COMMENT" : "COMPUTE");
			pStmt.setString(5, event.getOldState().toString());
			pStmt.setString(6, event.getNewState().toString());			
			pStmt.executeUpdate();

			if (event instanceof AlertCommentEvent) {
				AlertCommentEvent commentEvent = (AlertCommentEvent)event;				
				pStmt2 = con.prepareStatement(queryComment.toUpperCase());				
				pStmt2.setLong(1, event.getId());
				pStmt2.setLong(2, commentEvent.getUserId());
				pStmt2.setString(3, commentEvent.getComment());
				pStmt2.executeUpdate();
			} else {
				AlertComputeEvent computeEvent = (AlertComputeEvent)event;				
				pStmt2 = con.prepareStatement(queryCompute.toUpperCase());
				pStmt2.setLong(1,event.getId());
				Collection<AlertComputeResolution> crs = computeEvent.getAlertComputeResolutions();				
				for(AlertComputeResolution cr : crs) {
					pStmt2.setInt(2, cr.getAlertCompute().getWorkflow().getSeverity());
					pStmt2.setString(3, cr.getComputeStatus().toString());
					pStmt2.setString(4, cr.getIkrValueIds().toString());
					pStmt2.executeUpdate();
				}
			}			
			con.commit();
		} catch(SQLException e) {
			rollbackConnection(con);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeStatement(pStmt2);
			closeConnection(con);
		}		
	}
	
	public void updateAlertEvent(AlertEvent event)
	throws PersistenceException {
		String queryEventObject = "UPDATE ALERT_EVENT_COMMENT SET COMMENTS=?, USER_ID=? WHERE ALERT_EVENT_ID=?";
		if (event instanceof AlertComputeEvent)
			return;
		
		AlertCommentEvent evtComment = (AlertCommentEvent)event;
		Connection con = null;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt2 = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);			
			pStmt = con.prepareStatement(queryEventObject.toUpperCase());
			pStmt.setString(1,evtComment.getComment());
			pStmt.setLong(2,evtComment.getUserId());
			pStmt.setLong(3,event.getId());
			pStmt.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			rollbackConnection(con);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeStatement(pStmt2);
			closeConnection(con);
		}		
	}	
	
	public void deleteAlertEvent(AlertEvent event)
	throws PersistenceException {
		
		String queryEvent = "DELETE FROM ALERT_EVENT WHERE ID=?";	
		String queryEventObject = "DELETE FROM ALERT_EVENT_COMMENT WHERE ALERT_EVENT_ID=?";
		if (event instanceof AlertComputeEvent)
			queryEventObject = "DELETE FROM ALERT_EVENT_COMPUTE WHERE ALERT_EVENT_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt2 = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);			
			pStmt = con.prepareStatement(queryEvent.toUpperCase());			
			pStmt.setLong(1,event.getId());
			pStmt.executeUpdate();
			pStmt2 = con.prepareStatement(queryEventObject.toUpperCase());				
			pStmt2.setLong(1, event.getId());
			pStmt2.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			rollbackConnection(con);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeStatement(pStmt2);
			closeConnection(con);
		}		
	}	
	
	public void createAlertDefinition(AlertDefinition alertDef, long id)
	throws PersistenceException {
			
			String query = "INSERT INTO ALERT_DEFINITION (NAME,DESCRIPTION,CREATION_DATE,LAST_UPDATE_DATE,LOGICAL_ENV_ID,ALERT_GROUP_ID,DOMAIN_ID,SUB_DOMAIN_ID,ENABLE,HOW_TO,ID,RAISING_DELAY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		
			Connection con = null;
			PreparedStatement pStmt = null;

			try 
			{
				con = dataSource.getConnection();				
				pStmt = con.prepareStatement(query.toUpperCase());
				alertDef.setId(id);
	         
		        pStmt.setString(1,alertDef.getName());
		        pStmt.setString(2,alertDef.getDescription());        
		        pStmt.setTimestamp(3,new java.sql.Timestamp(System.currentTimeMillis()));
		        pStmt.setTimestamp(4,new java.sql.Timestamp(System.currentTimeMillis()));
		        pStmt.setInt(5, alertDef.getLogicalEnv());
		        pStmt.setInt(6,alertDef.getGroup());
		        pStmt.setInt(7, alertDef.getDomain());
		        pStmt.setInt(8, alertDef.getSubDomain());
		        pStmt.setBoolean(9,alertDef.isEnable());
		        pStmt.setString(10, alertDef.getHowTo());
		        pStmt.setLong(11,alertDef.getId());
		        pStmt.setLong(12, alertDef.getRaisingDelay());
	            pStmt.executeUpdate();
	            
	            createAlertConditions(alertDef);
	            createAlertComputes(alertDef);
	            createAlertValidities(alertDef);
	            
	            createAlertActions(alertDef);
			} 
			catch(SQLException e) {
				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
			} finally {
				closeStatement(pStmt);
				closeConnection(con);
			}	
			
	}
	
	protected void createAlertConditions(AlertDefinition alertDef)throws PersistenceException {
		String queryInsert = "INSERT INTO ALERT_CONDITION (Id,ALERT_DEFINITION_ID,IKR_DEFINITION_ID,COND_VALUE,COND_OPERATOR,ENABLE,COND_UNIT_TYPE,COND_UNIT) VALUES (?,?,?,?,?,?,?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(queryInsert.toUpperCase());
			
			for (AlertCondition condition: alertDef.getAlertConditions()) {
				pStmt.setInt(1, condition.getId());
				pStmt.setLong(2, alertDef.getId());
				pStmt.setLong(3, condition.getIkrDefinitionId());				
				String conditionValue = ((ValueAlertCondition)condition).getValue();
				pStmt.setString(4, conditionValue);
				pStmt.setString(5, condition.getOperator().name());
				pStmt.setBoolean(6, condition.isEnable());
				pStmt.setString(7, ((ValueAlertCondition)condition).getUnitType().name());
				pStmt.setString(8, ((ValueAlertCondition)condition).getUnit().name());
				pStmt.executeUpdate();
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void createAlertWorkflowFilters(AlertDefinition alertDef)throws PersistenceException {
		String queryInsert = "INSERT INTO ALERT_WORKFLOW_FILTER (ALERT_DEFINITION_ID,WORKFLOW) VALUES (?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(queryInsert.toUpperCase());
		
			for (AlertWorkflow alertWorkflow: alertDef.getAlertWorkflowFilters()) {
				pStmt.setLong(1, alertDef.getId());
				pStmt.setString(2, alertWorkflow.toString());
				pStmt.executeUpdate();
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void createAlertComputes(AlertDefinition alertDef)throws PersistenceException {
		String queryInsert = "INSERT INTO ALERT_COMPUTE (ALERT_DEFINITION_ID,SEVERITY,LABEL,CAUSE,ENABLE) VALUES (?,?,?,?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(queryInsert.toUpperCase());
		
			for (AlertCompute alertCompute: alertDef.getAlertComputes()) {
				pStmt.setLong(1, alertDef.getId());
				pStmt.setInt(2, alertCompute.getWorkflow().getSeverity());
				pStmt.setString(3, alertCompute.getLabel());
				pStmt.setString(4, alertCompute.getCause());
				pStmt.setBoolean(5, alertCompute.isEnable());
				pStmt.executeUpdate();
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void createAlertValidities(AlertDefinition alertDef)throws PersistenceException {
		String queryInsert = "INSERT INTO ALERT_VALIDITY (ALERT_DEFINITION_ID,ALERT_VALIDITY_ID,VALIDITY_TIME_TYPE,VALIDITY_HOUR,VALIDITY_MIN) VALUES (?,?,?,?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(queryInsert.toUpperCase());
			
			int i = 1;
			for (AlertValidity validity : alertDef.getAlertValidities()) {
				pStmt.setLong(1, alertDef.getId());
				pStmt.setInt(2, i);
				pStmt.setString(3, "START");
				pStmt.setInt(4, validity.getStart().getHour());
				pStmt.setInt(5, validity.getStart().getMinute());
				pStmt.executeUpdate();
				
				if (validity.getEnd() != null) {
					pStmt.setLong(1, alertDef.getId());
					pStmt.setInt(2, i);
					pStmt.setString(3, "END");
					pStmt.setInt(4, validity.getEnd().getHour());
					pStmt.setInt(5, validity.getEnd().getMinute());
					pStmt.executeUpdate();
				}
				
				i = i + 1;
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void deleteAlertValidities(long idAlertDef)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_VALIDITY WHERE ALERT_DEFINITION_ID=?";			
		Connection con = null;
		PreparedStatement pStmt = null;			
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}


	protected void deleteAlertConditions(long idAlertDef)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_CONDITION WHERE ALERT_DEFINITION_ID=?";			
		Connection con = null;
		PreparedStatement pStmt = null;			
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void deleteAlertComputes(long idAlertDef)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_COMPUTE WHERE ALERT_DEFINITION_ID=?";			
		Connection con = null;
		PreparedStatement pStmt = null;			
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void deleteAlertWorkflowFilters(long idAlertDef)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_WORKFLOW_FILTER WHERE ALERT_DEFINITION_ID=?";			
		Connection con = null;
		PreparedStatement pStmt = null;			
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void deleteAlertActions(long idAlertDef) throws PersistenceException {
		
		String query = "DELETE FROM ALERT_ACTION WHERE ALERT_DEFINITION_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt2 = null;
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeStatement(pStmt2);
			closeConnection(con);
		}		
	}	
	
	protected void deleteUserAlertActions(long idAlertDef) throws PersistenceException {
			
		String query = "DELETE FROM USER_ALERT_ACTION WHERE ALERT_DEFINITION_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void deleteSnmpAlertActions(long idAlertDef) throws PersistenceException {
		
		String query = "DELETE FROM SNMP_ALERT_ACTION WHERE ALERT_DEFINITION_ID=?";		
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,idAlertDef);
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void createUserAlertAction(UserAlertAction userAction)throws PersistenceException {
		
		String queryInsertAction = "INSERT INTO ALERT_ACTION (ID,ALERT_DEFINITION_ID,ACTION_TYPE) VALUES (?,?,?)";	
		String queryInsert = "INSERT INTO USER_ALERT_ACTION (ID,ALERT_DEFINITION_ID,RECIPIENT_ID,RECIPIENT_TYPE) VALUES (?,?,?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		List<PreparedStatement> pStmts = new ArrayList<PreparedStatement>();
		ResultSet res = null;

		try 
		{
			if (userAction.getUserIds().size() >0) {				
				String types = "";
				if(userAction.getTypes().contains(AlertActionType.SMS)) {
					types = types + "--" + AlertActionType.SMS.name();
				} 
				if(userAction.getTypes().contains(AlertActionType.MAIL)) {
					types = types + "--" + AlertActionType.MAIL.name();
				}
				
				if (types.length()>0){
					con = dataSource.getConnection();
					pStmt = con.prepareStatement(queryInsertAction.toUpperCase());
				    pStmt.setInt(1, userAction.getId());
					pStmt.setLong(2,userAction.getAlertDefinitionId());			   
					pStmt.setString(3,types);
				    pStmt.executeUpdate();	    
					
					for(Long userId : userAction.getUserIds()) {
						PreparedStatement pStmt2= con.prepareStatement(queryInsert.toUpperCase());
						pStmt2.setLong(1, userAction.getId());
						pStmt2.setLong(2,userAction.getAlertDefinitionId());
						pStmt2.setLong(3, userId);
						pStmt2.setString(4,UserAlertAction.RecipientType.USER.name());
						pStmt2.executeUpdate();
						pStmts.add(pStmt2);
					}
				}
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			for (PreparedStatement pStmt2 : pStmts) {
				closeStatement(pStmt2);
			}			
			closeResultSet(res);
			closeConnection(con);
		}		
	}
	
	protected void createSnmpAlertAction(SnmpAlertAction snmpAction)throws PersistenceException {
		
		String queryInsertAction = "INSERT INTO ALERT_ACTION (ID,ALERT_DEFINITION_ID,ACTION_TYPE) VALUES (?,?,?)";	
		String queryInsert = "INSERT INTO SNMP_ALERT_ACTION (ID,ALERT_DEFINITION_ID,SNMP_CONFIG_ID) VALUES (?,?,?)";	
		
		Connection con = null;
		PreparedStatement pStmt = null;
		List<PreparedStatement> pStmts = new ArrayList<PreparedStatement>();
		ResultSet res = null;

		try 
		{
			if (snmpAction.getSnmpConfigIds().size() >0) {
				String types = "";
				if(snmpAction.getTypes().contains(AlertActionType.SNMP)) {
					types = types + "--" + AlertActionType.SNMP.name();
				} 
				if (types.length()>0){
					con = dataSource.getConnection();
					pStmt = con.prepareStatement(queryInsertAction.toUpperCase());
				    pStmt.setInt(1, snmpAction.getId());
					pStmt.setLong(2,snmpAction.getAlertDefinitionId());			    
					pStmt.setString(3,types);
				    pStmt.executeUpdate();
	
					for(Long snmpConfigId : snmpAction.getSnmpConfigIds()) {
						PreparedStatement pStmt2 = con.prepareStatement(queryInsert.toUpperCase());
						pStmt2.setInt(1, snmpAction.getId());
						pStmt2.setLong(2,snmpAction.getAlertDefinitionId());
						pStmt2.setLong(3, snmpConfigId);
						pStmt2.executeUpdate();
						pStmts.add(pStmt2);
					}
				}
			}
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			for (PreparedStatement pStmt2 : pStmts) {
				closeStatement(pStmt2);
			}			
			closeResultSet(res);
			closeConnection(con);
		}		
	}

	public void updateAlertDefinition(AlertDefinition alertDef)
	throws PersistenceException {

		String query = "UPDATE ALERT_DEFINITION SET NAME=?,DESCRIPTION=?,LAST_UPDATE_DATE=?,"
						+ "LOGICAL_ENV_ID=?,ALERT_GROUP_ID=?,DOMAIN_ID=?,SUB_DOMAIN_ID=?,ENABLE=?,HOW_TO=?,RAISING_DELAY=? WHERE ID=?";	
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());

	        pStmt.setString(1,alertDef.getName());
	        pStmt.setString(2,alertDef.getDescription());
	        pStmt.setTimestamp(3,new java.sql.Timestamp(System.currentTimeMillis()));
	        pStmt.setInt(4, alertDef.getLogicalEnv());
	        pStmt.setInt(5,alertDef.getGroup());
	        pStmt.setInt(6, alertDef.getDomain());
	        pStmt.setInt(7, alertDef.getSubDomain());
	        pStmt.setBoolean(8,alertDef.isEnable());
	        pStmt.setString(9, alertDef.getHowTo());
	        pStmt.setLong(10, alertDef.getRaisingDelay());
	        pStmt.setLong(11,alertDef.getId());

            pStmt.executeUpdate();
            
            deleteAlertConditions(alertDef.getId());
            createAlertConditions(alertDef);
            
            deleteAlertComputes(alertDef.getId());
            createAlertComputes(alertDef);
            
            deleteAlertValidities(alertDef.getId());
            createAlertValidities(alertDef);
            
            deleteAlertWorkflowFilters(alertDef.getId());
            createAlertWorkflowFilters(alertDef);
            
            createAlertActions(alertDef);

	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	private void createAlertActions(AlertDefinition alertDef) 
	throws PersistenceException {
        deleteAlertActions(alertDef.getId());
        deleteUserAlertActions(alertDef.getId());
        deleteSnmpAlertActions(alertDef.getId());
        
        int alertActionId = 1;
        
        for(AlertAction alertAction : alertDef.getAlertActions()) {
        	AbstractAlertAction abstractAlertAction = (AbstractAlertAction)alertAction;
        	abstractAlertAction.setId(alertActionId++);
        	abstractAlertAction.setAlertDefinitionId(alertDef.getId());
        	if (abstractAlertAction instanceof UserAlertAction) {
        		createUserAlertAction((UserAlertAction)abstractAlertAction);
        	} else if (abstractAlertAction instanceof SnmpAlertAction) {
        		createSnmpAlertAction((SnmpAlertAction)abstractAlertAction);
        	}
		}		
		
	}
	
	/*When you delete an alertdefinition you may have to delete all alerts???*/
	public void deleteAlertDefinition(long alertDefId)
	throws PersistenceException {	
		
		deleteAlertActions(alertDefId);
		deleteUserAlertActions(alertDefId);
		deleteSnmpAlertActions(alertDefId);
		
		deleteAlertComputes(alertDefId);
		deleteAlertConditions(alertDefId);
		
		String query = "DELETE FROM ALERT_DEFINITION WHERE ID=?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase());			
			pStmt.setLong(1,alertDefId);			
			pStmt.executeUpdate();		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}			
	}
	
	public void deleteAlertDefinitions(Collection<Long> alertDefIds)
	throws PersistenceException {		
		String query = "DELETE FROM ALERT_DEFINITION " + getWhereClause("ID", alertDefIds);
		
		Connection con = null;
		PreparedStatement pStmt = null;
		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.executeUpdate();
			
			deleteAlertConditions(alertDefIds);
			deleteAlertComputes(alertDefIds);
			deleteAlertActions(alertDefIds);
			deleteAlertComputes(alertDefIds);
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	protected void deleteAlertConditions(Collection<Long> alertDefIds)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_CONDITION " + getWhereClause("ALERT_DEFINITION_ID", alertDefIds);
		
		Connection con = null;
		PreparedStatement pStmt = null;	
		
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.executeUpdate();

		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	protected void deleteAlertComputes(Collection<Long> alertDefIds)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_COMPUTE " + getWhereClause("ALERT_DEFINITION_ID", alertDefIds);		
		Connection con = null;
		PreparedStatement pStmt = null;	
		
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.executeUpdate();

		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void deleteAlertWorkflowFilters(Collection<Long> alertDefIds)throws PersistenceException {		
		String queryDelete = "DELETE FROM ALERT_WORKFLOW_FILTER " + getWhereClause("ALERT_DEFINITION_ID", alertDefIds);		
		Connection con = null;
		PreparedStatement pStmt = null;	
		
		try 
		{
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(queryDelete.toUpperCase());
			pStmt.executeUpdate();

		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}	
	
	protected void deleteAlertActions(Collection<Long> alertDefIds) throws PersistenceException {			
		String queryActionAlertDelete = "DELETE FROM ALERT_ACTION " 
				+ getWhereClause("ALERT_DEFINITION_ID", alertDefIds);	
		
		String queryUserAlertDelete = "DELETE FROM USER_ALERT_ACTION " 
			 				+ getWhereClause("ALERT_DEFINITION_ID", alertDefIds);
		
		String querySnmpAlertDelete = "DELETE FROM SNMP_ALERT_ACTION " 
				+ getWhereClause("ALERT_DEFINITION_ID", alertDefIds);
		
		Connection con = null;
		PreparedStatement pStmt1 = null;
		PreparedStatement pStmt2 = null;
		PreparedStatement pStmt3 = null;
		try 
		{
			con = dataSource.getConnection();
		
			pStmt1 = con.prepareStatement(queryActionAlertDelete.toUpperCase());
			pStmt1.executeUpdate();			
			
			pStmt2 = con.prepareStatement(queryUserAlertDelete.toUpperCase());
			pStmt2.executeUpdate();
			
			pStmt3 = con.prepareStatement(querySnmpAlertDelete.toUpperCase());
			pStmt2.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt1);
			closeStatement(pStmt2);
			closeStatement(pStmt3);			
			closeConnection(con);
		}		
	}

	public void createSnmpConfig(SnmpConfig config) throws PersistenceException {
		String queryInsert = "INSERT INTO SNMP_CONFIG (NAME,VERSION,COMMUNITY,HOSTNAME,PORT,GENERIC_TYPE,SPECIFIC_TYPE,USERNAME,AUTH_PROTOCOL,AUTH_PASSWORD,PRIV_PROTOCOL,PRIV_PASSWORD,CONTEXT_NAME,ENGINE_ID) " +
							 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";	
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(queryInsert.toUpperCase());
			pStmt.setString(1, config.getName());
			pStmt.setInt(2, config.getVersion());
			pStmt.setString(3, config.getCommunity());
			pStmt.setString(4, config.getHostname());
			pStmt.setInt(5, config.getPort());
			pStmt.setInt(6, config.getGenericTrapType());
			pStmt.setInt(7, config.getSpecificTrapType());
			pStmt.setString(8, config.getUser());
			pStmt.setString(9, config.getAuthProtocol());
			pStmt.setString(10, config.getAuthPassword());
			pStmt.setString(11, config.getPrivProtocol());
			pStmt.setString(12, config.getPrivPassword());
			pStmt.setString(13, config.getContextName());
			pStmt.setString(14, config.getEngineID());
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}

	public void deleteSnmpConfig(Collection<Long> ids)
			throws PersistenceException {
		String query = "DELETE FROM SNMP_CONFIG " + getWhereClause("ID", ids);
		
		Connection con = null;
		PreparedStatement pStmt = null;
		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.executeUpdate();		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}

	public void deleteSnmpConfig(long id) throws PersistenceException {
		String query = "DELETE FROM SNMP_CONFIG WHERE ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1,id);
			pStmt.executeUpdate();		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}				
	}

	public Collection<SnmpConfig> getSnmpConfigs() throws PersistenceException {
		List<SnmpConfig> res = new ArrayList<SnmpConfig>();
		
		String query = "SELECT * FROM SNMP_CONFIG";	
		
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
 	          	int version = rs.getInt("VERSION");
 	          	String community = rs.getString("COMMUNITY");
 	          	String hostname = rs.getString("HOSTNAME");
 	          	int port = rs.getInt("PORT");
 	          	int genericType = rs.getInt("GENERIC_TYPE");
 	          	int specificType = rs.getInt("SPECIFIC_TYPE");
 	          	String username = rs.getString("USERNAME");
 	          	String authProtocol = rs.getString("AUTH_PROTOCOL");
 	          	String authPassword = rs.getString("AUTH_PASSWORD");
 	          	String privProtocol = rs.getString("PRIV_PROTOCOL");
 	          	String privPassword = rs.getString("PRIV_PASSWORD");
 	          	String contextName = rs.getString("CONTEXT_NAME");
 	          	String engineID = rs.getString("ENGINE_ID");
 	          	
 	          	SnmpConfig snmpConfig = new SnmpConfig(id,name,version,hostname,port,genericType,specificType,community,username, authProtocol, authPassword, privProtocol,privPassword,contextName,engineID);    
 	          	res.add(snmpConfig);
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

	public Collection<SnmpConfig> getSnmpConfigs(Collection<Long> ids)
			throws PersistenceException {
		List<SnmpConfig> res = new ArrayList<SnmpConfig>();
		
		String query = "SELECT * FROM SNMP_CONFIG";		
		String whereClause = getWhereClause("ID", ids);
		
		query += whereClause;
		
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
 	          	int version = rs.getInt("VERSION");
 	          	String community = rs.getString("COMMUNITY");
 	          	String hostname = rs.getString("HOSTNAME");
 	          	int port = rs.getInt("PORT");
 	          	int genericType = rs.getInt("GENERIC_TYPE");
 	          	int specificType = rs.getInt("SPECIFIC_TYPE");
 	          	String username = rs.getString("USERNAME");
 	          	String authProtocol = rs.getString("AUTH_PROTOCOL");
 	          	String authPassword = rs.getString("AUTH_PASSWORD");
 	          	String privProtocol = rs.getString("PRIV_PROTOCOL");
 	          	String privPassword = rs.getString("PRIV_PASSWORD");
 	          	String contextName = rs.getString("CONTEXT_NAME");
 	          	String engineID = rs.getString("ENGINE_ID");
 	          	
 	          	SnmpConfig snmpConfig = new SnmpConfig(id,name,version,hostname,port,genericType,specificType,community,username, authProtocol, authPassword, privProtocol,privPassword,contextName,engineID);    
 	          	res.add(snmpConfig);
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

	public void updateSnmpConfig(SnmpConfig config) throws PersistenceException {
		String query = "UPDATE SNMP_CONFIG SET NAME=?,VERSION=?,COMMUNITY=?,"
			+ "HOSTNAME=?,PORT=?,GENERIC_TYPE=?,SPECIFIC_TYPE=?,USERNAME=?,AUTH_PROTOCOL=?,AUTH_PASSWORD=?,PRIV_PROTOCOL=?," +
			  "PRIV_PASSWORD=?,CONTEXT_NAME=?,ENGINE_ID=? WHERE ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		try  {
			con = dataSource.getConnection();
			pStmt= con.prepareStatement(query.toUpperCase());
			pStmt.setString(1, config.getName());
			pStmt.setInt(2, config.getVersion());
			pStmt.setString(3, config.getCommunity());
			pStmt.setString(4, config.getHostname());
			pStmt.setInt(5, config.getPort());
			pStmt.setInt(6, config.getGenericTrapType());
			pStmt.setInt(7, config.getSpecificTrapType());
			pStmt.setString(8, config.getUser());
			pStmt.setString(9, config.getAuthProtocol());
			pStmt.setString(10, config.getAuthPassword());
			pStmt.setString(11, config.getPrivProtocol());
			pStmt.setString(12, config.getPrivPassword());
			pStmt.setString(13, config.getContextName());
			pStmt.setString(14, config.getEngineID());
			pStmt.setLong(15, config.getId());
			pStmt.executeUpdate();
		} 
		catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}
	
	
}
