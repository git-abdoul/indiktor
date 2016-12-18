package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class MonitorConfigBeanCacheLoader implements CacheLoader {
	private static final Logger logger = Logger.getLogger(MonitorConfigBeanCacheLoader.class);
	
	private DataModelPM dataModelPM;
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}
	
	public Object load(Object key) throws CacheException {
		Long monitorConfigId = (Long)key;
		
		MonitorConfigBean bean = instanciateMonitorConfigBean(monitorConfigId);
		return bean;
	}

	public Map loadAll(Collection keys) throws CacheException {
		if (keys == null || keys.isEmpty()) {
			return new HashMap<Long,IkrCategory>();
		}
		
		Map<Long,MonitorConfigBean> res = new HashMap<Long,MonitorConfigBean>();
		
		try {
			System.out.println("MonitorConfigBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : keys) {
				Long monitorConfigId = (Long)objId;				
				MonitorConfigBean bean = instanciateMonitorConfigBean(monitorConfigId);
				res.put(monitorConfigId, bean);
			}
			
			System.out.println("MonitorConfigBeanCacheLoader - "+ new Date() + " - Bean loaded");
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("MonitorConfigBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}

	public Object load(Object key, Object argument) throws CacheException {
		logger.error("MonitorConfigBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection keys, Object argument) throws CacheException {
		logger.error("MonitorConfigBeanCacheLoader - loadAll");
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private MonitorConfigBean instanciateMonitorConfigBean(long monitorConfigId) {
		MonitorConfigBean res = null;
		try {
			MonitorConfig config = dataModelPM.getMonitorConfig(monitorConfigId);
			LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(config.getLogicalEnvId());
			IkrStaticDomain domain = dataModelPM.getIkrStaticDomain(config.getMetricDomainConfig().getIkrStaticDomainId());	
			Map<Integer, ConnectorConfig> connectorConfigs = dataModelPM.getConnectorConfigs();
			List<String> connectorTypes = new ArrayList<String>();
			for(int conId : config.getConnectorConfigIds()) {
				connectorTypes.add((connectorConfigs.get(conId)).getType());
			}			
			res = new MonitorConfigBean(config, logicalEnv, domain, connectorTypes);
		} catch(Exception exc) {
			System.err.println();
			logger.error("MonitorConfigBeanCacheLoader.instanciateMonitorConfigBean : Monitor Config Id = " + monitorConfigId,exc);
		}
		return res;
	}

}
