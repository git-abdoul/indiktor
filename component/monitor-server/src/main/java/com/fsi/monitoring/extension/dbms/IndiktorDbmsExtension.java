package com.fsi.monitoring.extension.dbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.dao.PooledDataSourceFactory;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.MonitorExtension;

public class IndiktorDbmsExtension extends MonitorExtension {

	private static final Logger logger = Logger.getLogger(IndiktorDbmsExtension.class);		
	
	//Default maximum number of active connections 
	public static final int DEFAULT_CONNECTION_POOL_SIZE = 30;
	//Default time to wait when pool exhausted
	public static final int DEFAULT_TIME_WAIT_BLOCKED = -1;
	
	private CustoDAO dao;
	
	public void initConnection(MonitorConfig monitorConfig) throws Exception {
		String connectionUri = monitorConfig.getAttributes().get("CONNECTION_URI");
		String driver = monitorConfig.getAttributes().get("DRIVER");

		try {
				Class.forName(driver);
		} catch (ClassNotFoundException exc) {
			logger.fatal("SGBD Driver not found in DbmsConnectorMonitor : " + exc);
		}
		PooledDataSourceFactory dataSourceFactory = new PooledDataSourceFactory();
//		DataSource dsSource = dataSourceFactory.createDataSource(DEFAULT_TIME_WAIT_BLOCKED, 
//													  			 DEFAULT_CONNECTION_POOL_SIZE, 
//													  			 connectionUri, 
//													  			 monitorConfig.getUserName(), 
//													  			 monitorConfig.getPassword());
		DataSource dsSource = null;
		
		if (connectionUri.contains("mysql")) {
			dao = new MySQLIndiktorDAO();
		} else {
			dao = new OracleIndiktorDAO();
		}		
		
		dao.setDataSource(dsSource);
	}

	// ONE fetch needed per categoryGroup name : fetchCategoryGroupName
	public List<IkrInstanceData> fetchINDIKTOR_DBMS_STATUS() {

		List<IkrInstanceData> data = null;
		
		try {
			data = dao.getData();
		} catch(Exception exc) {
			logger.error(exc);
		}

		return data;
	}	
	
	public class CustoIkrInstance extends IkrInstanceData {
		
		private String tableSize;
		private String tableRows;
		
		public CustoIkrInstance(String ikrInstance,
								String tableSize,
								String tableRows,
								Date captureTime) {
			super(ikrInstance, captureTime);
			
			this.tableSize = tableSize;
			this.tableRows = tableRows;
		}
		
		// ONE get need per category name : getCategoryName [String return mandatory]
		
		public String getSize() {
			return tableSize;
		}
		
		public String getRows() {
			return tableRows;
		}
	}	
	
	interface CustoDAO {
		List<IkrInstanceData> getData() throws PersistenceException;
		void setDataSource(DataSource dataSource);
	}	

	private class MySQLIndiktorDAO extends AbstractDataSourceDAO implements CustoDAO {
		
		public List<IkrInstanceData> getData() throws PersistenceException {
			List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
			
			Connection con = null;
		    PreparedStatement pStmt = null;
		    ResultSet rs = null;

		    String query = "SHOW TABLE STATUS";

		    Date now = new Date();
		    
		    try {
		    	con = dataSource.getConnection();

		        pStmt = con.prepareStatement(query);
	            rs = pStmt.executeQuery();
	            while (rs.next()) {  
	 	          	String ikrInstance = rs.getString("Name");
	 	          	Double tableRows = rs.getDouble("Rows");	          	
	 	          	Double tableSize = rs.getDouble("Data_length");
	 	          	
	 	          	CustoIkrInstance ikrInstanceData = new CustoIkrInstance(ikrInstance+"@Indiktor",
	 	          													    	String.valueOf(tableSize),
	 	          													    	String.valueOf(tableRows),
	 	          													    	now);
	 	          	
	 	          	res.add(ikrInstanceData);	
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
	}
	
	private class OracleIndiktorDAO extends AbstractDataSourceDAO implements CustoDAO {
		
		public List<IkrInstanceData> getData() throws PersistenceException {
			List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
			
			Connection con = null;
		    PreparedStatement pStmt = null;
		    ResultSet rs = null;

		    String query = "select table_name,num_rows,avg_row_len*num_rows as tableSize from dba_tables WHERE table_name='IKR_VALUE' OR table_name='IKR_DEFINITION' "
		    			+ "OR table_name='ALERT_EVENT'";

		    Date now = new Date();
		    
		    try {
		    	con = dataSource.getConnection();

		        pStmt = con.prepareStatement(query);
	            rs = pStmt.executeQuery();
	            while (rs.next()) {  
	 	          	String ikrInstance = rs.getString("table_name");
	 	          	Double tableRows = rs.getDouble("num_rows");	          	
	 	          	Double tableSize = rs.getDouble("tableSize");
	 	          	
	 	          	CustoIkrInstance ikrInstanceData = new CustoIkrInstance(ikrInstance+"@Monitor",
	 	          													    	String.valueOf(tableSize),
	 	          													    	String.valueOf(tableRows),
	 	          													    	now);
	 	          	
	 	          	res.add(ikrInstanceData);	
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
	}

	@Override
	protected void preFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}	
}
