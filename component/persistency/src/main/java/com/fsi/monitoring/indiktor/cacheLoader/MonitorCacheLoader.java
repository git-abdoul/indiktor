package com.fsi.monitoring.indiktor.cacheLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.dao.DataModelDAO;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

public class MonitorCacheLoader 
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(MonitorCacheLoader.class);
	
	private DataModelDAO dataModelDAO = null;
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}
	
	public MonitorCacheLoader() {
		//init();
	}

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

	public Map<Integer,IkrCategory> loadAll(Collection arg0, Object arg1)
	throws CacheException {
		System.out.println("---loadAll arg for MonitorCache---");
		return null;
	}

	public MonitorConfig load(Object arg0) throws CacheException {
		Long monitorId = (Long)arg0;
		
		MonitorConfig res = null;
		
		try {
			res = dataModelDAO.loadMonitorConfig(monitorId);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}		

		return res;
	}

	public Map<Long,MonitorConfig> loadAll(Collection arg0)
	throws CacheException {
	
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,MonitorConfig>();
		}
		
		Map<Long,MonitorConfig> res = null;
		
		try {
			res = dataModelDAO.loadMonitorConfigs((Collection<Long>)arg0);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}
		
		return res;
	}

}
