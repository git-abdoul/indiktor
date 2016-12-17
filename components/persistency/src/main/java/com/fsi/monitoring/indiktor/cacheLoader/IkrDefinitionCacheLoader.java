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
import com.fsi.monitoring.indiktor.dao.MonitorDAO;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class IkrDefinitionCacheLoader 
implements CacheLoader {
	private static final Logger logger = Logger.getLogger(IkrDefinitionCacheLoader.class);
	
	private MonitorDAO monitorDAO = null;
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}
	
	public IkrDefinitionCacheLoader() {}

	public void dispose() throws CacheException {}

	public String getName() {
		return null;
	}

	public Status getStatus() {
		return null;
	}
	
	public void setMonitorDAO(MonitorDAO monitorDAO) {
		this.monitorDAO = monitorDAO;
	}

	public Object load(Object arg0, Object arg1) throws CacheException {
		return null;
	}

	public Map<Integer,IkrCategory> loadAll(Collection arg0, Object arg1)
	throws CacheException {
		System.out.println("---loadAll arg for IkrDefinitionCache---");
		return null;
	}

	public Object load(Object arg0) throws CacheException {
		if (arg0 == null) {
			System.out.println("load for IkrDefinitionCache : arg0 null");
		}
		
		AbstractIkrDefinition res = null;		
		try {
			res = monitorDAO.getIkrDefinition((Long) arg0);
		} catch (PersistenceException exc) {
			logger.error(exc.getMessage(), exc);
		}
		
		return res;
	}

	public Map<Long,AbstractIkrDefinition> loadAll(Collection arg0)
	throws CacheException {
	
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,AbstractIkrDefinition>();
		}
		
		Map<Long,AbstractIkrDefinition> res = null;		
		try {
			res = monitorDAO.getIkrDefinitions((Collection<Long>)arg0);
		} catch (PersistenceException exc) {
			logger.error(exc.getMessage(), exc);
		}
		
		return res;
	}

}
