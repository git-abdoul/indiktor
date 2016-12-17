package com.fsi.monitoring.indiktor.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fsi.fwk.encryption.DES;
import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.encryption.DESException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.AbstractConnectorConfig;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.connector.HttpConnectorConfig;
import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.IkrVersion;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigExtension;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.IkrService;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.dao.DataModelDAO;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

public abstract class AbstractDataModelDAO 
extends AbstractDataSourceDAO 
implements DataModelDAO {
	protected DateTimeFormatter dateInFileName = DateTimeFormat.forPattern("yyyy-MM-dd_HH.mm.ss.SS");
	
	public static final short IKR_CATEGORY_LEVEL = 3;
	
	protected final static Logger LOG = Logger.getLogger(AbstractDataModelDAO.class);

	private DES connectorPwdManager;	
	
	protected AbstractDataModelDAO() {
		connectorPwdManager = new DES("CONNECTOR PASSWORDS ENCRYPTION");		
	}	
	
	public void resetIkrCategory() throws PersistenceException {
		String query = "DELETE FROM IKR_CATEGORY";		
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

	public void resetIkrService() throws PersistenceException {
		String query = "DELETE FROM IKR_MONITOR_SERVICE";		
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
	
//	public void resetActivatedIkrCategory() throws PersistenceException {
//		String query = "DELETE FROM ACTIVATED_IKR_CATEGORY";		
//		Connection con = null;
//		PreparedStatement pStmt = null;		
//		try {
//			con = dataSource.getConnection();		
//			pStmt = con.prepareStatement(query); 
//			pStmt.executeUpdate();
//		
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeStatement(pStmt);
//			closeConnection(con);
//		}	
//	}	
	
	
	private Map<String, List<String>> getMetricDomainItems() throws PersistenceException {
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG_ITEMS";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        rs = pStmt.executeQuery();
	        while (rs.next()) { 	  
	        	String domain = rs.getString("METRIC_DOMAIN");
	        	String value = rs.getString("ITEM_VALUE");
	        	List<String> items = res.get(domain);
	        	if (items == null) {
	        		items = new ArrayList<String>();
	        		res.put(domain, items);
	        	}
	        	items.add(value);
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
	
	public IkrVersion getIkrVersion() throws PersistenceException {
		IkrVersion version = null;
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM IKR_VERSION";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        rs = pStmt.executeQuery();
	        if (rs.next()) { 
	        	int major = rs.getInt("MAJOR");
	        	int minor = rs.getInt("MINOR");
	        	int sub = rs.getInt("SUB");
	        	Date versionDate = rs.getDate("VERSION_DATE");
	        	int patchVersion = rs.getInt("PATCH_VERSION");
	        	Date patchDate = rs.getDate("PATCH_DATE");    	
	        	
	        	version = new IkrVersion(major, minor, sub, versionDate, patchVersion, patchDate);
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
		return version;
	}

	private List<String> getMetricDomainItems(int metricDomainConfigId) throws PersistenceException {
		List<String> res = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG_ITEMS WHERE METRIC_DOMAIN_CONFIG_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,metricDomainConfigId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) { 	  
	        	String value = rs.getString("ITEM_VALUE");	        	
	        	res.add(value);
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

	private void addMetricDomainItems(List<String> items, long metricDomainConfigId)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "INSERT INTO METRIC_DOMAIN_CONFIG_ITEMS (METRIC_DOMAIN_CONFIG_ID,ITEM_VALUE) VALUES (?,?)";	       
	    try {
	    	con = dataSource.getConnection();	    	
	    	for (String item : items) {
		        pStmt = con.prepareStatement(query.toUpperCase());
		        pStmt.setLong(1,metricDomainConfigId);
		        pStmt.setString(2,item);	
	            pStmt.executeUpdate();	
	    	}
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}
	
	private void deleteMetricDomainItems(long metricDomainConfigId) throws PersistenceException {
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_ITEMS WHERE METRIC_DOMAIN_CONFIG_ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,metricDomainConfigId);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}

	public void cleanIkrStaticDomains() throws PersistenceException {
		String ikrStaticDomQuery = "DELETE FROM IKR_STATIC_DOMAIN";	
		String ikrCategoryQuery = "DELETE FROM IKR_CATEGORY";	
		Connection con = null;
		PreparedStatement ikrStaticDomPStmt = null;		
		PreparedStatement ikrCategoryPStmt = null;	
		try {
			con = dataSource.getConnection();		
			ikrStaticDomPStmt = con.prepareStatement(ikrStaticDomQuery.toUpperCase()); 
			ikrStaticDomPStmt.executeUpdate();
			
			ikrCategoryPStmt = con.prepareStatement(ikrCategoryQuery.toUpperCase()); 
			ikrCategoryPStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(ikrStaticDomPStmt);
			closeStatement(ikrCategoryPStmt);
			closeConnection(con);
		}	
	}
	
	private void cleanIkrCategories() throws PersistenceException {
		String query = "DELETE FROM IKR_CATEGORY";		
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
	
	public void createIkrStaticDomain(IkrStaticDomain staticDomain, int nextId) throws PersistenceException {		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		String query = "INSERT INTO IKR_STATIC_DOMAIN (ID, PARENT_ID, DOMAIN_VALUE, DOMAIN_LABEL, DESCRIPTION) VALUES (?,?,?,?,?)";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setInt(1,nextId);
		    pStmt.setInt(2,staticDomain.getParentDomainId());
		    pStmt.setString(3,staticDomain.getDomainValue());
		    pStmt.setString(4,(staticDomain.getLabel()!=null)?staticDomain.getLabel():"");	
		    pStmt.setString(5,(staticDomain.getDescription()!=null)?staticDomain.getDescription():"");
		    pStmt.executeUpdate();	
		    if (staticDomain instanceof IkrCategory) {
            	IkrCategory ikrCategory = (IkrCategory)staticDomain;
            	ikrCategory.setId(nextId);
            	createIkrCategory(ikrCategory);
            }
            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}	
	
	public void updateIkrStaticDomain(IkrStaticDomain staticDomain)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE IKR_STATIC_DOMAIN SET PARENT_ID=?,DOMAIN_VALUE=?,DOMAIN_LABEL=?,DESCRIPTION=? " +
	    				"WHERE ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,staticDomain.getParentDomainId());
	        pStmt.setString(2,staticDomain.getDomainValue());
	        pStmt.setString(3,staticDomain.getLabel());	
	        pStmt.setString(4,staticDomain.getDescription());
	        pStmt.setInt(5,staticDomain.getId());
            pStmt.executeUpdate();
            if (staticDomain instanceof IkrCategory) {
            	IkrCategory ikrCategory = (IkrCategory)staticDomain;
            	updateIkrCategory(ikrCategory);
            }
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }	
	}
	
	private void updateIkrCategory(IkrCategory ikrCategory)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE IKR_CATEGORY SET UNIT_TYPE=?,UNIT=?,THRESHOLD=?,PERSISTENT=?,ARCHIVE=?,SEARCH_INDEXES=? " +
	    				"WHERE IKR_STATIC_DOMAIN_ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,ikrCategory.getIkrUnitType().name());
	        pStmt.setString(2,ikrCategory.getIkrUnit().name());
	        pStmt.setDouble(3, ikrCategory.getThreshold());
	        pStmt.setBoolean(4, ikrCategory.isPersistent());
	        pStmt.setBoolean(5, ikrCategory.isArchive());	        
	        List<String> searchIndexes = ikrCategory.getSearchesIndexes();
	        int sz = searchIndexes.size();
	        String indexeStr = "";
	        int i = 0;
	        for (String index : searchIndexes) {
	        	indexeStr  = indexeStr + index;
	        	if (i < sz-1) {
	        		indexeStr  = indexeStr + ":";
	        	}
	        	i++;
	        }
	        pStmt.setString(6, indexeStr);
	        pStmt.setInt(7,ikrCategory.getId());	
            pStmt.executeUpdate();	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }	
	}
	
	public List<Integer> getIkrStaticDomain(int id, int level) throws PersistenceException {
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
			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(selectPStmt);
			closeStatement(deletePStmt);
			closeConnection(con);
		}	
		return ids;
	}

	public void deleteIkrStaticDomain(int id, int level)
			throws PersistenceException {
		
		String query = "";
		
		switch(level) {
			case IKR_CATEGORY_LEVEL-2:
				query = "DELETE domType,metricDom,metricCat,ikrCat FROM IKR_STATIC_DOMAIN domType" +
						" LEFT JOIN IKR_STATIC_DOMAIN metricDom on metricDom.PARENT_ID=domType.ID" +
						" LEFT JOIN IKR_STATIC_DOMAIN metricCat on metricCat.PARENT_ID=metricDom.ID" +
						" LEFT JOIN IKR_CATEGORY ikrCat on ikrCat.IKR_STATIC_DOMAIN_ID=metricCat.ID" +
						" WHERE domType.ID=?";
				break;
				
			case IKR_CATEGORY_LEVEL-1:
				query = "DELETE metricDom,metricCat,ikrCat FROM IKR_STATIC_DOMAIN metricDom" +
						" LEFT JOIN IKR_STATIC_DOMAIN metricCat on metricCat.PARENT_ID=metricDom.ID" +
						" LEFT JOIN IKR_CATEGORY ikrCat on ikrCat.IKR_STATIC_DOMAIN_ID=metricCat.ID" +
						" WHERE metricDom.ID=?";
				break;
				
			case IKR_CATEGORY_LEVEL:
				query = "DELETE metricCat,ikrCat FROM IKR_STATIC_DOMAIN metricCat" +
						" LEFT JOIN IKR_CATEGORY ikrCat on ikrCat.IKR_STATIC_DOMAIN_ID=metricCat.ID" +
						" WHERE metricCat.ID=?";
				break;
		}
		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1,id);
			pStmt.executeUpdate();	
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	public IkrService getIkrService(String serviceName)
			throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM IKR_MONITOR_SERVICE WHERE SERVICE_NAME = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setString(1, serviceName);
	        rs = pStmt.executeQuery();
	        if (rs.next()) { 	  
	        	int id = rs.getInt("ID");
	        	String service = rs.getString("SERVICE_NAME");
	        	String className = rs.getString("CLASSNAME");
	          	String extension = rs.getString("EXTENSION");          	
	          	return new IkrService(id, service, className, extension);
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return null;			
	}

	public List<IkrService> getIkrServices()
			throws PersistenceException {
		List<IkrService> res = new ArrayList<IkrService>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM IKR_MONITOR_SERVICE";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        rs = pStmt.executeQuery();
	        while (rs.next()) { 	  
	        	int id = rs.getInt("ID");
	        	String service = rs.getString("SERVICE_NAME");
	        	String className = rs.getString("CLASSNAME");
	          	String extension = rs.getString("EXTENSION");             	
	          	res.add(new IkrService(id, service, className, extension));
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

	public IkrStaticDomain getIkrStaticDomain(int id)
	throws PersistenceException {
		IkrStaticDomain ikrStaticDomain = null;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT dom.*, cat.IKR_STATIC_DOMAIN_ID as CAT_ID, cat.UNIT_TYPE as CAT_UNIT_TYPE, cat.UNIT as CAT_UNIT, cat.THRESHOLD as CAT_THRESHOLD, cat.PERSISTENT as CAT_PERSISTENT, cat.ARCHIVE as CAT_ARCHIVE, cat.SEARCH_INDEXES as CAT_SEARCH_INDEXES " 
					+ "FROM IKR_STATIC_DOMAIN dom "
					+ "LEFT JOIN IKR_CATEGORY cat on cat.IKR_STATIC_DOMAIN_ID=dom.ID "
					+ "WHERE dom.ID=?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setInt(1, id);
	        rs = pStmt.executeQuery();
	        
	        if(rs.next()) { 	        	
	        	ikrStaticDomain = getIkrStaticDomain(rs);
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return ikrStaticDomain;			
	}
	
	public Map<Integer, IkrStaticDomain>  getIkrStaticDomains(Collection<Integer> ikrStaticDomainIds) throws PersistenceException {		
		Map<Integer, IkrStaticDomain> res = new HashMap<Integer, IkrStaticDomain>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT dom.*, cat.IKR_STATIC_DOMAIN_ID as CAT_ID, cat.UNIT_TYPE as CAT_UNIT_TYPE, cat.UNIT as CAT_UNIT, cat.THRESHOLD as CAT_THRESHOLD, cat.PERSISTENT as CAT_PERSISTENT, cat.ARCHIVE as CAT_ARCHIVE, cat.SEARCH_INDEXES as CAT_SEARCH_INDEXES " 
					+ "FROM IKR_STATIC_DOMAIN dom "
					+ "LEFT JOIN IKR_CATEGORY cat on cat.IKR_STATIC_DOMAIN_ID=dom.ID "
					+ getWhereClause("dom.ID", ikrStaticDomainIds);
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        rs = pStmt.executeQuery();
	        while(rs.next()) { 			      	
		      	IkrStaticDomain ikrStaticDomain = getIkrStaticDomain(rs);		      	
		      	res.put(ikrStaticDomain.getId(), ikrStaticDomain);
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
	
	public List<Integer> getIkrStaticDomainIds(int parentId)throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		String query = "SELECT ID FROM IKR_STATIC_DOMAIN WHERE PARENT_ID = ?";
		
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());  
		    pStmt.setInt(1, parentId);
		    rs = pStmt.executeQuery();		    
			while(rs.next()) { 	
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
	
	
	public int getIkrStaticDomainIdByValue(String value)
	throws PersistenceException {
		int res = 0;
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT ID FROM IKR_STATIC_DOMAIN WHERE DOMAIN_VALUE=?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setString(1, value);
	        rs = pStmt.executeQuery();
	        if(rs.next()) { 	  
	        	res = rs.getInt("ID");
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
	
	public List<Integer> loadIkrStaticDomains() throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT ID FROM IKR_STATIC_DOMAIN";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());  
		    rs = pStmt.executeQuery();
		    while(rs.next()) { 			      	
		    	int id = rs.getInt("ID");	      	
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
	
	public List<IkrStaticDomain> getIkrStaticDomains()
	throws PersistenceException {
		List<IkrStaticDomain> res = new ArrayList<IkrStaticDomain>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT dom.*, cat.IKR_STATIC_DOMAIN_ID as CAT_ID, cat.UNIT_TYPE as CAT_UNIT_TYPE, cat.UNIT as CAT_UNIT, cat.THRESHOLD as CAT_THRESHOLD, cat.PERSISTENT as CAT_PERSISTENT, cat.ARCHIVE as CAT_ARCHIVE, cat.SEARCH_INDEXES as CAT_SEARCH_INDEXES " 
						+ "FROM IKR_STATIC_DOMAIN dom "
						+ "LEFT JOIN IKR_CATEGORY cat on cat.IKR_STATIC_DOMAIN_ID=dom.ID ";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());  
		    rs = pStmt.executeQuery();
		    while(rs.next()) { 			      	
		      	IkrStaticDomain ikrStaticDomain = getIkrStaticDomain(rs);		      	
		      	res.add(ikrStaticDomain);
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
	
	private IkrStaticDomain getIkrStaticDomain(ResultSet rs) throws SQLException {
		int id = rs.getInt("ID");
    	int parentId = rs.getInt("PARENT_ID");
    	String value = rs.getString("DOMAIN_VALUE");
    	String label = rs.getString("DOMAIN_LABEL");
      	String description = rs.getString("DESCRIPTION");
      	
      	IkrStaticDomain ikrStaticDomain = null;
      	if (rs.getInt("CAT_ID") != 0) {
           	String unitType = rs.getString("CAT_UNIT_TYPE");
           	String unit = rs.getString("CAT_UNIT");		
        	double threshold = rs.getDouble("CAT_THRESHOLD");
        	boolean persistent = rs.getBoolean("CAT_PERSISTENT");
        	boolean archive = rs.getBoolean("CAT_ARCHIVE");
        	String indexeStr = rs.getString("CAT_SEARCH_INDEXES");
        	List<String> searchIndexes = new ArrayList<String>();
			if (indexeStr!=null && indexeStr.length()>0) {
				String[] indexes = indexeStr.split(":");
				searchIndexes = Arrays.asList(indexes);
			}
           	IkrUnitType ikrUnitType = null;		           	
           	try {
           		ikrUnitType = IkrUnitType.valueOf(unitType);
           	} catch (Exception exc) {
           		LOG.error("Impossible to create category because of wrong IkrUnitType: " + unitType);
           		return null;
           	}		           	
            IkrUnit ikrUnit = null;
            if (ikrUnitType != null && unit != null) {
            	ikrUnit = ikrUnitType.getIkrUnit(unit);
            }
            
            ikrStaticDomain = new IkrCategory(id, parentId, value, label, description, ikrUnitType, ikrUnit, threshold, persistent, archive, searchIndexes);
      	}
      	else {
      		ikrStaticDomain = new IkrStaticDomain(id, parentId, value, label, description);
      	}
      	
      	return ikrStaticDomain;
	}
	
	private void createIkrCategory(IkrCategory category) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO IKR_CATEGORY (IKR_STATIC_DOMAIN_ID,UNIT_TYPE,UNIT,THRESHOLD,PERSISTENT,ARCHIVE,SEARCH_INDEXES) VALUES (?,?,?,?,?,?,?)";            
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,category.getId());
	        pStmt.setString(2,category.getIkrUnitType().name());	        
	        String unit = category.getIkrUnit() != null ?  category.getIkrUnit().name() : null;	        
	        pStmt.setString(3, unit);     
	        pStmt.setDouble(4, category.getThreshold());
	        pStmt.setBoolean(5, category.isPersistent());
	        pStmt.setBoolean(6, category.isArchive());	        
	        List<String> searchIndexes = category.getSearchesIndexes();
			int sz = searchIndexes.size();
	        String indexeStr = "";
	        int i = 0;
	        for (String index : searchIndexes) {
	        	indexeStr  = indexeStr + index;
	        	if (i < sz-1) {
	        		indexeStr  = indexeStr + ":";
	        	}
	        	i++;
	        }
	        
	        pStmt.setString(7, indexeStr);
            pStmt.executeUpdate();            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}

	public void addIkrService(IkrService service) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "INSERT INTO IKR_MONITOR_SERVICE (SERVICE_NAME,CLASSNAME,EXTENSION) VALUES (?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,service.getServiceName());
	        pStmt.setString(2,service.getClassname());	
	        pStmt.setString(3,(service.getExtension()!=null)?service.getExtension():"");	
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }			
	}

	public void deleteIkrService(String serviceName)
			throws PersistenceException {
		String query = "DELETE FROM IKR_MONITOR_SERVICE WHERE SERVICE_NAME = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setString(1,serviceName);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}

	public void updateIkrService(IkrService service)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE IKR_MONITOR_SERVICE SET SERVICE_NAME=?,CLASSNAME=?,EXTENSION=? " +
	    				"WHERE SERVICE_NAME=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,service.getServiceName());
	        pStmt.setString(2,service.getClassname());	
	        pStmt.setString(3,service.getExtension());	
	        pStmt.setString(4,service.getServiceName());	
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }			
	}	
	
	private void addMetricDomainConfigFields(List<MetricDomainConfigField> fields, long metricDomainConfigId) throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "INSERT INTO METRIC_DOMAIN_CONFIG_FIELDS (METRIC_DOMAIN_CONFIG_ID,NAME,LABEL,ENABLE,FIELD_TYPE,FIELD_TYPE_VALUES) VALUES (?,?,?,?,?,?)";	       
		for (MetricDomainConfigField field : fields) {
			try {
				con = dataSource.getConnection();
			    pStmt = con.prepareStatement(query.toUpperCase());
			    pStmt.setLong(1,metricDomainConfigId);
			    pStmt.setString(2,field.getName());
			    pStmt.setString(3,field.getLabel());	
			    pStmt.setBoolean(4,field.isEnable());	
			    pStmt.setString(5,field.getFieldType());
			    String fieldTypeValuesStr = "";
			    List<String> fieldTypeValues = field.getFieldTypeValues(); 
			    int i = 0;
			    int sz = fieldTypeValues.size();
			    for (String value : fieldTypeValues) {
			    	fieldTypeValuesStr = fieldTypeValuesStr + value;
			    	if (i < sz -1) {
			    		fieldTypeValuesStr = fieldTypeValuesStr + ",";
			    	}
			    	i++;
			    }
			    
			    pStmt.setString(6,fieldTypeValuesStr);
			    pStmt.executeUpdate();	            
			}	catch(SQLException e) {
				LOG.error(e.getMessage(), e);
			} finally {
			    closeStatement(pStmt);
			    closeConnection(con);
			}
		}
	}

	private void deleteMetricDomainConfigFields(long metricDomainConfigId) throws PersistenceException {
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_FIELDS WHERE METRIC_DOMAIN_CONFIG_ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,metricDomainConfigId);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	private void addMetricDomainConfigAttributes(Map<String, String> attributes, long metricDomainConfigId) throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "INSERT INTO METRIC_DOMAIN_CONFIG_ATTRIBUTE (METRIC_DOMAIN_CONFIG_ID,ATTRIBUTE_KEY,ATTRIBUTE_VALUE) VALUES (?,?,?)";	       
		for (String attrKey : attributes.keySet()) {
			try {
				con = dataSource.getConnection();
			    pStmt = con.prepareStatement(query.toUpperCase());
			    pStmt.setLong(1, metricDomainConfigId);
			    pStmt.setString(2,attrKey);
			    pStmt.setString(3,attributes.get(attrKey));
			    pStmt.executeUpdate();	            
			}	catch(SQLException e) {
				LOG.error(e.getMessage(), e);
			} finally {
			    closeStatement(pStmt);
			    closeConnection(con);
			}
		}
	}

	private void deleteMetricDomainConfigAttributes(long metricDomainConfigId) throws PersistenceException {
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_ATTRIBUTE WHERE METRIC_DOMAIN_CONFIG_ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,metricDomainConfigId);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
//	public void cleanMetricDomainConfigField() throws PersistenceException {
//		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_FIELDS";		
//		Connection con = null;
//		PreparedStatement pStmt = null;		
//		try {
//			con = dataSource.getConnection();		
//			pStmt = con.prepareStatement(query.toUpperCase()); 
//			pStmt.executeUpdate();
//		
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeStatement(pStmt);
//			closeConnection(con);
//		}	
//	}

	private List<MetricDomainConfigField> getMetricDomainConfigFields(int metricDomainConfigId) 
	throws PersistenceException {
		List<MetricDomainConfigField> res = new ArrayList<MetricDomainConfigField>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG_FIELDS WHERE METRIC_DOMAIN_CONFIG_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setInt(1, metricDomainConfigId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) {
	        	int id = rs.getInt("ID");
	        	String name = rs.getString("NAME");
	        	String label = rs.getString("LABEL");
	        	boolean enable = rs.getBoolean("ENABLE");
	        	String fieldType = rs.getString("FIELD_TYPE");
	        	String fieldTypeValuesStr = rs.getString("FIELD_TYPE_VALUES");
	        	List<String> fieldTypeValues = new ArrayList<String>();
	        	if (fieldTypeValuesStr!=null && fieldTypeValuesStr.length()>0){
	        		fieldTypeValues = Arrays.asList(fieldTypeValuesStr.split(","));
	        	}	        		
	        	res.add(new MetricDomainConfigField(id, metricDomainConfigId, name, label, enable, fieldType, fieldTypeValues));
	        }
		} catch(SQLException e) {
			LOG.error(e);
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			} catch (PersistenceException e) {
				LOG.error(e);
			}
		}
		return res;
	}
	
	private Map<String, String> getMetricDomainConfigAttributes(int metricDomainConfigId) 
	throws PersistenceException {
		Map<String, String> res = new HashMap<String, String>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG_ATTRIBUTE WHERE METRIC_DOMAIN_CONFIG_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setInt(1, metricDomainConfigId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) {
	        	String attrKey = rs.getString("ATTRIBUTE_KEY");
	        	String attrValue = rs.getString("ATTRIBUTE_VALUE");
	        	res.put(attrKey, attrValue);
	        }
		} catch(SQLException e) {
			LOG.error(e);
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			} catch (PersistenceException e) {
				LOG.error(e);
			}
		}
		return res;
	}

