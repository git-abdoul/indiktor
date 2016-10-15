package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.datamodel.connector.ConnectorConfigSelectionBean;
import com.fsi.monitoring.indiktor.DataModelPM;

public class ConnectorConfigBeanCacheLoader implements CacheLoader {
	private static final Logger logger = Logger.getLogger(ConnectorConfigBeanCacheLoader.class);
	
	private DataModelPM dataModelPM;	
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public Object load(Object key) throws CacheException {
		Integer connectorConfigId = (Integer)key;		
		ConnectorConfigSelectionBean bean = instanciateConnectorConfigBean(connectorConfigId);
		return bean;
	}

	public Map loadAll(Collection keys) throws CacheException {
		if (keys == null || keys.isEmpty()) {
			return new HashMap<Integer,ConnectorConfigSelectionBean>();
		}
		
		Map<Integer,ConnectorConfigSelectionBean> res = new HashMap<Integer,ConnectorConfigSelectionBean>();
		
		try {
			System.out.println("ConnectorConfigBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : keys) {
				Integer connectorConfigId = (Integer)objId;				
				ConnectorConfigSelectionBean bean = instanciateConnectorConfigBean(connectorConfigId);
				res.put(connectorConfigId, bean);
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
		logger.error("ConnectorConfigBeanCacheLoader - loadAll");
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ConnectorConfigSelectionBean instanciateConnectorConfigBean(int connectorConfigId) {
		ConnectorConfigSelectionBean res = null;
		try {
			ConnectorConfig config = dataModelPM.getConnectorConfig(connectorConfigId);			
			res = new ConnectorConfigSelectionBean(config);
		} catch(Exception exc) {
			logger.error("ConnectorConfigBeanCacheLoader.instanciateConnectorConfigBean",exc);
		}
		return res;
	}

}
