package com.fsi.monitoring.indiktor.cacheLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.indiktor.dao.MonitorDAO;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

public class IkrValueCacheLoader 
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(IkrValueCacheLoader.class);
	
	private MonitorDAO monitorDAO = null;
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}
	
	public IkrValueCacheLoader() {
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

//	public void init() {
//		try {
//			monitorDAO = (MonitorDAO)DAOFactory.getDAO("MonitorDAO");
//		} catch(PersistenceException exc) {
//			logger.fatal(exc);
//		}
//	}
	
	public void setMonitorDAO(MonitorDAO monitorDAO) {
		this.monitorDAO = monitorDAO;
	}

	public Object load(Object arg0, Object arg1)
	throws CacheException {
		System.out.println("---load noarg for IkrValueCache---");
		return null;
	}

	public Map<Integer,IkrCategory> loadAll(Collection arg0, Object arg1)
	throws CacheException {
		System.out.println("---loadAll arg for IkrValueCache---");
		return null;
	}

	public Object load(Object arg0) throws CacheException {
		if (arg0 == null) {
			System.out.println("load for IkrValueCache : arg0 null");
		}
		
		IkrValue res = null;
		
//		System.out.println("LOAD IKR VALUE IN CACHE: ID=" + arg0);
		
		try {
			res = monitorDAO.getIkrValue((Long) arg0);
		} catch (PersistenceException exc) {
			logger.error(exc);
		}
		
		return res;
	}

	public Map<Long,IkrDefinition> loadAll(Collection arg0)
	throws CacheException {
		return null;
	}

}
