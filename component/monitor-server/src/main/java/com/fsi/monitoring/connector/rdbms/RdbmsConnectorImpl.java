package com.fsi.monitoring.connector.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.AbstractConnector;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.dao.PooledDataSourceFactory;


public class RdbmsConnectorImpl
extends AbstractConnector 
implements RdbmsConnector {
	
	private static final Logger logger = Logger.getLogger(RdbmsConnectorImpl.class);	
	
	//Default maximum number of active connections 
	private static final int DEFAULT_CONNECTION_POOL_SIZE = 30;
	//Default time to wait when pool exhausted
	private static final int DEFAULT_TIME_WAIT_BLOCKED = -1;	
	
	private RdbmsConnectorConfig connectorConfig;
	
	private DataSource dsSource;
	
	public RdbmsConnectorImpl(RdbmsConnectorConfig connectorConfig) {
		super(connectorConfig);
		this.connectorConfig = connectorConfig;
	}
	
	@Override
	public void closeConnection() throws Exception {
		dsSource = null;
	}

	@Override
	public void openConnection() throws Exception {
		String driver = connectorConfig.getDriver();

		try {
				Class.forName(driver);
		} catch (ClassNotFoundException exc) {
			logger.error("Database Driver not found", exc);
			throw new Exception("RDBMS Driver not found", exc);			
		}
		
		PooledDataSourceFactory dataSourceFactory = new PooledDataSourceFactory();
		dsSource = dataSourceFactory.createDataSource(DEFAULT_TIME_WAIT_BLOCKED, 
				 						  			  DEFAULT_CONNECTION_POOL_SIZE, 
				 						  			  connectorConfig.getUri(), 
													  connectorConfig.getUserName(), 
													  connectorConfig.getPassword());
		if (dsSource == null) {
			throw new Exception("Database DataSource cannot be null");
		}
		
		testConnection();
	}

	public String getConnectorContext() {
		return connectorConfig.getConnectorContext();
	}
	
	private void testConnection() throws Exception {
		Connection con = null;
		Statement stmt = null;
        try
        {
        	con = dsSource.getConnection();
            stmt = con.createStatement();
        } finally {
        	if (stmt != null)
        		stmt.close();
        	if (con != null)
        		con.close();       
        }
	}
	
	private boolean isOracle() {
		String driver = connectorConfig.getDriver();
		return driver.toLowerCase().contains("oracle");
	}
	
	private boolean isSybase() {
		String driver = connectorConfig.getDriver();
		return driver.toLowerCase().contains("sybase");
	}
	
	private boolean isMysql() {
		String driver = connectorConfig.getDriver();
		return driver.toLowerCase().contains("mysql");
	}
	
	public Connection getConnection() 
	throws ConnectorException {
		checkStatus();
		
		if (dsSource == null) {
			reportFailure("Database DataSource is null");
		}
		
		Connection connection = null;
		try {
			connection = dsSource.getConnection();
			if (connection.isClosed()) {
				reportFailure("Database Connection is closed");
			}
		} catch (Exception exc) {
			reportFailure(exc);
		}

		return connection;
	}	
	
	public void rollbackConnection(Connection con) throws PersistenceException {		
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}	

	public void closeResultSet(ResultSet rs) throws PersistenceException {		
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}
	
	public void closeStatement(Statement stmt) throws PersistenceException  {
		try {
			if (stmt !=null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}	
	
	public void closeConnection(Connection con) throws PersistenceException  {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}
}
