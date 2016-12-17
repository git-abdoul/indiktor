package com.fsi.monitoring.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;


public class PersistencyConfig {

	private String cacheConfigFileName;
	
	private DataSource pooledMonitorDatasource;
	private Map<String,String> daoClassNames;
	
	private String dataModelFileName;
	
	public PersistencyConfig() {
		daoClassNames = new HashMap<String,String>();
	}
	
	public DataSource getPooledMonitorDatasource() {
		return pooledMonitorDatasource;
	}
	
	public String getDataModelFileName() {
		return dataModelFileName;
	}
	
	public String getDaoClassName(String daoName) {
		return daoClassNames.get(daoName);
	}
	
	public String getCacheConfigFileName() {
		return cacheConfigFileName;
	}
	
	// ------------------------------------------------------------------- //
	protected void setDataModelFileName(String dataModelFileName) {
		this.dataModelFileName = dataModelFileName;
	}
	
	protected void addDaoClassName(String daoName, String daoClassName) {
		daoClassNames.put(daoName, daoClassName);
	}
	
	protected void setPooledMonitorDatasource(DataSource dataSource) {
		this.pooledMonitorDatasource = dataSource;
	}
	
	protected void setCacheConfigFileName(String fileName) {
		this.cacheConfigFileName = fileName;
	}
}
