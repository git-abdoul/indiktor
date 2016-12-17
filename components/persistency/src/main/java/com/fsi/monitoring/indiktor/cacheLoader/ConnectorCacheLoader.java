package com.fsi.monitoring.indiktor.cacheLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.indiktor.dao.DataModelDAO;

public class ConnectorCacheLoader 
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(ConnectorCacheLoader.class);
	
	private DataModelDAO dataModelDAO = null;
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}
	
	public ConnectorCacheLoader() {}

	public void dispose() throws CacheException {
	}

	public String getName() {
		return null;
	}

	public Status getStatus() {
		return null;
	}
	
	public void setDataModelDAO(DataModelDAO dataModelDAO) {
		this.dataModelDAO = dataModelDAO;
	}

	public Object load(Object arg0, Object arg1)
			throws CacheException {
		return null;
	}

	public Map<Integer,ConnectorConfig> loadAll(Collection arg0, Object arg1)
	throws CacheException {
		System.out.println("---loadAll arg for ConnectorCache---");
		return null;
	}

	public ConnectorConfig load(Object arg0) throws CacheException {
		int connectorId = (Integer)arg0;		
		ConnectorConfig res = null;		
		try {
			res = dataModelDAO.getConnectorConfig(connectorId);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}		
		return res;
	}

	public Map<Integer,ConnectorConfig> loadAll(Collection arg0)
	throws CacheException {	
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Integer,ConnectorConfig>();
		}		
		Map<Integer,ConnectorConfig> res = null;		
		try {
			res = dataModelDAO.getConnectorConfigs((Collection<Integer>)arg0);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}		
		return res;
	}

}
