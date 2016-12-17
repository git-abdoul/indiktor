package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;

public class LogicalEnvBeanCacheLoader implements CacheLoader {
	private static final Logger logger = Logger.getLogger(LogicalEnvBeanCacheLoader.class);
	
	private DataModelPM dataModelPM;	
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public Object load(Object key) throws CacheException {
		Integer id = (Integer)key;		
		LogicalEnvBean bean = instanciateLogicalEnvBean(id);
		return bean;
	}

	public Map loadAll(Collection keys) throws CacheException {
		if (keys == null || keys.isEmpty()) {
			return new HashMap<Integer,LogicalEnvBean>();
		}
		
		Map<Integer,LogicalEnvBean> res = new HashMap<Integer,LogicalEnvBean>();
		
		try {
			System.out.println("ConnectorConfigBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : keys) {
				Integer id = (Integer)objId;				
				LogicalEnvBean bean = instanciateLogicalEnvBean(id);
				res.put(id, bean);
			}
			
			System.out.println("ConnectorConfigBeanCacheLoader - "+ new Date() + " - Bean loaded");
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("ConnectorConfigBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}

	public Object load(Object key, Object argument) throws CacheException {
		logger.error("ConnectorConfigBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection keys, Object argument) throws CacheException {
		logger.error("IkrDefinitionBeanCacheLoader - loadAll");
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private LogicalEnvBean instanciateLogicalEnvBean(int id) {
		LogicalEnvBean res = null;
		try {
			LogicalEnv config = dataModelPM.getLogicalEnv(id);			
			res = new LogicalEnvBean(config);
		} catch(Exception exc) {
			logger.error("IkrDefinitionBeanCacheLoader.instanciateIkrDefinitionBean",exc);
		}
		return res;
	}

}