//	public void updateMetricDomainConfigField(MetricDomainConfigField field)
//	throws PersistenceException {
//		Connection con = null;
//	    PreparedStatement pStmt = null;
//	    String query = "UPDATE METRIC_DOMAIN_CONFIG_FIELDS SET METRIC_DOMAIN_CONFIG_ID=?,NAME=?,LABEL=?,ENABLE=? " +
//	    				"WHERE ID=?";	       
//	    try {
//	    	con = dataSource.getConnection();
//	        pStmt = con.prepareStatement(query.toUpperCase());
//	        pStmt.setInt(1,field.getMetricDomainConfigId());
//	        pStmt.setString(2,field.getName());
//	        pStmt.setString(3,field.getLabel());	
//	        pStmt.setBoolean(4,field.isEnable());
//	        pStmt.setInt(5,field.getId());
//            pStmt.executeUpdate();	            
//	    }	catch(SQLException e) {
//        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//        } finally {
//            closeStatement(pStmt);
//            closeConnection(con);
//        }		
//	}

	public List<LogicalEnv> getLogicalEnvs()
	throws PersistenceException {
		List<LogicalEnv> res = new ArrayList<LogicalEnv>();
		
		String query = "SELECT * FROM LOGICAL_ENV ORDER BY NAME";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());  
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
        	int id = rs.getInt("ID");
          	String name = rs.getString("NAME");
          	String description = rs.getString("DESCRIPTION");
          	
          	LogicalEnv logicalEnv = new LogicalEnv(id, name, (description!=null)?description:"");
          	
          	res.add(logicalEnv);
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
	
	public void createLogicalEnv(LogicalEnv env)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO LOGICAL_ENV (NAME,DESCRIPTION) VALUES (?,?)"; 
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,env.getName());
	        pStmt.setString(2,env.getDescription());	        
            pStmt.executeUpdate();            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updateLogicalEnv(LogicalEnv env)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE LOGICAL_ENV SET NAME=?,DESCRIPTION=? WHERE ID=?";  
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,env.getName());
	        pStmt.setString(2,env.getDescription());	
	        pStmt.setInt(3, env.getId());
            pStmt.executeUpdate();            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void deleteLogicalEnv(int envId)
	throws PersistenceException {
		String query = "DELETE FROM LOGICAL_ENV WHERE ID=?";
		Connection con = null;
		PreparedStatement pStmt = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,envId);
			pStmt.executeUpdate();

		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
