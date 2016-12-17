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
import com.fsi.monitoring.indiktor.dao.DataModelDAO;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;

public class ScheduledTaskCacheLoader 
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(ScheduledTaskCacheLoader.class);
	
	private DataModelDAO dataModelDAO = null;
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}
	
	public ScheduledTaskCacheLoader() {}

	public void dispose() throws CacheException {}

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

	public Map<Integer,IkrJobSchedulerConfig> loadAll(Collection arg0, Object arg1)
	throws CacheException {
		System.out.println("---loadAll arg for MonitorCache---");
		return null;
	}

	public IkrJobSchedulerConfig load(Object arg0) throws CacheException {
		int taskId = (Integer)arg0;
		
		IkrJobSchedulerConfig res = null;
		
		try {
			res = dataModelDAO.getJobSchedulerConfig(taskId);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}		

		return res;
	}

	public Map<Integer,IkrJobSchedulerConfig> loadAll(Collection arg0)
	throws CacheException {
	
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Integer,IkrJobSchedulerConfig>();
		}
		
		Map<Integer,IkrJobSchedulerConfig> res = null;
		
		try {
			res = dataModelDAO.loadJobSchedulerConfigs((Collection<Integer>)arg0);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}
		
		return res;
	}

}
