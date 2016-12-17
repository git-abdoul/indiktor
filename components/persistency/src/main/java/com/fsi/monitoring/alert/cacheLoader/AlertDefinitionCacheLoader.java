package com.fsi.monitoring.alert.cacheLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.dao.AlertDAO;


import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

public class AlertDefinitionCacheLoader
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(AlertDefinitionCacheLoader.class);	
	
	private AlertDAO alertDAO = null;
	
	public AlertDefinitionCacheLoader() {}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setAlertDAO(AlertDAO alertDAO) {
		this.alertDAO = alertDAO;
	}

	public Object load(Object arg0, Object arg1) throws CacheException {
		System.out.println("---load for AlertDefinitionCacheLoader---");
		return null;
	}

	public Map loadAll(Collection arg0, Object arg1) throws CacheException {
		System.out.println("---loadAll for AlertDefinitionCacheLoader---");
		return null;
	}

	public AlertDefinition load(Object arg0) throws CacheException {
		AlertDefinition res = null;
		
		try {
			res = alertDAO.getAlertDefinition((Long)arg0);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}
		
		return res;
	}

	public Map<Long,AlertDefinition> loadAll(Collection arg0)	
	throws CacheException {
		
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,AlertDefinition>();
		}
			
		Map<Long,AlertDefinition> res = null;			
		try {
			res = alertDAO.getAlertDefinitions((Collection<Long>)arg0); 
		} catch (PersistenceException exc) {
			logger.error(exc);
		}
		
		return res;
	}
}