//	public List<AgentConfig> loadAgentConfigs()
//	throws PersistenceException {
//		List<AgentConfig> res = new ArrayList<AgentConfig>();
//			
//		String query = "SELECT * FROM AGENT ORDER BY NAME";
//		
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//          con = dataSource.getConnection();
//
//          pStmt = con.prepareStatement(query);  
//          rs = pStmt.executeQuery();
//
//          while (rs.next()) {       	
//          	long id = rs.getLong("ID");
//          	String name = rs.getString("NAME");
//          	String type = rs.getString("AGENT_TYPE");       	
//          	
//          	AgentConfig config = new AgentConfig(id,
//          										 name,
//          										 type);
//          								
//          	res.add(config);
//          }
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}				
//
//		return res;
//	}	
//	
//	public AgentConfig getAgentConfig(int id) throws PersistenceException {
//		String query = "SELECT * FROM AGENT WHERE ID=?";	
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//		try {
//          con = dataSource.getConnection();
//          pStmt = con.prepareStatement(query);  
//          pStmt.setInt(1, id);
//          rs = pStmt.executeQuery();
//          if(rs.next()) {       	
//          	String name = rs.getString("NAME");
//          	String type = rs.getString("AGENT_TYPE"); 
//          	return new AgentConfig(id,name,type);
//          }
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}		
//		return null;
//	}
	
	public Map<Long,MonitorConfig> loadMonitorConfigs(Collection<Long> monitorIds)
	throws PersistenceException {
		Map<Long,MonitorConfig> res = new HashMap<Long,MonitorConfig>();
		
		String query = "SELECT * FROM MONITOR "				
					+ getWhereClause("ID", monitorIds);

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());
          rs = pStmt.executeQuery();

          while (rs.next()) {       	          	
          	MonitorConfig config = getMonitorConfig(rs);          	
          	if (config!= null)
          		res.put(config.getId(),config);
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
	
	public Collection<Long> loadMonitorConfigs(int logicalEnvId)
	throws PersistenceException {
		Collection<Long> res = new ArrayList<Long>();
		
//		String query = "SELECT ID FROM MONITOR WHERE LOGICAL_ENV_ID = ?";
		String query = "SELECT ID FROM MONITOR";
		if (logicalEnvId>0)
			query = query + " WHERE LOGICAL_ENV_ID = ?";
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
        	  long monitorId = rs.getLong("ID");
        	  res.add(monitorId);
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

	public MonitorConfig loadMonitorConfig(long monitorId)
	throws PersistenceException {
		MonitorConfig res = null;
		
		String query = "SELECT * FROM MONITOR WHERE ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
          con = dataSource.getConnection();

          pStmt = con.prepareStatement(query.toUpperCase());
          pStmt.setLong(1, monitorId);
          rs = pStmt.executeQuery();

          while (rs.next()) {       	
        	 res = getMonitorConfig(rs);
          }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
//		Collection<Integer> connectorIds = getMonitorConnectorIds(res.getId());
//		res.setConnectorConfigIds(connectorIds);
		
		return res;
	}	
	
	private Map<String, String> loadMonitorAttributes(long monitorId) throws PersistenceException {
		Map<String, String> res = new HashMap<String, String>();
		
		String query = "SELECT * FROM MONITOR_ATTRIBUTE WHERE MONITOR_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, monitorId);
			rs = pStmt.executeQuery();
			while (rs.next()) {       	
				res.put(rs.getString("NAME"), rs.getString("ATTR_VALUE"));
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
	
	private IkrMonitorSchedulerConfig getSchedulerConfig(long monitorId) throws PersistenceException {
		IkrMonitorSchedulerConfig config = null;
		
		String monitorSchedQuery = "SELECT * FROM MONITOR_SCHEDULER WHERE MONITOR_ID=?";
		String schedTimeQuery = "SELECT * FROM MONITOR_SCHEDULER_TIME WHERE MONITOR_SCHEDULER_ID=?";

		Connection con = null;
		
		PreparedStatement monitorSchedPStmt = null;
		ResultSet monitorSchedRs = null;
		PreparedStatement schedTimePStmt = null;
		ResultSet schedTimeRs = null;

		try {
			con = dataSource.getConnection();
			
			monitorSchedPStmt = con.prepareStatement(monitorSchedQuery.toUpperCase());
			monitorSchedPStmt.setLong(1, monitorId);		
			
			monitorSchedRs = monitorSchedPStmt.executeQuery();
			long captureDelay = 0;
			String schedType = null;
			String schedMode = null;
			int schedId = 0;
			if (monitorSchedRs.next()) {
				schedId = monitorSchedRs.getInt("ID");
				captureDelay = monitorSchedRs.getLong("CAPTURE_DELAY");
				schedType = monitorSchedRs.getString("SCHEDULING_TYPE");
				schedMode = monitorSchedRs.getString("SCHEDULING_MODE");
			}
			
			schedTimePStmt = con.prepareStatement(schedTimeQuery.toUpperCase());
			schedTimePStmt.setLong(1, schedId);
			schedTimeRs = schedTimePStmt.executeQuery();
			Calendar startDate = null;
			Calendar endDate = null;
			while (schedTimeRs.next()) {
				String type = schedTimeRs.getString("SCHED_TIME_TYPE");
				int day = schedTimeRs.getInt("SCHED_DAY");
				int hour = schedTimeRs.getInt("SCHED_HOUR");
				int min = schedTimeRs.getInt("SCHED_MIN");
				if (IkrMonitorSchedulerConfig.START_TIME.equals(type)) {
					startDate = Calendar.getInstance();
					startDate.setTime(new Date());
					if (IkrMonitorSchedulerConfig.WEEKLY.equals(schedMode))
						startDate.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrMonitorSchedulerConfig.MONTHLY.equals(schedMode))
						startDate.set(Calendar.DAY_OF_MONTH, day);
					startDate.set(Calendar.HOUR_OF_DAY, hour);
					startDate.set(Calendar.MINUTE, min);
				}
				else if (IkrMonitorSchedulerConfig.END_TIME.equals(type)) {
					endDate = Calendar.getInstance();
					endDate.setTime(new Date());
					if (IkrMonitorSchedulerConfig.WEEKLY.equals(schedMode))
						endDate.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrMonitorSchedulerConfig.MONTHLY.equals(schedMode))
						endDate.set(Calendar.DAY_OF_MONTH, day);
					endDate.set(Calendar.HOUR_OF_DAY, hour);
					endDate.set(Calendar.MINUTE, min);
				}
			}
			
			config = new IkrMonitorSchedulerConfig(schedId, schedType, schedMode, startDate, endDate, captureDelay);
			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(monitorSchedRs);
			closeStatement(monitorSchedPStmt);
			closeResultSet(schedTimeRs);
			closeStatement(schedTimePStmt);
			closeConnection(con);
		}				

		return config;
	}
	
	public void addMetricDomainConfig(MetricDomainConfig config, long nextId)
	throws PersistenceException {		
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "INSERT INTO METRIC_DOMAIN_CONFIG (ID,CONNECTOR_TYPE,IKR_STATIC_DOMAIN_ID,DESCRIPTION,CLASSNAME,SYNC_USE) VALUES (?,?,?,?,?,?)";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setLong(1,nextId);
		    
			String connectorTypes = "";
			if (config.getConnectorTypes() != null) {
				for (String type : config.getConnectorTypes()) {
					connectorTypes = connectorTypes + "," + type;
				}
				connectorTypes = connectorTypes.substring(1);
			}
		   
		    pStmt.setString(2,connectorTypes);
		    pStmt.setInt(3,config.getIkrStaticDomainId());
		    pStmt.setString(4,config.getDescription());
		    pStmt.setString(5,config.getClassName());
		    pStmt.setBoolean(6, config.isUseDataSynchronization());
		    pStmt.executeUpdate();	
		    
		    addMetricDomainConfigExtension(config.getExtensionConfigs(), nextId);	
		    addMetricDomainItems(config.getDomainItemConfigs(), nextId);
		    addMetricDomainConfigFields(config.getFields(), nextId);
		    addMetricDomainConfigResource(config.getResources(), nextId);
		    addMetricDomainConfigAttributes(config.getAttributes(), nextId);
		    
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}
	
	private void deleteMetricDomainConfigExtension(long metricDomainConfigId) throws PersistenceException {
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_EXTENSION WHERE METRIC_DOMAIN_CONFIG_ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,metricDomainConfigId);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	private void deleteMetricDomainConfigResource(long metricDomainConfigId) throws PersistenceException {
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_RESOURCES WHERE METRIC_DOMAIN_CONFIG_ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,metricDomainConfigId);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	private void addMetricDomainConfigExtension(List<MetricDomainConfigExtension> extensionConfigs, long metricDomainConfigId) throws PersistenceException {		
		String query = "INSERT INTO METRIC_DOMAIN_CONFIG_EXTENSION (METRIC_DOMAIN_CONFIG_ID,PRIORITY,DESCRIPTION,CLASSNAME) VALUES (?,?,?,?)";	    
		for (MetricDomainConfigExtension config : extensionConfigs) {
			Connection con = null;
			PreparedStatement pStmt = null;
			try {		
				con = dataSource.getConnection();
			    pStmt = con.prepareStatement(query.toUpperCase());
			    pStmt.setLong(1,metricDomainConfigId);		    
			    pStmt.setInt(2,config.getPriority());
			    pStmt.setString(3,config.getDescription());
			    pStmt.setString(4,config.getClassName());
			    pStmt.executeUpdate();	            
			}	catch(SQLException e) {
				LOG.error(e.getMessage(), e);
			} finally {
			    closeStatement(pStmt);
			    closeConnection(con);
			}
		}
	}
	
	private void addMetricDomainConfigResource(List<MetricDomainConfigResource> resources, long metricDomainConfigId) throws PersistenceException {		
		String query = "INSERT INTO METRIC_DOMAIN_CONFIG_RESOURCES (METRIC_DOMAIN_CONFIG_ID,METRIC_DOMAIN_RESOURCE_ID,ENABLED) VALUES (?,?,?)";	    
		for (MetricDomainConfigResource resource : resources) {
			Connection con = null;
			PreparedStatement pStmt = null;
			try {		
				con = dataSource.getConnection();
			    pStmt = con.prepareStatement(query.toUpperCase());
			    pStmt.setLong(1,metricDomainConfigId);		    
			    pStmt.setLong(2,resource.getResource().getId());
			    pStmt.setBoolean(3,resource.isEnabled());
			    pStmt.executeUpdate();	            
			}	catch(SQLException e) {
				LOG.error(e.getMessage(), e);
			} finally {
			    closeStatement(pStmt);
			    closeConnection(con);
			}
		}
	}
	
	private List<MetricDomainConfigExtension> getMetricDomainConfigExtensions(int metricDomainConfigId) throws PersistenceException {
		List<MetricDomainConfigExtension> res = new ArrayList<MetricDomainConfigExtension>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG_EXTENSION WHERE METRIC_DOMAIN_CONFIG_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,metricDomainConfigId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) { 	
	        	int priority = rs.getInt("PRIORITY");
	        	String description = rs.getString("DESCRIPTION");
	        	String className = rs.getString("CLASSNAME");	        	
	        	res.add(new MetricDomainConfigExtension(metricDomainConfigId, priority, description, className));
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
	
	private List<MetricDomainConfigResource> getMetricDomainConfigResources(int metricDomainConfigId) throws PersistenceException {
		List<MetricDomainConfigResource> res = new ArrayList<MetricDomainConfigResource>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT res.ID, res.IKR_STATIC_DOMAIN_ID, res.RESOURCE_NAME, confRes.ENABLED " + 
						"FROM METRIC_DOMAIN_CONFIG_RESOURCES confRes, METRIC_DOMAIN_RESOURCE res " + 
						"WHERE confRes.METRIC_DOMAIN_RESOURCE_ID = res.ID AND confRes.METRIC_DOMAIN_CONFIG_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,metricDomainConfigId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) { 	
	        	int resourceId = rs.getInt("ID");
	        	int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
	        	String resourceName = rs.getString("RESOURCE_NAME");
	        	boolean enabled = rs.getBoolean("ENABLED");	        	
	        	res.add(new MetricDomainConfigResource(metricDomainConfigId, new MetricDomainResource(resourceId, ikrStaticDomainId, resourceName), enabled));
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
	
	public void updateMetricDomainConfig(MetricDomainConfig config)
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "UPDATE METRIC_DOMAIN_CONFIG SET CONNECTOR_TYPE=?,DESCRIPTION=?,CLASSNAME=?,SYNC_USE=? WHERE ID=?";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    
			String connectorTypes = "";
			if (config.getConnectorTypes() != null) {
				for (String type : config.getConnectorTypes()) {
					connectorTypes = connectorTypes + "," + type;
				}
				connectorTypes = connectorTypes.substring(1);
			}
		    
		    pStmt.setString(1,connectorTypes);
		    pStmt.setString(2,config.getDescription());
		    pStmt.setString(3,config.getClassName());
		    pStmt.setBoolean(4,config.isUseDataSynchronization());
		    pStmt.setLong(5, config.getId());
		    pStmt.executeUpdate();	  
		    
		    deleteMetricDomainConfigExtension(config.getId());
		    addMetricDomainConfigExtension(config.getExtensionConfigs(), config.getId());	
		    
		    deleteMetricDomainItems(config.getId());
		    addMetricDomainItems(config.getDomainItemConfigs(), config.getId());
		    
		    deleteMetricDomainConfigFields(config.getId());
		    addMetricDomainConfigFields(config.getFields(), config.getId());
		    
		    deleteMetricDomainConfigResource(config.getId());
		    addMetricDomainConfigResource(config.getResources(), config.getId());
		    
		    deleteMetricDomainConfigAttributes(config.getId());
		    addMetricDomainConfigAttributes(config.getAttributes(), config.getId());
		    
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}	
	
	public void removeMetricDomainConfig(long configId)
	throws PersistenceException {	
		Connection con = null;
	    PreparedStatement pStmt = null;
	    PreparedStatement pStmt2 = null;
		String query1 = "DELETE FROM METRIC_DOMAIN_CONFIG WHERE ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query1.toUpperCase());
	        pStmt.setLong(1, configId);
            pStmt.executeUpdate();
            
            deleteMetricDomainConfigExtension(configId);
            deleteMetricDomainConfigFields(configId);
            deleteMetricDomainItems(configId);
            deleteMetricDomainConfigResource(configId);
            deleteMetricDomainConfigAttributes(configId);
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeStatement(pStmt2);
            closeConnection(con);
        }
	}	
	
	
	public void cleanMetricDomainItems() throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "DELETE FROM METRIC_DOMAIN_CONFIG_ITEMS";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.executeUpdate();
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
	}
	
	public void cleanMetricDomainConfig()
	throws PersistenceException {	
		Connection con = null;
	    PreparedStatement pStmt = null;
	    PreparedStatement pStmt2 = null;
		String query1 = "DELETE FROM METRIC_DOMAIN_CONFIG";
		String query2 = "DELETE FROM METRIC_DOMAIN_CONFIG_EXTENSION";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query1.toUpperCase());
	        pStmt.executeUpdate();
            
	        pStmt2 = con.prepareStatement(query2.toUpperCase());
	        pStmt2.executeUpdate();
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeStatement(pStmt2);
            closeConnection(con);
        }
	}	
	
	public List<MetricDomainConfig> loadMetricDomainConfigs() 
	throws PersistenceException {
		List<MetricDomainConfig> res = new ArrayList<MetricDomainConfig>();

		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();
			
			while (rs.next()) {
				res.add(getMetricDomainConfigs(rs));
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
	
	public List<MetricDomainConfig> getMetricDomainConfigs(int metricDomainId) throws PersistenceException {
		List<MetricDomainConfig> res = new ArrayList<MetricDomainConfig>();

		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG WHERE IKR_STATIC_DOMAIN_ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, metricDomainId);
			rs = pStmt.executeQuery();
			
			while (rs.next()) {
				res.add(getMetricDomainConfigs(rs));
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
	
	public MetricDomainConfig getMetricDomainConfig(int ikrStaticDomainId, String classname, String description) 
	throws PersistenceException {
		MetricDomainConfig res = null;

		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG WHERE IKR_STATIC_DOMAIN_ID=? AND CLASSNAME=? AND DESCRIPTION=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, ikrStaticDomainId);
			pStmt.setString(2, classname);
			pStmt.setString(3, description);
			rs = pStmt.executeQuery();			
			if (rs.next()) {
				res = getMetricDomainConfigs(rs);
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
	
	public MetricDomainConfig getMetricDomainConfig(int metricDomainConfigId) throws PersistenceException {
		return loadMetricDomainConfig(metricDomainConfigId);
	}
	
	private MetricDomainConfig loadMetricDomainConfig(int id) 
	throws PersistenceException {
		MetricDomainConfig res = null;

		String query = "SELECT * FROM METRIC_DOMAIN_CONFIG WHERE ID=?";
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, id);
			rs = pStmt.executeQuery();
			
			if (rs.next()) {
				res = getMetricDomainConfigs(rs);
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
		
	private MetricDomainConfig getMetricDomainConfigs(ResultSet rs)
	throws SQLException {
		MetricDomainConfig res = null;

		Collection<String> conTypes = new ArrayList<String>();
		int metricDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
		
		int id = rs.getInt("ID");
		String description = rs.getString("DESCRIPTION");
		String className = rs.getString("CLASSNAME");
		String connectorTypes = rs.getString("CONNECTOR_TYPE");
		boolean useDataSynchronization = rs.getBoolean("SYNC_USE");
		
		if (connectorTypes != null) {
			conTypes.addAll(Arrays.asList(connectorTypes.split(",")));
		}
		
		List<String> items = new ArrayList<String>();
		try {
			items = getMetricDomainItems(id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
		}
		
		List<MetricDomainConfigField> fields = new ArrayList<MetricDomainConfigField>();
		try {
			fields = getMetricDomainConfigFields(id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
		}
		
		List<MetricDomainConfigExtension> extensionConfigs = new ArrayList<MetricDomainConfigExtension>();
		try {
			extensionConfigs = getMetricDomainConfigExtensions(id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
		}
		
		List<MetricDomainConfigResource> resources = new ArrayList<MetricDomainConfigResource>();
		try {
			resources = getMetricDomainConfigResources(id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
		}
		
		Map<String, String> attributes = new HashMap<String, String>();
		try {
			attributes = getMetricDomainConfigAttributes(id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
		}
		
		res = new MetricDomainConfig(id,metricDomainId,conTypes,items,description,className,useDataSynchronization,fields,extensionConfigs,resources,attributes);	
		
		return res;
	}
	
	private MonitorConfig getMonitorConfig(ResultSet rs)
	throws SQLException, PersistenceException {
      	long id = rs.getLong("ID");
      	String name = rs.getString("NAME");      	
      	int logicalEnvId = rs.getInt("LOGICAL_ENV_ID");
      	int metricDomainConfigId = rs.getInt("METRIC_DOMAIN_CONFIG_ID");
      	boolean started = rs.getBoolean("STARTED");     	
      	
      	// String label;
      	MonitorConfig config = null;
		try {
			// Metric Domain Config
			MetricDomainConfig metricDomainConfig = loadMetricDomainConfig(metricDomainConfigId);
			
			// Metric Domain Resource associated
//			Map<String, MetricDomainResource> domainResources = loadMetricDomainResources(metricDomainConfig.getIkrStaticDomainId());
			
			//get Scheduler Config
			IkrMonitorSchedulerConfig schedulerCong = getSchedulerConfig(id);			
			
			config = new MonitorConfig(id,
									   logicalEnvId,
									   name,
									   metricDomainConfig,
									   schedulerCong,
									   started);
			try {				
				config.setAttributes(loadMonitorAttributes(id));
				config.setConnectorConfigIds(getMonitorConnectorIds(id));
			} catch (PersistenceException e) {
				LOG.error(e);
			}      	
		} catch (PersistenceException e1) {
			throw new PersistenceException(e1.getMessage(), e1, BaseException.EXCEPTION);
		}       	
      	return config;
	}
	
//	private Map<String, MetricDomainResource> loadMetricDomainResources(int ikrStaticDomainId) throws PersistenceException {
//		Map<String, MetricDomainResource> res = new HashMap<String, MetricDomainResource>();
//		
//		String query = "SELECT * FROM METRIC_DOMAIN_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=?";
//
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//		try {
//			con = dataSource.getConnection();
//			pStmt = con.prepareStatement(query.toUpperCase());
//			pStmt.setInt(1, ikrStaticDomainId);
//			rs = pStmt.executeQuery();
//			while (rs.next()) {    
//				int metricDomainResourceId = rs.getInt("ID");
//				int ikrStaticDomainParentId = rs.getInt("IKR_STATIC_DOMAIN_ID");
//				String metricDomainResourceName = rs.getString("RESOURCE_NAME");
//				MetricDomainResource domainResource = new MetricDomainResource(metricDomainResourceId, ikrStaticDomainParentId, metricDomainResourceName);
//				res.put(metricDomainResourceName, domainResource);
//			}
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
// 				closeStatement(pStmt);
//			closeConnection(con);
//		}				
//
//		return res;
//	}
	
	public String getMonitorLabel(String agentType, String monitortype)
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM IKR_STATIC_DOMAIN WHERE DOMAIN_TYPE = ? AND DOMAIN_VALUE = ?";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());  
		    pStmt.setString(1, agentType);
		    pStmt.setString(2, monitortype);
		    rs = pStmt.executeQuery();
		    if(rs.next()) { 	  
		    	return rs.getString("DOMAIN_LABEL");
		    }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return null;			
	}
	
//	public IkrCategory getIkrCategory(Integer id)
//	throws PersistenceException {	
//		IkrCategory ikrCategory = null;
//		
//		String query = "SELECT * FROM IKR_CATEGORY WHERE ID=?";
//		
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query);
//			pStmt.setInt(1, id);
//			rs = pStmt.executeQuery();
//
//			if (rs.next()) {  
//				ikrCategory = getIkrCategory(rs);
//			}
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}	
//		return ikrCategory;
//	}
	
//	public Map<Integer,IkrCategory> getIkrCategories(Collection<Integer> ids)
//	throws PersistenceException {
//		Map<Integer,IkrCategory> res = new HashMap<Integer,IkrCategory>();
//		
//		String query = "SELECT * FROM IKR_CATEGORY";		
//		String whereClause = getWhereClause("ID", ids);
//		
//		query += whereClause;
//		
//		
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query);  
//			rs = pStmt.executeQuery();
//
//			while (rs.next()) {  
//				try {
//					IkrCategory ikrCategory = getIkrCategory(rs);
//					if (ikrCategory != null) {
//						res.put(ikrCategory.getId(),ikrCategory);
//					}
//				} catch(Exception e) {
//					LOG.error(e.getMessage(), e);
//				}
//			}
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}				
//
//		return res;
//	}
	
//	private IkrCategory getIkrCategory(ResultSet rs) 
//	throws SQLException {
//      	int id = rs.getInt("ID");
//      	String identifier = rs.getString("IDENTIFIER");
//       	int metricDomainId = rs.getInt("METRIC_DOMAIN_ID");
//       	String name = rs.getString("NAME");
//       	String label = rs.getString("LABEL");
//       	boolean computed = rs.getBoolean("COMPUTED");
//       	String unitType = rs.getString("VALUE_TYPE");
//       	String unit = rs.getString("UNIT_TYPE");
//       	String description = rs.getString("DESCRIPTION");
//       	
//       	IkrUnitType ikrUnitType = null;
//       	
//       	try {
//       		ikrUnitType = IkrUnitType.valueOf(unitType);
//       	} catch (Exception exc) {
//       		LOG.error("Impossible to create category because of wrong IkrUnitType: " + identifier);
//       		return null;
//       	}
//       	
//        IkrUnit ikrUnit = null;
//        if (ikrUnitType != null && unit != null) {
//        	ikrUnit = ikrUnitType.getIkrUnit(unit);
//        }
//       		
//       	IkrCategory ikrCategory = new IkrCategory(id,
//       											  identifier,
//       											  metricDomainId,
//       											  name,
//       											  label,
//       											  computed,
//       											  ikrUnitType,
//       											  ikrUnit,
//       											  description);
//		return ikrCategory;
//	}
	
	
//	public void createActivatedCategory(long monitorId, String ikrCategoryGroup)
//	throws PersistenceException {
//		
//		Connection con = null;
//        PreparedStatement pStmt = null;
//        
//        String query = "INSERT INTO ACTIVATED_IKR_CATEGORY (MONITOR_ID,IKR_CATEGORY_ID,ENABLE) SELECT ?,ID,? FROM IKR_CATEGORY WHERE "
//					+ "IKR_CATEGORY_GROUP=? ";
//
//        try {
//            con = dataSource.getConnection();
//
//            pStmt = con.prepareStatement(query);
//            pStmt.setLong(1,monitorId);
//            pStmt.setBoolean(2,true);
//            pStmt.setString(3,ikrCategoryGroup);
//           	pStmt.executeUpdate();
//          
//        } catch(SQLException e) {
//        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//        } finally {
//            closeStatement(pStmt);
//            closeConnection(con);
//        }	
//	}	
	
	public long createMonitor(MonitorConfig monitorConfig)
	throws PersistenceException {
		
		Connection con = null;
	    PreparedStatement pStmt = null;
		ResultSet rs = null;  
		
		long res = 0;
	    
	    String query = "INSERT INTO MONITOR (NAME,METRIC_DOMAIN_CONFIG_ID,LOGICAL_ENV_ID,STARTED) VALUES (?,?,?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,monitorConfig.getContext());
	        pStmt.setInt(2,monitorConfig.getMetricDomainConfig().getId());
	        pStmt.setInt(3, monitorConfig.getLogicalEnvId());
	        pStmt.setBoolean(4, monitorConfig.isAutoStart());
	        
            pStmt.executeUpdate();
            
            rs = pStmt.getGeneratedKeys();
          
            if (rs.next()) {
                 res = rs.getLong(1);
            }
            
            monitorConfig.setId(res);
            
            if (monitorConfig.getAttributes() != null && monitorConfig.getAttributes().size()>0) {
            	createMonitorAttributes(res, monitorConfig.getAttributes());
            }
            
            if (monitorConfig.getSchedulerConfig() != null)
            	createMonitorScheduler(res, monitorConfig.getSchedulerConfig());
            
            createMonitorBindings(res, monitorConfig.getConnectorConfigIds());
            
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
            
	    } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        return res;
	}
	
	protected void createMonitorActivities(long monitorId, int metricDomainResourceId, List<Integer> ikrCategoryResourceIdsProvided) throws PersistenceException {	
		Connection con = null;
	    PreparedStatement pStmt = null;			    
	    Collection<IkrCategoryResource> ikrCategoryResources = getIkrCategoryResources(metricDomainResourceId).values();
		for(IkrCategoryResource catResource : ikrCategoryResources) {
			boolean okToSave = true;
			if (ikrCategoryResourceIdsProvided!=null&&!ikrCategoryResourceIdsProvided.contains(catResource.getId())) {
				okToSave = false;
			}
			
			if (okToSave) {
				String query = "INSERT INTO MONITOR_ACTIVITY (MONITOR_ID,IKR_CATEGORY_RESOURCE_ID,ENABLE) VALUES (?,?,?)";	       
			    try {
			    	con = dataSource.getConnection();
			        pStmt = con.prepareStatement(query.toUpperCase());
			        pStmt.setLong(1,monitorId);
			        pStmt.setInt(2,catResource.getId());
			        pStmt.setBoolean(3,true);		        
		            pStmt.executeUpdate();	            
			    }	catch(SQLException e) {
		        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		        } finally {
		            closeStatement(pStmt);
		            closeConnection(con);
		        }
			}
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
            
            ResultSet rs = pStmt.getGeneratedKeys();
            if (rs.next()) {
            	schedId = rs.getInt(1);        
            }
            
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
	
	public void createMonitorAttributes(long monitorId, Map<String, String> attributes)
	throws PersistenceException {	
		Connection con = null;
	    PreparedStatement pStmt = null;		
		for(String name:attributes.keySet()) {
			String query = "INSERT INTO MONITOR_ATTRIBUTE (MONITOR_ID,NAME,ATTR_VALUE) VALUES (?,?,?)";	       
		    try {
		    	con = dataSource.getConnection();
		        pStmt = con.prepareStatement(query.toUpperCase());
		        pStmt.setLong(1,monitorId);
		        pStmt.setString(2,name);
		        pStmt.setString(3,attributes.get(name));		        
	            pStmt.executeUpdate();	            
		    }	catch(SQLException e) {
	        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
	        } finally {
	            closeStatement(pStmt);
	            closeConnection(con);
	        }
		}
	}
	
	public void updateMonitor(MonitorConfig monitorConfig)
	throws PersistenceException {
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE MONITOR SET NAME=?,METRIC_DOMAIN_CONFIG_ID=?," +
	    			   "STARTED=?,LOGICAL_ENV_ID=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,monitorConfig.getContext());
	        pStmt.setInt(2,monitorConfig.getMetricDomainConfig().getId());
	        pStmt.setBoolean(3,monitorConfig.isAutoStart());
	        pStmt.setInt(4, monitorConfig.getLogicalEnvId());
	        pStmt.setLong(5, monitorConfig.getId());	        
            pStmt.executeUpdate();
            
            deleteMonitorAttributes(monitorConfig.getId());            
            if (monitorConfig.getAttributes() != null && monitorConfig.getAttributes().size()>0) {
            	createMonitorAttributes(monitorConfig.getId(), monitorConfig.getAttributes());
            }
            
            if (monitorConfig.getSchedulerConfig() != null)
            	updateMonitorScheduler(monitorConfig.getId(), monitorConfig.getSchedulerConfig());
            
            deleteMonitorBinding(monitorConfig.getId());
            createMonitorBindings(monitorConfig.getId(), monitorConfig.getConnectorConfigIds());
            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
//	public void updateMonitorAttributes(long monitorId, Map<String, String> attributes)
//	throws PersistenceException {		
//		for(String name:attributes.keySet()) {
//			if(!isMonitorAttributeExist(monitorId, name))
//				addMonitorAttribute(monitorId, name, attributes.get(name));
//			else
//				updateMonitorAttribute(monitorId, name, attributes.get(name));
//		}
//	}
	
	public void updateMonitorScheduler(long monitorId,  IkrMonitorSchedulerConfig config)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE MONITOR_SCHEDULER SET CAPTURE_DELAY=?,SCHEDULING_TYPE=?,SCHEDULING_MODE=? WHERE MONITOR_ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,config.getDelay());
	        pStmt.setString(2,config.getType());
	        pStmt.setString(3,config.getMode());
	        pStmt.setLong(4,monitorId);
            pStmt.executeUpdate();            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        deleteMonitorSchedulerTimes(config.getId());
        
        //update start time
        Calendar startCal = config.getStartTime();
        if (startCal != null) {
        	int day = 0;
        	if (IkrMonitorSchedulerConfig.WEEKLY.equals(config.getMode()))
				day = startCal.get(Calendar.DAY_OF_WEEK);
			else if (IkrMonitorSchedulerConfig.MONTHLY.equals(config.getMode()))
				day = startCal.get(Calendar.DAY_OF_MONTH);        	
        	createMonitorSchedulerTime(config.getId(), IkrMonitorSchedulerConfig.START_TIME, day, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE));
        }
        
        //update end time       
        Calendar endCal = config.getEndTime();
        if (endCal != null) {
        	int day = 0;
        	if (IkrMonitorSchedulerConfig.WEEKLY.equals(config.getMode()))
				day = endCal.get(Calendar.DAY_OF_WEEK);
			else if (IkrMonitorSchedulerConfig.MONTHLY.equals(config.getMode()))
				day = endCal.get(Calendar.DAY_OF_MONTH);
        	createMonitorSchedulerTime(config.getId(), IkrMonitorSchedulerConfig.END_TIME, day, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE));
        }
	}
	
	public void addMonitorAttribute(long monitorId, String name, String value)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
		String query = "INSERT INTO MONITOR_ATTRIBUTE (MONITOR_ID,NAME,ATTR_VALUE) VALUES (?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,monitorId);
	        pStmt.setString(2,name);
	        pStmt.setString(3,value);		        
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}	
	
	public void deleteMonitorAttributes(long monitorId)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
		String query = "DELETE FROM MONITOR_ATTRIBUTE WHERE MONITOR_ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,monitorId);    
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updateIkrDefinitionsActivation(Map<Long, Boolean> status)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE IKR_DEFINITION SET ENABLE=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        
	        for(Map.Entry<Long, Boolean> entry : status.entrySet()) {
		        pStmt.setBoolean(1,entry.getValue());
		        pStmt.setLong(2,entry.getKey().longValue());
		        pStmt.executeUpdate();
	        }	        
           
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}

//	public void updateIkrCategoriesActivation(long monitorId, Map<Integer, Boolean> status)
//	throws PersistenceException {
//		Connection con = null;
//	    PreparedStatement pStmt = null;
//	    
//	    String query = "UPDATE ACTIVATED_IKR_CATEGORY SET ENABLE=? WHERE MONITOR_ID=? AND IKR_CATEGORY_ID=?";       
//	       
//	    try {
//	    	con = dataSource.getConnection();
//	        	
//	        pStmt = con.prepareStatement(query);
//	        pStmt.setLong(2, monitorId);
//	        
//	        for(Map.Entry<Integer, Boolean> entry : status.entrySet()) {
//	        	pStmt.setBoolean(1,entry.getValue());
//		        pStmt.setLong(3,entry.getKey().longValue());
//		        pStmt.executeUpdate();
//	        }	        
//           
//	    }	catch(SQLException e) {
//        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//        } finally {
//            closeStatement(pStmt);
//            closeConnection(con);
//        }		
//	}
//	
//	public Map<Integer,Boolean> getIkrCategoriesActivation(long monitorId,String ikrCategoryGroup)
//	throws PersistenceException {
//		Map<Integer,Boolean> res = new HashMap<Integer,Boolean>();
//		
//		String query = "SELECT cat.ID,act.ENABLE FROM ACTIVATED_IKR_CATEGORY act,IKR_CATEGORY cat WHERE cat.ID=act.IKR_CATEGORY_ID"
//					+ " AND act.MONITOR_ID=? AND cat.IKR_CATEGORY_GROUP=?";
//
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query); 
//			pStmt.setLong(1,monitorId);
//			pStmt.setString(2,ikrCategoryGroup);
//			rs = pStmt.executeQuery();
//
//			while (rs.next()) {       	       
//				int ikrCategoryId = rs.getInt("ID");
//				boolean activation = rs.getBoolean("ENABLE");
// 	          	res.put(Integer.valueOf(ikrCategoryId),Boolean.valueOf(activation));
//			}
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}				
//
//		return res;		
//	}
	
	public Map<Integer,List<IkrCategoryResource>> getMonitorActivities(long monitorId)
	throws PersistenceException {
		Map<Integer,List<IkrCategoryResource>> res = new HashMap<Integer,List<IkrCategoryResource>>();
		
		String query = "SELECT res.ID as ID, res.IKR_STATIC_DOMAIN_ID as IKR_STATIC_DOMAIN_ID, res.METRIC_DOMAIN_RESOURCE_ID as METRIC_DOMAIN_RESOURCE_ID, res.NAME as NAME, act.ENABLE as ENABLE"
					+ " FROM IKR_CATEGORY_RESOURCE res, MONITOR_ACTIVITY act WHERE res.ID = act.IKR_CATEGORY_RESOURCE_ID"
					+ " AND act.MONITOR_ID = ?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1,monitorId);
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				int id = rs.getInt("ID");
				int ikrStaticDomainId =  rs.getInt("IKR_STATIC_DOMAIN_ID");
				int metricDomainResourceId = rs.getInt("METRIC_DOMAIN_RESOURCE_ID");
				String name = rs.getString("NAME");
				boolean activated = rs.getBoolean("ENABLE");
				List<IkrCategoryResource> resources = res.get(metricDomainResourceId);
				if (resources == null) {
					resources = new ArrayList<IkrCategoryResource>();
					res.put(metricDomainResourceId, resources);
				}
 	          	resources.add(new IkrCategoryResource(id, ikrStaticDomainId, metricDomainResourceId, name, activated));
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
	
	public void deleteMonitor(long monitorId)
	throws PersistenceException {
		String query = "DELETE mon,con_bind,mon_act,mon_attr,mon_sched,mon_sched_time FROM MONITOR mon" +
					   " LEFT JOIN CONNECTOR_BINDING con_bind on con_bind.MONITOR_ID=mon.id" +
					   " LEFT JOIN MONITOR_ACTIVITY mon_act on mon_act.MONITOR_ID=mon.id" +
					   " LEFT JOIN MONITOR_ATTRIBUTE mon_attr on mon_attr.MONITOR_ID=mon.id" +
					   " LEFT JOIN MONITOR_SCHEDULER mon_sched on mon_sched.MONITOR_ID=mon.id" +
					   " LEFT JOIN MONITOR_SCHEDULER_TIME mon_sched_time on mon_sched_time.MONITOR_SCHEDULER_ID=mon_sched.id" +
					   " WHERE mon.ID=?";
		
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
	 
//	public int deleteAgent(long agentId)
//	throws PersistenceException {
//		int res = 0;
//		
//		String query = "DELETE agt,mon,mon_attr,cat,ikrDef "
//					+ "FROM AGENT agt "
//					+ "LEFT JOIN monitor mon ON agt.ID=mon.AGENT_ID " 
//					+ "LEFT JOIN MONITOR_ATTRIBUTE mon_attr ON mon.ID=mon_attr.MONITOR_ID " 
//					+ "LEFT JOIN ACTIVATED_IKR_CATEGORY cat ON mon.ID=cat.MONITOR_ID "
//					+ "LEFT JOIN IKR_DEFINITION ikrDef ON mon.ID=ikrDef.MONITOR_ID "
//					+ "WHERE agt.ID=?";
//
//		
//		Connection con = null;
//		PreparedStatement pStmt = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query); 
//			pStmt.setLong(1,agentId);
//			res = pStmt.executeUpdate();
//
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeStatement(pStmt);
//			closeConnection(con);
//		}
//		return res;
//	}
	
//	public void resetMonitorEnv(String monitorEnv)throws PersistenceException {
//		String query = "DELETE agt,mon,mon_attr,cat,ikrDef "
//					+ "FROM AGENT agt "
//					+ "LEFT JOIN monitor mon ON agt.ID=mon.AGENT_ID " 
//					+ "LEFT JOIN MONITOR_ATTRIBUTE mon_attr ON mon.ID=mon_attr.MONITOR_ID " 
//					+ "LEFT JOIN ACTIVATED_IKR_CATEGORY cat ON mon.ID=cat.MONITOR_ID "
//					+ "LEFT JOIN IKR_DEFINITION ikrDef ON mon.ID=ikrDef.MONITOR_ID "
//					+ "WHERE mon.ENV=?";			
//		Connection con = null;
//		PreparedStatement pStmt = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query); 
//			pStmt.setString(1,monitorEnv);
//			pStmt.executeUpdate();
//
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeStatement(pStmt);
//			closeConnection(con);
//		}
//	}
//	
//	public void deleteAgentWithoutMonitor(long agentId)
//	throws PersistenceException {
//		String query = "DELETE FROM AGENT WHERE ID=?";
//		
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		
//		try {
//			con = dataSource.getConnection();
//		
//			pStmt = con.prepareStatement(query); 
//			pStmt.setLong(1,agentId);
//			pStmt.executeUpdate();
//		
//		} catch(SQLException e) {
//			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeStatement(pStmt);
//			closeConnection(con);
//		}		
//	}
//


//	public List<IkrDefinition> getAllIkrDefinitions() throws PersistenceException {
//		List<IkrDefinition> res = new ArrayList<IkrDefinition>();
//
//		String query = "SELECT DISTINCT def.ID,cat.ID,def.MONITOR_ID,def.IKR_INSTANCE,def.IKR_ENV,def.IKR_COMPUTE,def.ENABLE FROM IKR_DEFINITION def, IKR_CATEGORY cat "
//			+ "WHERE cat.ID=def.IKR_CATEGORY_ID";
//
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query); 
//			rs = pStmt.executeQuery();
//
//			while (rs.next()) {       	
//				long id = rs.getLong("def.ID");
//				int ikrCategoryId = rs.getInt("cat.ID");
//				int monitorId = rs.getInt("def.MONITOR_ID");	
//				String ikrInstance = rs.getString("def.IKR_INSTANCE");
//				String ikrEnv = rs.getString("def.IKR_ENV");
//				boolean enable = rs.getBoolean("def.ENABLE");
//
//				String metricComputeName  = rs.getString("def.IKR_COMPUTE");
//				MetricCompute metricCompute = null;
//				try {
//					metricCompute = MetricCompute.valueOf(metricComputeName);
//				} catch(Exception exc) {
//					LOG.error("MetricCompute unknown : " + metricComputeName);
//				}
//				
//				IkrDefinition ikrDefinition = new IkrDefinition(id,
//								    							ikrCategoryId,
//								    							ikrInstance,
//								    							ikrEnv,
//								    							metricCompute,
//								    							monitorId,
//								    							enable);
//			
//				res.add(ikrDefinition);
//			}
//		} catch(SQLException e) {
//				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}				
//			
//		return res;	
//	}
	
//	public Collection<IkrDefinition> getIkrDefinitionsByCatGroupName(String name)
//	throws PersistenceException{
//		
//		
//		List<IkrDefinition> res = new ArrayList<IkrDefinition>();
//
//		String query = "SELECT DISTINCT def.ID,cat.ID,def.MONITOR_ID,def.IKR_INSTANCE,def.IKR_ENV,def.IKR_COMPUTE,def.ENABLE FROM IKR_DEFINITION def, IKR_CATEGORY cat "
//			+ "WHERE cat.ID=def.IKR_CATEGORY_ID AND cat.NAME=?";
//
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = dataSource.getConnection();
//
//			pStmt = con.prepareStatement(query); 
//			pStmt.setString(1,name);
//			rs = pStmt.executeQuery();
//
//			while (rs.next()) {       	
//				long id = rs.getLong("def.ID");
//				int ikrCategoryId = rs.getInt("cat.ID");
//				int monitorId = rs.getInt("def.MONITOR_ID");	
//				String ikrInstance = rs.getString("def.IKR_INSTANCE");
//				String ikrEnv = rs.getString("def.IKR_ENV");
//				boolean enable = rs.getBoolean("def.ENABLE");
//				
//				String metricComputeName  = rs.getString("def.IKR_COMPUTE");
//				MetricCompute metricCompute = null;
//				try {
//					metricCompute = MetricCompute.valueOf(metricComputeName);
//				} catch(Exception exc) {
//					LOG.error("MetricCompute unknown : " + metricComputeName);
//				}				
//
//				IkrDefinition ikrDefinition = new IkrDefinition(id,
//								    							ikrCategoryId,
//								    							ikrInstance,
//								    							ikrEnv,
//								    							metricCompute,
//								    							monitorId,
//								    							enable);
//			
//				res.add(ikrDefinition);
//			}
//		} catch(SQLException e) {
//				throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}				
//			
//		return res;		
//	}
	
	public ConnectorConfig getConnectorConfig(int connectorId) throws PersistenceException {
		ConnectorConfig res = null;

		String query = "SELECT * FROM CONNECTOR_CONFIG WHERE ID = ?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1,connectorId);
			rs = pStmt.executeQuery();

			if (rs.next()) {       	
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String type = rs.getString("CONNECTOR_TYPE");
				String description = rs.getString("DESCRIPTION");
				int maxAttempt = rs.getInt("MAX_ATTEMPT");
				int attemptDelay = rs.getInt("ATTEMPT_DELAY");

				AbstractConnectorConfig connectorConfig = null;
				
				if (type.equals(SysloadConnectorConfig.TYPE)) {
					connectorConfig = new SysloadConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSysloadConnectorConfigAttributes((SysloadConnectorConfig)connectorConfig);
				} else if (type.equals(HttpConnectorConfig.TYPE)) {
					connectorConfig = new HttpConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadHttpConnectorConfigAttributes((HttpConnectorConfig)connectorConfig);
				} else if (type.equals(CalypsoConnectorConfig.TYPE)) {
					connectorConfig = new CalypsoConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadCalypsoConnectorConfigAttributes((CalypsoConnectorConfig)connectorConfig);
				} else if (type.equals(RdbmsConnectorConfig.TYPE)) {
					connectorConfig = new RdbmsConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadRdbmsConnectorConfigAttributes((RdbmsConnectorConfig)connectorConfig);
				} else if (type.equals(JmxConnectorConfig.TYPE)) {
					connectorConfig = new JmxConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadJmxConnectorConfigAttributes((JmxConnectorConfig)connectorConfig);
				} else if (type.equals(SystemAgentConnectorConfig.TYPE)) {
					connectorConfig = new SystemAgentConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSystemAgentConnectorConfigAttributes((SystemAgentConnectorConfig)connectorConfig);
				}
				
				res = connectorConfig;
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
	
	public List<Integer> getConnectorConfigIds() throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();

		String query = "SELECT ID FROM CONNECTOR_CONFIG";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				int id = rs.getInt("ID");
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
	
	public Map<Integer, ConnectorConfig> getConnectorConfigs(Collection<Integer> connectorConfigIds) throws PersistenceException {
		Map<Integer, ConnectorConfig> res = new HashMap<Integer, ConnectorConfig>();

		String query = "SELECT * FROM CONNECTOR_CONFIG "
					   + getWhereClause("ID", connectorConfigIds);

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String type = rs.getString("CONNECTOR_TYPE");
				String description = rs.getString("DESCRIPTION");
				int maxAttempt = rs.getInt("MAX_ATTEMPT");
				int attemptDelay = rs.getInt("ATTEMPT_DELAY");

				AbstractConnectorConfig connectorConfig = null;
				
				if (type.equals(SysloadConnectorConfig.TYPE)) {
					connectorConfig = new SysloadConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSysloadConnectorConfigAttributes((SysloadConnectorConfig)connectorConfig);
				} else if (type.equals(HttpConnectorConfig.TYPE)) {
					connectorConfig = new HttpConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadHttpConnectorConfigAttributes((HttpConnectorConfig)connectorConfig);
				} else if (type.equals(CalypsoConnectorConfig.TYPE)) {
					connectorConfig = new CalypsoConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadCalypsoConnectorConfigAttributes((CalypsoConnectorConfig)connectorConfig);
				} else if (type.equals(RdbmsConnectorConfig.TYPE)) {
					connectorConfig = new RdbmsConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadRdbmsConnectorConfigAttributes((RdbmsConnectorConfig)connectorConfig);
				} else if (type.equals(JmxConnectorConfig.TYPE)) {
					connectorConfig = new JmxConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadJmxConnectorConfigAttributes((JmxConnectorConfig)connectorConfig);
				} else if (type.equals(SystemAgentConnectorConfig.TYPE)) {
					connectorConfig = new SystemAgentConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSystemAgentConnectorConfigAttributes((SystemAgentConnectorConfig)connectorConfig);
				}
				
				res.put(id, connectorConfig);
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
	
	public Map<Integer, ConnectorConfig> getConnectorConfigs() throws PersistenceException {
		Map<Integer, ConnectorConfig> res = new HashMap<Integer, ConnectorConfig>();

		String query = "SELECT * FROM CONNECTOR_CONFIG";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String type = rs.getString("CONNECTOR_TYPE");
				String description = rs.getString("DESCRIPTION");
				int maxAttempt = rs.getInt("MAX_ATTEMPT");
				int attemptDelay = rs.getInt("ATTEMPT_DELAY");

				AbstractConnectorConfig connectorConfig = null;
				
				if (type.equals(SysloadConnectorConfig.TYPE)) {
					connectorConfig = new SysloadConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSysloadConnectorConfigAttributes((SysloadConnectorConfig)connectorConfig);
				} else if (type.equals(HttpConnectorConfig.TYPE)) {
					connectorConfig = new HttpConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadHttpConnectorConfigAttributes((HttpConnectorConfig)connectorConfig);
				} else if (type.equals(CalypsoConnectorConfig.TYPE)) {
					connectorConfig = new CalypsoConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadCalypsoConnectorConfigAttributes((CalypsoConnectorConfig)connectorConfig);
				} else if (type.equals(RdbmsConnectorConfig.TYPE)) {
					connectorConfig = new RdbmsConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadRdbmsConnectorConfigAttributes((RdbmsConnectorConfig)connectorConfig);
				} else if (type.equals(JmxConnectorConfig.TYPE)) {
					connectorConfig = new JmxConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadJmxConnectorConfigAttributes((JmxConnectorConfig)connectorConfig);
				} else if (type.equals(SystemAgentConnectorConfig.TYPE)) {
					connectorConfig = new SystemAgentConnectorConfig(id,name,description,maxAttempt,attemptDelay);
					loadSystemAgentConnectorConfigAttributes((SystemAgentConnectorConfig)connectorConfig);
				}
				
				res.put(id, connectorConfig);
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
	
	private void loadSysloadConnectorConfigAttributes(SysloadConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("hostname")) {
					connector.setConnectorContext(value);
				} else if (label.equals("port")) {
					connector.setPort(Integer.parseInt(value));
				} else if (label.equals("agent")) {
					connector.setAgent(value);
				} else if (label.equals("userName")) {
					connector.setUserName(value);
				} else if (label.equals("password")) {
					connector.setPassword(connectorPwdManager.decrypt(value));
				}
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}
	
	private void loadJmxConnectorConfigAttributes(JmxConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("hostname")) {
					connector.setConnectorContext(value);
				} else if (label.equals("port")) {
					connector.setPort(Integer.parseInt(value));
				} else if (label.equals("username")) {
					connector.setUserName(value);
				} else if (label.equals("password")) {
					connector.setPassword(connectorPwdManager.decrypt(value));
				} else if (label.equals("processName")) {
					connector.setProcessName(value);
				} 
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}	
	
	private void loadRdbmsConnectorConfigAttributes(RdbmsConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("driver")) {
					connector.setDriver(value);
				} else if (label.equals("uri")) {
					connector.setUri(value);
				} else if (label.equals("userName")) {
					connector.setUserName(value);
				} else if (label.equals("password")) {
					connector.setPassword(connectorPwdManager.decrypt(value));
				}
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}	
	
	private void loadCalypsoConnectorConfigAttributes(CalypsoConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("userName")) {
					connector.setUserName(value);
				} else if (label.equals("calypsoEnv")) {
					connector.setConnectorContext(value);
				} else if (label.equals("applicationName")) {
					connector.setApplicationName(value);
				} else if (label.equals("password")) {
					connector.setPassword(connectorPwdManager.decrypt(value));
				} else if (label.equals("asofdateEnable")) {
					connector.setAsofdateActive(Boolean.valueOf(value));
				} else if (label.equals("asofdate")) {
					connector.setAsofdate(value);
				} else if (label.equals("dbUserName")) {
					connector.setDbUserName(value);
				}  else if (label.equals("dbPassword")) {
					connector.setDbPassword(connectorPwdManager.decrypt(value));
				} 
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}	
	
	private void loadHttpConnectorConfigAttributes(HttpConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("hostname")) {
					connector.setConnectorContext(value);
				} else if (label.equals("port")) {
					connector.setPort(Integer.parseInt(value));
				}
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}	
	
	private void loadSystemAgentConnectorConfigAttributes(SystemAgentConnectorConfig connector)
	throws PersistenceException {
		String query = "SELECT * FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, connector.getId());
			rs = pStmt.executeQuery();

			while (rs.next()) {
				String label = rs.getString("ATTR_LABEL");
				String value = rs.getString("ATTR_VALUE");
				
				if (label.equals("hostname")) {
					connector.setConnectorContext(value);
				} else if (label.equals("port")) {
					connector.setPort(Integer.parseInt(value));
				}
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}	
	
	private Collection<Integer> getMonitorConnectorIds(long monitorId) 
	throws PersistenceException {
		Collection<Integer> res = new ArrayList<Integer>();

		String query = "SELECT CONNECTOR_CONFIG_ID FROM CONNECTOR_BINDING WHERE MONITOR_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setLong(1, monitorId);
			rs = pStmt.executeQuery();

			while (rs.next()) {
				int connectorId = rs.getInt("CONNECTOR_CONFIG_ID");

				res.add(connectorId);
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public void createConnector(ConnectorConfig connector) 
	throws PersistenceException {

		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO CONNECTOR_CONFIG (ID,NAME,CONNECTOR_TYPE,DESCRIPTION,MAX_ATTEMPT,ATTEMPT_DELAY) VALUES (?,?,?,?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,connector.getId());
	        pStmt.setString(2,connector.getName());
	        pStmt.setString(3,connector.getType());
	        pStmt.setString(4,connector.getDescription());
	        pStmt.setInt(5,connector.getMaxAttempt());
	        pStmt.setInt(6,connector.getAttemptDelay());
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        try {
        	createConnectorAttributes(connector);
		} catch (DESException desException) {
			LOG.error("Error while encrypting passwords", desException);
		}
	}
	
	public void updateConnector(ConnectorConfig connector) 
	throws PersistenceException {

		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "UPDATE CONNECTOR_CONFIG SET NAME=?,CONNECTOR_TYPE=?,DESCRIPTION=?,MAX_ATTEMPT=?,ATTEMPT_DELAY=? WHERE ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,connector.getName());
	        pStmt.setString(2,connector.getType());
	        pStmt.setString(3,connector.getDescription());
	        pStmt.setInt(4,connector.getMaxAttempt());
	        pStmt.setInt(5,connector.getAttemptDelay());
	        pStmt.setLong(6,connector.getId());
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
		deleteConnectorAttributes(connector.getId());
		try {
			createConnectorAttributes(connector);
		} catch (DESException desException) {
			LOG.error("Error while encrypting passwords", desException);
		}
	}	
	
	private void createConnectorAttributes(ConnectorConfig connector) 
	throws PersistenceException, DESException {	

		Map<String,String> attributes = new HashMap<String,String>();

		if (connector.getType().equals(CalypsoConnectorConfig.TYPE)) {
			CalypsoConnectorConfig calypsoConnector = (CalypsoConnectorConfig)connector;
			attributes.put("userName", calypsoConnector.getUserName());
			attributes.put("password", connectorPwdManager.encrypt(calypsoConnector.getPassword()));
			attributes.put("dbUserName", calypsoConnector.getDbUserName());
			attributes.put("dbPassword", connectorPwdManager.encrypt(calypsoConnector.getDbPassword()));
			attributes.put("calypsoEnv", calypsoConnector.getConnectorContext());
			attributes.put("asofdateEnable", String.valueOf(calypsoConnector.isAsofdateActive()));
			attributes.put("asofdate", String.valueOf(calypsoConnector.getAsofdate()));
			attributes.put("applicationName", calypsoConnector.getApplicationName());
		} else if (connector.getType().equals(HttpConnectorConfig.TYPE)) {
			HttpConnectorConfig httpConnectorConfig = (HttpConnectorConfig)connector;
			attributes.put("hostname", httpConnectorConfig.getConnectorContext());
			attributes.put("port", String.valueOf(httpConnectorConfig.getPort()));
		} else if (connector.getType().equals(SysloadConnectorConfig.TYPE)) {
			SysloadConnectorConfig sysloadConnectorConfig = (SysloadConnectorConfig)connector;
			attributes.put("hostname", sysloadConnectorConfig.getConnectorContext());
			attributes.put("port", String.valueOf(sysloadConnectorConfig.getPort()));
			attributes.put("userName", sysloadConnectorConfig.getUserName());
			attributes.put("password", connectorPwdManager.encrypt(sysloadConnectorConfig.getPassword()));
			attributes.put("agent", sysloadConnectorConfig.getAgent());
		} else if (connector.getType().equals(RdbmsConnectorConfig.TYPE)) {
			RdbmsConnectorConfig rdbmsConnectorConfig = (RdbmsConnectorConfig)connector;
			attributes.put("userName", rdbmsConnectorConfig.getUserName());
			attributes.put("password", connectorPwdManager.encrypt(rdbmsConnectorConfig.getPassword()));
			attributes.put("driver", rdbmsConnectorConfig.getDriver());
			attributes.put("uri", rdbmsConnectorConfig.getUri());
		} else if (connector.getType().equals(JmxConnectorConfig.TYPE)) {
			JmxConnectorConfig jmxConnectorConfig = (JmxConnectorConfig)connector;
			attributes.put("hostname", jmxConnectorConfig.getConnectorContext());
			attributes.put("port", String.valueOf(jmxConnectorConfig.getPort()));
			attributes.put("userName", jmxConnectorConfig.getUserName());
			attributes.put("password", connectorPwdManager.encrypt(jmxConnectorConfig.getPassword()));	
			attributes.put("processName", jmxConnectorConfig.getProcessName());	
		} else if (connector.getType().equals(SystemAgentConnectorConfig.TYPE)) {
			SystemAgentConnectorConfig saConnectorConfig = (SystemAgentConnectorConfig)connector;
			attributes.put("hostname", saConnectorConfig.getConnectorContext());
			attributes.put("port", String.valueOf(saConnectorConfig.getPort())); 
		}
		
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO CONNECTOR_CONFIG_ATTRIBUTE (CONNECTOR_CONFIG_ID,ATTR_LABEL,ATTR_VALUE) VALUES (?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,connector.getId());
	        
	        for(Map.Entry<String, String> attr : attributes.entrySet()) {	
	        	pStmt.setString(2,attr.getKey());
	        	pStmt.setString(3,attr.getValue());
	        	pStmt.executeUpdate();
	        }           
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void deleteConnector(int connectorId) 
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "DELETE FROM CONNECTOR_CONFIG WHERE ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,connectorId);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        deleteConnectorAttributes(connectorId);
        deleteConnectorBinding(connectorId);
	}	
	
	protected void createMonitorBindings(long monitorId, Collection<Integer> connectorIds) 
	throws PersistenceException {			
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO CONNECTOR_BINDING (MONITOR_ID,CONNECTOR_CONFIG_ID) VALUES (?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,monitorId);
	        
	        for(int connectorId: connectorIds) {	
	        	pStmt.setInt(2,connectorId);
	        	pStmt.executeUpdate();
	        }  
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}	
	
	private void deleteConnectorBinding(int connectorId) 
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "DELETE FROM CONNECTOR_BINDING WHERE CONNECTOR_CONFIG_ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,connectorId);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	protected void deleteMonitorBinding(long monitorId) 
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "DELETE FROM CONNECTOR_BINDING WHERE MONITOR_ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,monitorId);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	private void deleteConnectorAttributes(int connectorId) 
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "DELETE FROM CONNECTOR_CONFIG_ATTRIBUTE WHERE CONNECTOR_CONFIG_ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,connectorId);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}	
	
	public List<MetricDomainResource> getMetricDomainResources() throws PersistenceException {
		
		List<MetricDomainResource> res = new ArrayList<MetricDomainResource>();

		String query = "SELECT * FROM METRIC_DOMAIN_RESOURCE";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("RESOURCE_NAME");
				res.add(new MetricDomainResource(id, ikrStaticDomainId, name));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public List<MetricDomainResource> getMetricDomainResources(
			int ikrStaticDomainId) throws PersistenceException {
		
		List<MetricDomainResource> res = new ArrayList<MetricDomainResource>();

		String query = "SELECT * FROM METRIC_DOMAIN_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, ikrStaticDomainId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("RESOURCE_NAME");
				res.add(new MetricDomainResource(id, ikrStaticDomainId, name));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}	

	public MetricDomainResource getMetricDomainResource(int id)
			throws PersistenceException {
		
		MetricDomainResource res = null;

		String query = "SELECT * FROM METRIC_DOMAIN_RESOURCE WHERE ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, id);
			rs = pStmt.executeQuery();
			if (rs.next()) {
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("RESOURCE_NAME");
				res = new MetricDomainResource(id, ikrStaticDomainId, name);
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public MetricDomainResource getMetricDomainResource(int metricDomainId, String resourceName) throws PersistenceException {
		MetricDomainResource res = null;

		String query = "SELECT * FROM METRIC_DOMAIN_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=? AND RESOURCE_NAME=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, metricDomainId);
			pStmt.setString(2, resourceName);
			rs = pStmt.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("ID");
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("RESOURCE_NAME");
				res = new MetricDomainResource(id, ikrStaticDomainId, name);
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}

	public void addMetricDomainResource(MetricDomainResource resource, int nextId)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO METRIC_DOMAIN_RESOURCE (ID,IKR_STATIC_DOMAIN_ID,RESOURCE_NAME) VALUES (?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,nextId);
	        pStmt.setInt(2,resource.getIkrStaticDomainId());
	        pStmt.setString(3,resource.getResourceName());
	        pStmt.executeUpdate();	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updateMetricDomainResource(MetricDomainResource resource)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE METRIC_DOMAIN_RESOURCE SET RESOURCE_NAME=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,resource.getResourceName());
	        pStmt.setInt(2, resource.getId());
	        pStmt.executeUpdate();            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updateIkrCategoryResource(IkrCategoryResource resource)
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "UPDATE IKR_CATEGORY_RESOURCE SET IKR_STATIC_DOMAIN_ID=?,METRIC_DOMAIN_RESOURCE_ID=?,NAME=? WHERE ID=?";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setInt(1,resource.getIkrStaticDomainId());
		    pStmt.setInt(2,resource.getMetricDomainResourceId());
		    pStmt.setString(3,resource.getName());
		    pStmt.setInt(4, resource.getId());
		    pStmt.executeUpdate();	
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}		
	}
	
	public void removeMetricDomainResource(int resourceId)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "DELETE FROM METRIC_DOMAIN_RESOURCE WHERE ID=?";
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,resourceId);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void cleanMetricDomainResource() throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "DELETE FROM METRIC_DOMAIN_RESOURCE";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.executeUpdate();	            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}
	
	public void deleteIkrCategoryResourceById(int resourceId)
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "DELETE FROM IKR_CATEGORY_RESOURCE WHERE ID=?";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setInt(1,resourceId);
		    pStmt.executeUpdate();	            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}
	
	public void deleteIkrCategoryResourceByStaticDomainId(int ikrStaticDomainId) throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "DELETE FROM IKR_CATEGORY_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=?";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setInt(1,ikrStaticDomainId);
		    pStmt.executeUpdate();	            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}
	
	public void cleanIkrCategoryResource()
	throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "DELETE FROM IKR_CATEGORY_RESOURCE";
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		   pStmt.executeUpdate();	            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}
	}
	
	public Map<Integer, IkrCategoryResource> loadIkrCategoryResources() throws PersistenceException {
		Map<Integer, IkrCategoryResource> res = new HashMap<Integer, IkrCategoryResource>();

		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				int metricDomainResourceId = rs.getInt("METRIC_DOMAIN_RESOURCE_ID");
				String name = rs.getString("NAME");
				res.put(ikrStaticDomainId, new IkrCategoryResource(id, ikrStaticDomainId, metricDomainResourceId, name, true));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public Map<Integer, IkrCategoryResource> getIkrCategoryResourcesById(int metricDomainResourceId) throws PersistenceException {
		Map<Integer, IkrCategoryResource> res = new HashMap<Integer, IkrCategoryResource>();

		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE WHERE METRIC_DOMAIN_RESOURCE_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, metricDomainResourceId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("NAME");
				res.put(ikrStaticDomainId, new IkrCategoryResource(id, ikrStaticDomainId, metricDomainResourceId, name, true));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public List<IkrCategoryResource> getIkrCategoryResources(List<Integer> categoryResourceIds) throws PersistenceException {
		List<IkrCategoryResource> res = new ArrayList<IkrCategoryResource>();
		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE" + getWhereClause("ID", categoryResourceIds);

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				int metricDomainResourceId = rs.getInt("METRIC_DOMAIN_RESOURCE_ID");
				int ikrCategoryId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("NAME");				
				res.add(new IkrCategoryResource(id, ikrCategoryId, metricDomainResourceId, name, true));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public IkrCategoryResource getIkrCategoryResource(int ikrCategoryId) throws PersistenceException {
		IkrCategoryResource res = null;
		
		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, ikrCategoryId);
			rs = pStmt.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("ID");
				int metricDomainResourceId = rs.getInt("METRIC_DOMAIN_RESOURCE_ID");
				String name = rs.getString("NAME");
				res = new IkrCategoryResource(id, ikrCategoryId, metricDomainResourceId, name, true);
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}
	
	public int getIkrCategoryResourceId(int ikrCategoryId, int metricDomainResourceId, String name) throws PersistenceException {
		int id = 0;
		
		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE WHERE IKR_STATIC_DOMAIN_ID=? AND METRIC_DOMAIN_RESOURCE_ID=? AND NAME=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, ikrCategoryId);
			pStmt.setInt(2, metricDomainResourceId);
			pStmt.setString(3, name);
			rs = pStmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("ID");
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return id;
	}

	public Map<String, IkrCategoryResource> getIkrCategoryResources(int metricDomainResourceId) throws PersistenceException {
		Map<String, IkrCategoryResource> res = new HashMap<String, IkrCategoryResource>();

		String query = "SELECT * FROM IKR_CATEGORY_RESOURCE WHERE METRIC_DOMAIN_RESOURCE_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1, metricDomainResourceId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ID");
				int ikrStaticDomainId = rs.getInt("IKR_STATIC_DOMAIN_ID");
				String name = rs.getString("NAME");
				res.put(name, new IkrCategoryResource(id, ikrStaticDomainId, metricDomainResourceId, name, true));
			}
		} catch(Exception e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}			
		
		return res;
	}

	public void saveIkrCategoryResource(IkrCategoryResource resource, int nextId)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		String query = "INSERT INTO IKR_CATEGORY_RESOURCE (ID,IKR_STATIC_DOMAIN_ID,METRIC_DOMAIN_RESOURCE_ID,NAME) VALUES (?,?,?,?)";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,nextId);
	        pStmt.setInt(2,resource.getIkrStaticDomainId());
	        pStmt.setInt(3,resource.getMetricDomainResourceId());
	        pStmt.setString(4,resource.getName());
	        pStmt.executeUpdate();	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}

	public IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(int jobSchedulerStaticDomainId)
			throws PersistenceException {
		IkrJobSchedulerStaticDomain taskStaticDomain = null;		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM JOB_SCHEDULER_STATIC_DOMAIN WHERE ID=?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setInt(1, jobSchedulerStaticDomainId);
	        rs = pStmt.executeQuery();	        
	        if(rs.next()) { 	  
	        	String jobSchedulerType = rs.getString("JOB_SCHEDULER_TYPE");
	          	String name = rs.getString("NAME");
	          	String desc = rs.getString("DESCRIPTION");	            
	          	taskStaticDomain = new IkrJobSchedulerStaticDomain(jobSchedulerStaticDomainId, jobSchedulerType, name, desc);
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return taskStaticDomain;	
	}

	public List<IkrJobSchedulerStaticDomain> getJobSchedulerStaticDomains()
			throws PersistenceException {
		List<IkrJobSchedulerStaticDomain> taskDomains = new ArrayList<IkrJobSchedulerStaticDomain>();		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM JOB_SCHEDULER_STATIC_DOMAIN";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase()); 
	        rs = pStmt.executeQuery();	        
	        while(rs.next()) { 	  
	        	int id = rs.getInt("ID");
	        	String jobSchedulerType = rs.getString("JOB_SCHEDULER_TYPE");
	          	String name = rs.getString("NAME");
	          	String desc = rs.getString("DESCRIPTION");
	          	taskDomains.add(new IkrJobSchedulerStaticDomain(id, jobSchedulerType, name, desc));
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return taskDomains;	
	}

	public IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(String jobSchedulerStaticDomainType)
			throws PersistenceException {
		IkrJobSchedulerStaticDomain taskStaticDomain = null;		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM JOB_SCHEDULER_STATIC_DOMAIN WHERE JOB_SCHEDULER_TYPE=?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setString(1, jobSchedulerStaticDomainType);
	        rs = pStmt.executeQuery();	        
	        if(rs.next()) { 	  
	        	int id = rs.getInt("ID");
	          	String name = rs.getString("NAME");
	          	String desc = rs.getString("DESCRIPTION");	            
	          	taskStaticDomain = new IkrJobSchedulerStaticDomain(id, jobSchedulerStaticDomainType, name, desc);
	        }
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		return taskStaticDomain;	
	}

	public void createJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain)
			throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		String query = "INSERT INTO JOB_SCHEDULER_STATIC_DOMAIN (JOB_SCHEDULER_TYPE, NAME, DESCRIPTION) VALUES (?,?,?)";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setString(1,jobSchedulerStaticDomain.getJobSchedulerType());
		    pStmt.setString(2,(jobSchedulerStaticDomain.getName()!=null)?jobSchedulerStaticDomain.getName():jobSchedulerStaticDomain.getJobSchedulerType().toLowerCase());	
		    pStmt.setString(3,(jobSchedulerStaticDomain.getDescription()!=null)?jobSchedulerStaticDomain.getDescription():"");
		    pStmt.executeUpdate();            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
		    closeStatement(pStmt);
		    closeConnection(con);
		}
		
	}

	public void updateJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE JOB_SCHEDULER_STATIC_DOMAIN SET JOB_SCHEDULER_TYPE=?,NAME=?,DESCRIPTION=? " +
	    				"WHERE ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,jobSchedulerStaticDomain.getJobSchedulerType());
		    pStmt.setString(2,jobSchedulerStaticDomain.getName());	
		    pStmt.setString(3,jobSchedulerStaticDomain.getDescription());
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}

	public void deleteJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain)
			throws PersistenceException {
		String query = "DELETE FROM JOB_SCHEDULER_STATIC_DOMAIN WHERE ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1,jobSchedulerStaticDomain.getId());
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}		
	}

	public void deleteJobSchedulerStaticDomains() throws PersistenceException {
		String query = "DELETE FROM JOB_SCHEDULER_STATIC_DOMAIN";		
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

	public Map<Integer, IkrJobSchedulerConfig> loadJobSchedulerConfigs(int logicalEnvId) throws PersistenceException {
		Map<Integer,IkrJobSchedulerConfig> res = new HashMap<Integer,IkrJobSchedulerConfig>();	
		
		String query = "SELECT * FROM JOB_SCHEDULER";
		if (logicalEnvId > 0)
			query = query + " WHERE LOGICAL_ENV_ID = ?";
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase());
          if (logicalEnvId > 0)
        	  pStmt.setInt(1, logicalEnvId);
          rs = pStmt.executeQuery();
          while (rs.next()) {       	          	
        	  IkrJobSchedulerConfig config = getJobSchedulerConfig(rs); 
	          if (config!= null)
	        	  res.put(config.getId(),config);
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
	
	private IkrJobSchedulerConfig getJobSchedulerConfig(ResultSet rs)
	throws SQLException, PersistenceException {
      	int id = rs.getInt("ID");
      	String name = rs.getString("NAME");
      	String mode = rs.getString("SCHEDULING_MODE");
      	int logicalEnvId = rs.getInt("LOGICAL_ENV_ID");
      	int jobSchedulerStaticDomainId = rs.getInt("JOB_SCHEDULER_STATIC_DOMAIN_ID");
      	String description = rs.getString("DESCRIPTION");
      	boolean active = rs.getBoolean("ACTIVE");         	
      	
      	IkrJobSchedulerConfig config = null;
		try {
			//get Scheduler Times
			Map<String, Object> times = getJobSchedulerTime(id, mode);				
			config = new IkrJobSchedulerConfig(id, 
											name,
											logicalEnvId,
											jobSchedulerStaticDomainId, 
											mode, 
											(Calendar)times.get(IkrJobSchedulerConfig.START_TIME), 
											(Calendar)times.get(IkrJobSchedulerConfig.END_TIME),
											description,
											active);
			if (IkrJobSchedulerConfig.NONE.equals(mode)) {
				for(String key : times.keySet()) {
					config.setModeType(key);
					config.setModeValue((Integer)times.get(key));
				}				
			}
			try {				
				config.setAttributes(loadJobSchedulerAttributes(id));
			} catch (PersistenceException e) {
				LOG.error(e);
			}      	
		} catch (PersistenceException e1) {
			throw new PersistenceException(e1.getMessage(), e1, BaseException.EXCEPTION);
		}       	
      	return config;
	}
	
	private Map<String, Object> getJobSchedulerTime(int schedulerId, String mode) throws PersistenceException {
		Map<String, Object> schedTimes = new HashMap<String, Object>();
		String query = "SELECT * FROM JOB_SCHEDULER_TIME WHERE JOB_ID=?";
		Connection con = null;		
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();			
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, schedulerId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				String type = rs.getString("SCHED_TIME_TYPE");
				int day = rs.getInt("SCHED_DAY");
				int hour = rs.getInt("SCHED_HOUR");
				int min = rs.getInt("SCHED_MIN");
				if (IkrJobSchedulerConfig.START_TIME.equals(type)) {
					Calendar time = Calendar.getInstance();
					time.setTime(new Date());
					if (IkrJobSchedulerConfig.WEEKLY.equals(mode))
						time.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrJobSchedulerConfig.MONTHLY.equals(mode))
						time.set(Calendar.DAY_OF_MONTH, day);
					time.set(Calendar.HOUR_OF_DAY, hour);
					time.set(Calendar.MINUTE, min);
					schedTimes.put(type, time);
				}
				else if (IkrJobSchedulerConfig.END_TIME.equals(type)) {
					Calendar time = Calendar.getInstance();
					time.setTime(new Date());
					if (IkrJobSchedulerConfig.WEEKLY.equals(mode))
						time.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrJobSchedulerConfig.MONTHLY.equals(mode))
						time.set(Calendar.DAY_OF_MONTH, day);
					time.set(Calendar.HOUR_OF_DAY, hour);
					time.set(Calendar.MINUTE, min);
					schedTimes.put(type, time);
				}
				else if (IkrJobSchedulerConfig.DAY_CT.equals(type)) {
					schedTimes.put(type, day);
				}
				else if (IkrJobSchedulerConfig.HOUR_CT.equals(type)) {
					schedTimes.put(type, hour);
				}
				else if (IkrJobSchedulerConfig.MIN_CT.equals(type)) {
					schedTimes.put(type, min);
				}
			}			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return schedTimes;
	}
	
	private Map<String, Object> getMonitorSchedulerTime(int schedulerId, String mode) throws PersistenceException {
		Map<String, Object> schedTimes = new HashMap<String, Object>();
		String query = "SELECT * FROM MONITOR_SCHEDULER_TIME WHERE MONITOR_SCHEDULER_ID=?";
		Connection con = null;		
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();			
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, schedulerId);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				String type = rs.getString("SCHED_TIME_TYPE");
				int day = rs.getInt("SCHED_DAY");
				int hour = rs.getInt("SCHED_HOUR");
				int min = rs.getInt("SCHED_MIN");
				if (IkrJobSchedulerConfig.START_TIME.equals(type)) {
					Calendar time = Calendar.getInstance();
					time.setTime(new Date());
					if (IkrJobSchedulerConfig.WEEKLY.equals(mode))
						time.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrJobSchedulerConfig.MONTHLY.equals(mode))
						time.set(Calendar.DAY_OF_MONTH, day);
					time.set(Calendar.HOUR_OF_DAY, hour);
					time.set(Calendar.MINUTE, min);
					schedTimes.put(type, time);
				}
				else if (IkrJobSchedulerConfig.END_TIME.equals(type)) {
					Calendar time = Calendar.getInstance();
					time.setTime(new Date());
					if (IkrJobSchedulerConfig.WEEKLY.equals(mode))
						time.set(Calendar.DAY_OF_WEEK, day);
					else if (IkrJobSchedulerConfig.MONTHLY.equals(mode))
						time.set(Calendar.DAY_OF_MONTH, day);
					time.set(Calendar.HOUR_OF_DAY, hour);
					time.set(Calendar.MINUTE, min);
					schedTimes.put(type, time);
				}
				else if (IkrJobSchedulerConfig.DAY_CT.equals(type)) {
					schedTimes.put(type, day);
				}
				else if (IkrJobSchedulerConfig.HOUR_CT.equals(type)) {
					schedTimes.put(type, hour);
				}
				else if (IkrJobSchedulerConfig.MIN_CT.equals(type)) {
					schedTimes.put(type, min);
				}
			}			
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return schedTimes;
	}
	
	private Map<String, String> loadJobSchedulerAttributes(int taskId) throws PersistenceException {
		Map<String, String> res = new HashMap<String, String>();		
		String query = "SELECT * FROM JOB_SCHEDULER_ATTRIBUTE WHERE JOB_SCHEDULER_ID=?";
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setInt(1, taskId);
			rs = pStmt.executeQuery();
			while (rs.next()) {       	
				res.put(rs.getString("NAME"), rs.getString("ATTR_VALUE"));
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

	public Map<Integer, IkrJobSchedulerConfig> loadJobSchedulerConfigs(Collection<Integer> taskIds) throws PersistenceException {
		Map<Integer,IkrJobSchedulerConfig> res = new HashMap<Integer,IkrJobSchedulerConfig>();		
		String query = "SELECT * FROM JOB_SCHEDULER "	+ getWhereClause("ID", taskIds);
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			rs = pStmt.executeQuery();
			while (rs.next()) {       	          	
				IkrJobSchedulerConfig config = getJobSchedulerConfig(rs);          	
          		if (config!= null)
          			res.put(config.getId(),config);
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

	public IkrJobSchedulerConfig getJobSchedulerConfig(int taskId)
			throws PersistenceException {
		IkrJobSchedulerConfig res = null;		
		String query = "SELECT * FROM JOB_SCHEDULER WHERE ID=?";
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
          con = dataSource.getConnection();
          pStmt = con.prepareStatement(query.toUpperCase());
          pStmt.setInt(1, taskId);
          rs = pStmt.executeQuery();
          while (rs.next()) {       	
        	 res = getJobSchedulerConfig(rs);
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
	        pStmt.setInt(3, config.getJobStaticDomainId());
	        pStmt.setString(4,config.getMode());
	        pStmt.setString(5, config.getDescription());
	        pStmt.setBoolean(6, config.isActive());	        
            pStmt.executeUpdate();    
            
            rs = pStmt.getGeneratedKeys();          
            if (rs.next()) {
                 res = rs.getInt(1);
            } 
            
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
	
	public void createJobSchedulerAttributes(int jobSchedulerId, Map<String, String> attributes)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
		for(String name:attributes.keySet()) {
			String query = "INSERT INTO JOB_SCHEDULER_ATTRIBUTE (JOB_SCHEDULER_ID,NAME,ATTR_VALUE) VALUES (?,?,?)";	       
		    try {
		    	con = dataSource.getConnection();
		        pStmt = con.prepareStatement(query.toUpperCase());
		        pStmt.setInt(1,jobSchedulerId);
		        pStmt.setString(2,name);
		        pStmt.setString(3,attributes.get(name));		        
	            pStmt.executeUpdate();	            
		    }	catch(SQLException e) {
	        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
	        } finally {
	            closeStatement(pStmt);
	            closeConnection(con);
	        }
		}
	}
	
	public void deleteJobSchedulerAttributes(int jobSchedulerId)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;	    
	    String query = "DELETE FROM JOB_SCHEDULER_ATTRIBUTE WHERE JOB_SCHEDULER_ID=?";
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,jobSchedulerId);
            pStmt.executeUpdate();            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void createJobSchedulerTimes(IkrJobSchedulerConfig config)
	throws PersistenceException {		
		if (!IkrJobSchedulerConfig.NONE.equals(config.getMode())) {
            if (config.getStartTime() != null) {
            	Calendar time = config.getStartTime();
            	int day = 0;
            	if (IkrJobSchedulerConfig.WEEKLY.equals(config.getMode()))
    				day = time.get(Calendar.DAY_OF_WEEK);
    			else if (IkrJobSchedulerConfig.MONTHLY.equals(config.getMode()))
    				day = time.get(Calendar.DAY_OF_MONTH);;
            	createJobSchedulerTime(config.getId(), IkrJobSchedulerConfig.START_TIME, day, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
            }
            
            if (config.getEndTime() != null) {
            	Calendar time = config.getEndTime();
            	int day = 0;
            	if (IkrJobSchedulerConfig.WEEKLY.equals(config.getMode()))
    				day = time.get(Calendar.DAY_OF_WEEK);
    			else if (IkrJobSchedulerConfig.MONTHLY.equals(config.getMode()))
    				day = time.get(Calendar.DAY_OF_MONTH);;
    				createJobSchedulerTime(config.getId(), IkrJobSchedulerConfig.END_TIME, day, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
            }
        }
        else {
        	int day = 0;
        	int hour = 0;
        	int min = 0;
        	String type = config.getModeType();
        	if (IkrJobSchedulerConfig.HOUR_CT.equals(type))
        		hour = config.getModeValue();
        	else if (IkrJobSchedulerConfig.MIN_CT.equals(type))
        		min = config.getModeValue();
        	else
        		day = config.getModeValue();
        	
        	createJobSchedulerTime(config.getId(),type, day, hour, min);
        }
	}
	
	public void createMonitorSchedulerTime(int monitorschedulerId,String type, int day, int hour, int min)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
		String query = "INSERT INTO MONITOR_SCHEDULER_TIME (MONITOR_SCHEDULER_ID,SCHED_TIME_TYPE,SCHED_DAY,SCHED_HOUR,SCHED_MIN) VALUES (?,?,?,?,?)";	
		try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,monitorschedulerId);
	        pStmt.setString(2,type);
	        pStmt.setInt(3,day);	
	        pStmt.setInt(4,hour);
	        pStmt.setInt(5,min);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void deleteMonitorSchedulerTimes(int monitorschedulerId)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;	    
	    String query = "DELETE FROM MONITOR_SCHEDULER_TIME WHERE MONITOR_SCHEDULER_ID=?";
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,monitorschedulerId);
            pStmt.executeUpdate();            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void createJobSchedulerTime(int jobId,String type, int day, int hour, int min)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;		
		String query = "INSERT INTO JOB_SCHEDULER_TIME (JOB_ID,SCHED_TIME_TYPE,SCHED_DAY,SCHED_HOUR,SCHED_MIN) VALUES (?,?,?,?,?)";	
		try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,jobId);
	        pStmt.setString(2,type);
	        pStmt.setInt(3,day);	
	        pStmt.setInt(4,hour);
	        pStmt.setInt(5,min);
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void deleteJobSchedulerTimes(int jobId)
	throws PersistenceException {		
		Connection con = null;
	    PreparedStatement pStmt = null;	    
	    String query = "DELETE FROM JOB_SCHEDULER_TIME WHERE JOB_ID=?";
	    try {
	    	con = dataSource.getConnection();	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,jobId);
            pStmt.executeUpdate();            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}

	public void updateJobScheduler(IkrJobSchedulerConfig config)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE JOB_SCHEDULER SET NAME=?,LOGICAL_ENV_ID=?,JOB_SCHEDULER_STATIC_DOMAIN_ID=?," +
	    			   						"SCHEDULING_MODE=?,DESCRIPTION=?,ACTIVE=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,config.getName());
	        pStmt.setInt(2,config.getLogicalEnvId());
//	        pStmt.setString(3,config.getAction());
	        pStmt.setInt(3, config.getJobStaticDomainId());
	        pStmt.setString(4, config.getMode());	
	        pStmt.setString(5,config.getDescription());
	        pStmt.setBoolean(6, config.isActive());
	        pStmt.setInt(7, config.getId());
            pStmt.executeUpdate();
            
            deleteJobSchedulerAttributes(config.getId());      
            if (config.getAttributes() != null) 
            	createJobSchedulerAttributes(config.getId(), config.getAttributes());
            
            deleteJobSchedulerTimes(config.getId());
            createJobSchedulerTimes(config);
            	
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
		
	}

	public void deleteJobScheduler(int taskId) throws PersistenceException {
		String query = "DELETE task,task_attr,task_sched_time" +
				" FROM JOB_SCHEDULER task" +
				" LEFT JOIN JOB_SCHEDULER_ATTRIBUTE task_attr on JOB_SCHEDULER_ID=task.id" +
				" LEFT JOIN JOB_SCHEDULER_TIME task_sched_time on task_sched_time.JOB_ID=task.id" +
				" WHERE task.ID=?";
		
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

	public void addJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig)
			throws PersistenceException {
		Connection con = null;
		PreparedStatement pStmt = null;
		String query = "INSERT INTO JOB_SCHEDULER_ATTRIBUTE_CONFIG (JOB_SCHEDULER_STATIC_DOMAIN_ID,NAME,LABEL,ENABLE,,SELECTION=?,SELECTION_VALUE=? ) VALUES (?,?,?,?,?,?)";	       
		try {
			con = dataSource.getConnection();
		    pStmt = con.prepareStatement(query.toUpperCase());
		    pStmt.setInt(1,attrConfig.getJobSchedulerStaticDomainId());
		    pStmt.setString(2,attrConfig.getName());
		    pStmt.setString(3,attrConfig.getLabel());	
		    pStmt.setBoolean(4,attrConfig.isEnabled());	
		    pStmt.setBoolean(5,attrConfig.isSelection());	
		    String selectionValue = null;
	        List<String> values = attrConfig.getSelectionValues();
	        if (attrConfig.isSelection() && values!=null && values.size()>0) {
	        	int lg = values.size();
	        	selectionValue = "";
	        	for (String val : values) {
	        		if (lg == 1)
	        			selectionValue = selectionValue + val;
	        		else
	        			selectionValue = selectionValue + val + ":";
	        		lg = lg - 1;
	        	}
	        }
	        pStmt.setString(6,selectionValue);
		    pStmt.executeUpdate();	            
		}	catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
		    closeStatement(pStmt);
		    closeConnection(con);
		}			
		
	}

	public void updateJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    String query = "UPDATE JOB_SCHEDULER_ATTRIBUTE_CONFIG SET JOB_SCHEDULER_STATIC_DOMAIN_ID=?,NAME=?,LABEL=?,ENABLE=?,SELECTION=?,SELECTION_VALUE=? " +
	    				"WHERE ID=?";	       
	    try {
	    	con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setInt(1,attrConfig.getJobSchedulerStaticDomainId());
	        pStmt.setString(2,attrConfig.getName());
	        pStmt.setString(3,attrConfig.getLabel());	
	        pStmt.setBoolean(4,attrConfig.isEnabled());
	        pStmt.setBoolean(5,attrConfig.isSelection());
	        String selectionValue = null;
	        List<String> values = attrConfig.getSelectionValues();
	        if (attrConfig.isSelection() && values!=null && values.size()>0) {
	        	int lg = values.size();
	        	selectionValue = "";
	        	for (String val : values) {
	        		if (lg == 1)
	        			selectionValue = selectionValue + val;
	        		else
	        			selectionValue = selectionValue + val + ":";
	        		lg = lg - 1;
	        	}
	        }
	        pStmt.setString(6,selectionValue);
	        pStmt.setInt(7,attrConfig.getId());
            pStmt.executeUpdate();	            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}

	public void deleteJobSchedulerAttributeConfig(int id)
			throws PersistenceException {
		String query = "DELETE FROM JOB_SCHEDULER_ATTRIBUTE_CONFIG WHERE ID = ?";		
		Connection con = null;
		PreparedStatement pStmt = null;		
		try {
			con = dataSource.getConnection();		
			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setInt(1,id);
			pStmt.executeUpdate();
		
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeStatement(pStmt);
			closeConnection(con);
		}	
	}

	public Map<String, IkrJobSchedulerAttributeConfig> getJobSchedulerAttributeConfigs(int taskStaticDomainId) throws PersistenceException {
		Map<String, IkrJobSchedulerAttributeConfig> res = new HashMap<String, IkrJobSchedulerAttributeConfig>();
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM JOB_SCHEDULER_ATTRIBUTE_CONFIG WHERE JOB_SCHEDULER_STATIC_DOMAIN_ID = ?";
		try {
			con = dataSource.getConnection();
	        pStmt = con.prepareStatement(query.toUpperCase());  
	        pStmt.setInt(1, taskStaticDomainId);
	        rs = pStmt.executeQuery();
	        while (rs.next()) {
	        	int id = rs.getInt("ID");
	        	String name = rs.getString("NAME");
	        	String label = rs.getString("LABEL");
	        	boolean enable = rs.getBoolean("ENABLE");
	        	
	        	List<String> selectionValues = null;
	        	boolean selection = rs.getBoolean("SELECTION");
	        	if (selection) {
	        		String selectionValChar = rs.getString("SELECTION_VALUE");
	        		selectionValues = Arrays.asList(selectionValChar.split(":"));
	        	}
	        	res.put(name, new IkrJobSchedulerAttributeConfig(id, taskStaticDomainId, name, label, enable, selection, selectionValues));
	        }
		} catch(SQLException e) {
			LOG.error(e);
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(pStmt);
				closeConnection(con);
			} catch (PersistenceException e) {
				LOG.error(e);
			}
		}
		return res;
	}

	public List<Integer> getLastIkrStaticDomainIds(int maxSize) throws PersistenceException {
		List<Integer> res = new ArrayList<Integer>();
		
		String query = "SELECT ID FROM IKR_STATIC_DOMAIN LIMIT ?";
		
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
		
		String query = "SELECT ID FROM MONITOR LIMIT ?";
		
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
		
		String query = "SELECT ID FROM LOGICAL_ENV LIMIT ?";
		
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
		
		String query = "SELECT ID FROM CONNECTOR_CONFIG LIMIT ?";
		
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