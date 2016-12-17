package com.fsi.monitoring.indiktor.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.indiktor.dao.MonitorDAO;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.monitoring.scheduler.IkrJobSchedulerExec;


public abstract class AbstractMonitorDAO 
extends AbstractDataSourceDAO
implements MonitorDAO { 
	
	protected final static Logger LOG = Logger.getLogger(AbstractMonitorDAO.class);
	
	public long getIkrDefinition(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException {
		long res = 0;
		
		String query = "SELECT ID FROM IKR_DEFINITION"
					  + " WHERE MONITOR_ID=? AND IKR_CATEGORY_ID=? AND IKR_INSTANCE=? AND IKR_COMPUTE=? ORDER BY IKR_INSTANCE";
	
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;		
		try {
		      con = dataSource.getConnection();		
		      pStmt = con.prepareStatement(query.toUpperCase());
		      pStmt.setLong(1, monitorId);
		      pStmt.setInt(2, metricGroupId);
		      pStmt.setString(3, instance);
		      pStmt.setString(4, compute.name());
		      rs = pStmt.executeQuery();		      
		      if (rs.next()) {       		 
			    	res = rs.getLong("ID");
		      }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
	
		return res;	
	}
	
	public List<Long> getCrossComputeDefinitionIds(int logicalEnvId, int metricGroupId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM IKR_DEFINITION WHERE MONITOR_ID=0 AND IKR_CATEGORY_ID=?";
		if (logicalEnvId>0){
			query = query + " AND LOGICAL_ENV_ID=?";
		}
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          pStmt.setInt(1, metricGroupId);
          pStmt.setInt(2, logicalEnvId);
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return res;	
	}

	public List<Long> getCrossComputeDefinitionIds(int logicalEnvId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM IKR_DEFINITION WHERE MONITOR_ID=0";
		if (logicalEnvId>0){
			query = query + " AND LOGICAL_ENV_ID=?";
		}

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          if (logicalEnvId>0){
        	  pStmt.setInt(1, logicalEnvId);
          }
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}	
	
	public List<Long> getStaticDataDefinitionIds(int logicalEnvId, int metricGroupId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM IKR_DEFINITION WHERE MONITOR_ID=-1 AND IKR_CATEGORY_ID=?";
		if (logicalEnvId>0){
			query = query + " AND LOGICAL_ENV_ID=?";
		}
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          pStmt.setInt(1, metricGroupId);
          pStmt.setInt(2, logicalEnvId);
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return res;	
	}
	
	public List<Long> loadStaticDataDefinitionIds()
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM IKR_DEFINITION def WHERE MONITOR_ID=-1";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}	 

	public List<Long> getStaticDataDefinitionIds(int logicalEnvId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT ID FROM IKR_DEFINITION WHERE MONITOR_ID=-1";
		if (logicalEnvId>0){
			query = query + " AND LOGICAL_ENV_ID=?";
		}

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          if (logicalEnvId>0)
        	  pStmt.setInt(1, logicalEnvId);
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}	
	
	public List<Long> getIkrDefinitionIds(int logicalEnvId, String context, int metricGroupId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT def.ID as ID FROM IKR_DEFINITION def, MONITOR mon"
					+ " WHERE mon.ID=def.MONITOR_ID AND mon.NAME=? AND mon.LOGICAL_ENV_ID=? AND def.IKR_CATEGORY_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase()); 
          pStmt.setString(1, context);
          pStmt.setInt(2, logicalEnvId);
          pStmt.setInt(3, metricGroupId);
          rs = pStmt.executeQuery();           
          while (rs.next()) { 
        	long id = rs.getLong("ID");
	       	res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}
	
	protected Collection<Long> getCrossCompute(long defId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DEF1.ID as TRIG,DEF2.ID as COMPUTED, DEF2.CROSS_COMPUTE as FORMULA FROM IKR_DEFINITION DEF1 INNER JOIN IKR_DEFINITION DEF2"
					 + " ON LOCATE(CONCAT('M',DEF1.ID), DEF2.CROSS_COMPUTE) AND DEF1.ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
		    con = dataSource.getConnection();
		
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setLong(1,defId);
		    rs = pStmt.executeQuery();
		    
		    while (rs.next()) {    
		    	long trig = rs.getLong("TRIG");
		    	long computed = rs.getLong("COMPUTED");
		    	
		    	String formula = rs.getString("FORMULA");
		    	
				Pattern p = Pattern.compile("M\\d+");
				Matcher m = p.matcher(formula);
				boolean b = false;
				while(b = m.find()) {
					String formulaId = m.group();
					
					String defIdStr = formulaId.substring(1);
					long tmpDefId = Long.valueOf(defIdStr);
					
					if (tmpDefId == trig) {
						res.add(computed);
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
		
		return res;
	}	
	
	private Collection<Long> getIkrCompute(long ikrDefinitionId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
		String query = "SELECT DEF1.ID as TRIG,DEF2.ID as COMPUTED FROM IKR_DEFINITION DEF1 INNER JOIN IKR_DEFINITION DEF2 "
					+ "ON DEF2.IKR_CATEGORY_ID=DEF1.IKR_CATEGORY_ID AND DEF2.IKR_INSTANCE=DEF1.IKR_INSTANCE "
					+ "AND DEF2.LOGICAL_ENV_ID=DEF1.LOGICAL_ENV_ID "
					+ "AND DEF1.ID != DEF2.ID AND DEF1.ID=? "
					+ "AND DEF2.IKR_COMPUTE !=? AND DEF1.IKR_COMPUTE=? AND DEF2.enable=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
		    con = dataSource.getConnection();
		
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setLong(1, ikrDefinitionId);
		    pStmt.setString(2, MetricCompute.RT.name());
		    pStmt.setString(3, MetricCompute.RT.name());
		    pStmt.setBoolean(4, true);
		    rs = pStmt.executeQuery();
		    
		    while (rs.next()) {    
		    	long computed = rs.getLong("COMPUTED");
		    	res.add(computed);
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
	
	public Collection<Long> getLastIkrValueIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM IKR_VALUE ORDER BY CAPTURE_TIME LIMIT ?";
		
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
	
	public Collection<Long> getLastIkrDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
	
		String query = "SELECT ID FROM IKR_DEFINITION LIMIT ?";
		
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
	
	
	public IkrValue getIkrValue(long id) 
	throws PersistenceException {
	
		IkrValue ikrValue = null;
		
		String query = "SELECT CAPTURE_TIME,IKR_CATEGORY_ID,IKR_DEFINITION_ID,VALUE FROM IKR_VALUE val, IKR_DEFINITION def" 
					  + " WHERE val.ID=? AND val.IKR_DEFINITION_ID=def.ID";

		Connection con = null;
		PreparedStatement pStmt = null;
		
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, id);
			 
			rs = pStmt.executeQuery();
		
			if (rs.next()) {       	
 	          	Date captureTime = new Date(rs.getTimestamp("CAPTURE_TIME").getTime());
				long ikrDefinitionId = rs.getLong("IKR_DEFINITION_ID");
				String valueStr = rs.getString("VALUE");
				int ikrCategoryId = rs.getInt("IKR_CATEGORY_ID");
				
				ikrValue = new IkrValue();
				ikrValue.setCaptureTime(captureTime);
				ikrValue.setIkrCategoryId(ikrCategoryId);
				ikrValue.setIkrDefinitionId(ikrDefinitionId);
				ikrValue.setValue(valueStr);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return ikrValue;
	}
	
	public List<Long> getIkrDefinitionIds(long monitorId) 
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();

		String query = "SELECT ID FROM IKR_DEFINITION WHERE MONITOR_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
        

			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, monitorId);
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("ID");
				res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}
	
	public List<Long> getIkrDefinitionIds(int ikrStaticDomainId) 
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();

		String query = "SELECT ID FROM IKR_DEFINITION WHERE IKR_CATEGORY_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
        

			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, ikrStaticDomainId);
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("ID");
				res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}
	
	public List<Long> getIkrDefinitionIds() 
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();

		String query = "SELECT ID FROM IKR_DEFINITION ";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("ID");
				res.add(id);
          }
		} catch(SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}		
	
	public Map<Long,AbstractIkrDefinition> getIkrDefinitions(Collection<Long> ids) 
		throws PersistenceException {
		
		Map<Long,AbstractIkrDefinition> res = new HashMap<Long,AbstractIkrDefinition>();

		String query = "SELECT * FROM IKR_DEFINITION";
		String whereClause = null;
		if (ids != null) {
			whereClause = getWhereClause("ID", ids);
		} else {
			whereClause = "";
		}
			
		query = query + whereClause + " ORDER BY IKR_INSTANCE";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
        

			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				AbstractIkrDefinition ikrDefinition = mapIkrDefiniton(rs);
				 if (ikrDefinition != null) {
					 res.put(ikrDefinition.getId(),ikrDefinition);
				 }
			}
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}
	
	public AbstractIkrDefinition getIkrDefinition(long ikrDefinitionId) 
	throws PersistenceException {
	
		AbstractIkrDefinition res = null;	

		 String query = "SELECT * FROM IKR_DEFINITION WHERE  ID=?";

		 Connection con = null;
		 PreparedStatement pStmt = null;
		 ResultSet rs = null;
	
		 try {
			 con = dataSource.getConnection();
    

			 pStmt = con.prepareStatement(query.toUpperCase());
			 pStmt.setLong(1, ikrDefinitionId);
			 rs = pStmt.executeQuery();

			 if (rs.next()) {
				 res = mapIkrDefiniton(rs);
			 }
		} catch(SQLException e) {
			LOG.error(e);
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}	
	
	protected AbstractIkrDefinition mapIkrDefiniton(ResultSet rs)
	throws SQLException, PersistenceException {
		AbstractIkrDefinition res = null;

		long id = rs.getLong("ID");
		long monitorId = rs.getLong("MONITOR_ID");
		int ikrCategoryId = rs.getInt("IKR_CATEGORY_ID");
		String ikrInstance = rs.getString("IKR_INSTANCE");
		int logicalEnvId = rs.getInt("LOGICAL_ENV_ID");
		String crossComputation = rs.getString("CROSS_COMPUTE");
		String sd = rs.getString("SD_VALUE");
		boolean defEnable = rs.getBoolean("ENABLE");
	
		String metricComputeName  = rs.getString("IKR_COMPUTE");
		MetricCompute metricCompute = null;
		try {
			if (metricComputeName != null && metricComputeName.length() > 0) {
				metricCompute = MetricCompute.valueOf(metricComputeName);
			}
		} catch(Exception exc) {
			LOG.error("MetricCompute unknown : " + metricComputeName);
		}      	
      	
		if (logicalEnvId != 0 && monitorId == 0) {
			// This def is a CrossCompute def
			res = new CrossComputeDefinition(id, logicalEnvId, ikrCategoryId, ikrInstance, metricCompute, crossComputation, defEnable);
		} 
		else if (logicalEnvId != 0 && monitorId == -1) {
			// This Def is a Static Data Def
			StaticData staticData = new StaticData(id, logicalEnvId, ikrCategoryId, ikrInstance);
			staticData.setValue(sd);
			res = staticData;
		}
		else if (monitorId > 0){
			res = new IkrDefinition(id, monitorId, ikrCategoryId, ikrInstance, metricCompute, defEnable);
			Collection<Long> linkedCrossComputeDefinitionIds = getCrossCompute(id);
			IkrDefinition ikrDefinition = (IkrDefinition)res;
			ikrDefinition.setLinkedCrossComputeDefinitionIds(linkedCrossComputeDefinitionIds);
			res = ikrDefinition;
		}
		
		if (res != null) {
			Collection<Long> linkedStatisticDefinitionIds = getIkrCompute(id);
			res.setLinkedStatisticDefinitionIds(linkedStatisticDefinitionIds);
		}
		
		return res;
	}
	
	public void saveSchedulerExec(IkrJobSchedulerExec exec) 
	throws PersistenceException {
		Connection con = null;
        PreparedStatement pStmt = null;
        
        String query = "INSERT INTO SCHEDULER_EXEC (TASK_ID,START_TIME,END_TIME) VALUES (?,?,?)";

        try {
            con = dataSource.getConnection();
            pStmt = con.prepareStatement(query.toUpperCase());
            pStmt.setLong(1, exec.getJobSchedulerId());
            pStmt.setTimestamp(2, new Timestamp(exec.getStartTime().getTime()));
            Date endTime = exec.getEndTime();
            if (endTime != null)
            	pStmt.setTimestamp(3, new Timestamp(endTime.getTime()));
            else
            	pStmt.setTimestamp(3, null);
        	pStmt.executeUpdate();  
        } catch(SQLException e) {
        	e.printStackTrace();
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}
	
	
	
	public void saveIkrValues(Collection<IkrValue> values, long nextId, boolean archive) 
	throws PersistenceException {
		Connection con = null;
        PreparedStatement pStmt = null;
        
        String query = "INSERT INTO IKR_VALUE (ID,IKR_DEFINITION_ID,CAPTURE_TIME,VALUE) VALUES (?,?,?,?)";
        if (archive) {
        	query = "INSERT INTO IKR_VALUE_ARCHIVE (ID,IKR_DEFINITION_ID,CAPTURE_TIME,VALUE) VALUES (?,?,?,?)";
        }

        try {
            con = dataSource.getConnection();

            pStmt = con.prepareStatement(query.toUpperCase());
            for (IkrValue value : values) {
            	value.setId(nextId++);
            	
            	pStmt.setLong(1, value.getId());
            	pStmt.setLong(2, value.getValueDefinitionId());
            	pStmt.setTimestamp(3, new Timestamp(value.getCaptureTime().getTime()));
    			pStmt.setString(4,value.getValue());

            	pStmt.executeUpdate();      	
            }
        } catch(SQLException e) {
        	e.printStackTrace();
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }			
		
	}	

	public void createIkrDefinitions(Collection<IkrDefinition> ikrDefinitions) 
	throws PersistenceException{		
		for (IkrDefinition definition : ikrDefinitions) {
			createIkrDefinition(definition);
		}
	}
	
	public long createIkrDefinition(IkrDefinition ikrDefinition) 
	throws PersistenceException{
		
		long res = 0;
		 	
		Connection con = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        String query = "INSERT INTO IKR_DEFINITION (MONITOR_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,ENABLE,LOGICAL_ENV_ID) VALUES (?,?,?,?,?,?)";
        try {
            con = dataSource.getConnection();

            pStmt = con.prepareStatement(query.toUpperCase());            
            pStmt.setLong(1, ikrDefinition.getMonitorId());
        	pStmt.setInt(2, ikrDefinition.getIkrCategoryId());
        	pStmt.setString(3, ikrDefinition.getIkrInstance());
        	
        	String metricComputeName = ikrDefinition.getIkrCompute() == null ? null : ikrDefinition.getIkrCompute().name();
        	pStmt.setString(4, metricComputeName);
        	
        	pStmt.setBoolean(5, ikrDefinition.isActivated());
        	pStmt.setInt(6,0);
        	pStmt.executeUpdate(); 
        	
        	 rs = pStmt.getGeneratedKeys();
 			
             if (rs.next()) {
                 res = rs.getInt(1);   
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
	
	public void updateIkrDefinitions(Collection<IkrDefinition> ikrDefinitions) 
	throws PersistenceException{
		 	
		Connection con = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        String query = "UPDATE IKR_DEFINITION SET IKR_INSTANCE=?, ENABLE=? WHERE ID=?";
        try {
            con = dataSource.getConnection();

            pStmt = con.prepareStatement(query.toUpperCase()); 
            
            for (IkrDefinition ikrDefinition : ikrDefinitions) {
                    pStmt = con.prepareStatement(query);

                    pStmt.setString(1, ikrDefinition.getIkrInstance());
                	pStmt.setBoolean(2, ikrDefinition.isActivated());
               	 	pStmt.setLong(3, ikrDefinition.getId());
                	pStmt.executeUpdate(); 
            }
        } catch(SQLException e) {
        	e.printStackTrace();
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			        
	}	
	
	public void deleteIkrDefinitions(int logicalEnvId, boolean isComputed) 
	throws PersistenceException {
		String query = "DELETE FROM IKR_DEFINITION WHERE LOGICAL_ENV_ID=?" + ((isComputed) ? " AND MONITOR_ID=0" : "");
		Connection con = null;
		PreparedStatement pStmt = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, logicalEnvId);
			pStmt.executeUpdate();
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
	public void deleteIkrDefinitions(Collection<Long> ids) 
	throws PersistenceException {
		if (ids!=null && ids.size()>0) {
			String query = "DELETE FROM IKR_DEFINITION";
			String whereClause = getWhereClause("ID", ids);
			
			query = query + whereClause;
	
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
	}	
	
	public void deleteIkrDefinition(long ikrDefinitionId) 
	throws PersistenceException {
		String query = "DELETE FROM IKR_DEFINITION WHERE ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, ikrDefinitionId);
			pStmt.executeUpdate();
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
	public long createCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition)
	throws PersistenceException {	
		int res = 0;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "INSERT INTO IKR_DEFINITION (LOGICAL_ENV_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,CROSS_COMPUTE,ENABLE,MONITOR_ID) VALUES (?,?,?,?,?,?,?)";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, crossComputeDefinition.getLogicalEnvId());
			pStmt.setInt(2, crossComputeDefinition.getIkrCategoryId());
			pStmt.setString(3, crossComputeDefinition.getIkrInstance());
			pStmt.setString(4, crossComputeDefinition.getIkrCompute().name());
			pStmt.setString(5, crossComputeDefinition.getCrossComputation());
			pStmt.setBoolean(6, crossComputeDefinition.isActivated());
			pStmt.setLong(7, 0);

			pStmt.executeUpdate();
			
            rs = pStmt.getGeneratedKeys();
			
            if (rs.next()) {
                res = rs.getInt(1);
                crossComputeDefinition.setId(res);            
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

	public void updateCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition)
	throws PersistenceException {			
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "UPDATE IKR_DEFINITION SET LOGICAL_ENV_ID=?,IKR_CATEGORY_ID=?,IKR_INSTANCE=?,IKR_COMPUTE=?,CROSS_COMPUTE=?,ENABLE=?,MONITOR_ID=? WHERE ID=?";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, crossComputeDefinition.getLogicalEnvId());
			pStmt.setInt(2, crossComputeDefinition.getIkrCategoryId());
			pStmt.setString(3, crossComputeDefinition.getIkrInstance());
			pStmt.setString(4, crossComputeDefinition.getIkrCompute().name());
			pStmt.setString(5, crossComputeDefinition.getCrossComputation());
			pStmt.setBoolean(6, crossComputeDefinition.isActivated());
			pStmt.setLong(7, 0);
			pStmt.setLong(8, crossComputeDefinition.getId());
			
			pStmt.executeUpdate();
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
	}	
	
	public long createStaticDataDefinition(StaticData staticData)
	throws PersistenceException {	
		int res = 0;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "INSERT INTO IKR_DEFINITION (LOGICAL_ENV_ID,IKR_CATEGORY_ID,IKR_INSTANCE,IKR_COMPUTE,SD_VALUE,ENABLE,MONITOR_ID) VALUES (?,?,?,?,?,?,?)";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, staticData.getLogicalEnvId());
			pStmt.setInt(2, staticData.getIkrCategoryId());
			pStmt.setString(3, staticData.getIkrInstance());
			pStmt.setString(4, staticData.getIkrCompute().name());
			pStmt.setString(5, staticData.getValue());
			pStmt.setBoolean(6, staticData.isActivated());
			pStmt.setLong(7, -1);

			pStmt.executeUpdate();
			
            rs = pStmt.getGeneratedKeys();
			
            if (rs.next()) {
                res = rs.getInt(1);
                staticData.setId(res);            
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

	public void updateStaticDataDefinition(StaticData staticData)
	throws PersistenceException {			
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		String query = "UPDATE IKR_DEFINITION SET LOGICAL_ENV_ID=?,IKR_CATEGORY_ID=?,IKR_INSTANCE=?,IKR_COMPUTE=?,SD_VALUE=?,ENABLE=?,MONITOR_ID=? WHERE ID=?";

		try {
			con = dataSource.getConnection();
			
			pStmt = con.prepareStatement(query.toUpperCase());

			pStmt.setInt(1, staticData.getLogicalEnvId());
			pStmt.setInt(2, staticData.getIkrCategoryId());
			pStmt.setString(3, staticData.getIkrInstance());
			pStmt.setString(4, staticData.getIkrCompute().name());
			pStmt.setString(5, staticData.getValue());
			pStmt.setBoolean(6, staticData.isActivated());
			pStmt.setLong(7, -1);
			pStmt.setLong(8, staticData.getId());
			
			pStmt.executeUpdate();
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
	
}
