package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.msd.StaticData;

public class IkrDefinitionBeanCacheLoader
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(IkrDefinitionBeanCacheLoader.class);
	
	private MonitoringPM monitoringPM;
	private DataModelPM dataModelPM;	
	
	private BeanPM beanPM;
	
	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}
	
	public void setBeanPM(BeanPM beanPM) {
		this.beanPM = beanPM;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object load(Object arg0, Object arg1) throws CacheException {
		logger.error("IkrDefinitionBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection arg0, Object arg1) throws CacheException {
		logger.error("IkrDefinitionBeanCacheLoader - loadAll");
		return null;
	}

	public Object load(Object arg0) throws CacheException {
		Long ikrDefinitionId = (Long)arg0;
		
		IkrDefinitionBean bean = instanciateIkrDefinitionBean(ikrDefinitionId);
		return bean;
	}

	public Map loadAll(Collection arg0) throws CacheException {
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,IkrDefinitionBean>();
		}
		
		Map<Long,IkrDefinitionBean> res = new HashMap<Long,IkrDefinitionBean>();
		
		try {
			System.out.println("IkrDefinitionBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : arg0) {
				Long ikrDefinitionId = (Long)objId;				
				IkrDefinitionBean bean = instanciateIkrDefinitionBean(ikrDefinitionId);
				if (bean!=null)
					res.put(ikrDefinitionId, bean);
			}
			
			System.out.println("IkrDefinitionBeanCacheLoader - "+ new Date() + " - Bean loaded");
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("IkrDefinitionBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}
	
	private IkrDefinitionBean instanciateIkrDefinitionBean(long ikrDefinitionId) {
		IkrDefinitionBean res = null;
		try {
			AbstractIkrDefinition ikrDefinition = monitoringPM.getIkrDefinition(ikrDefinitionId);
			IkrCategory ikrCategory = (IkrCategory)beanPM.getIkrStaticDomainBean(ikrDefinition.getIkrCategoryId()).getIkrStaticDomain();
			
			IkrStaticDomain metricDomain = beanPM.getIkrStaticDomainBean(ikrCategory.getParentDomainId()).getIkrStaticDomain();
			IkrStaticDomain domainType = beanPM.getIkrStaticDomainBean(metricDomain.getParentDomainId()).getIkrStaticDomain();
			
			String context = null;
			
			LogicalEnv logicalEnv = null;
			String domainView = "";
			
			if (ikrDefinition instanceof IkrDefinition) {
				IkrDefinition def = (IkrDefinition)ikrDefinition;
				MonitorConfigBean bean = beanPM.getMonitorConfigBean(def.getMonitorId());
				if (bean!=null) {
					MonitorConfig monitorConfig = bean.getMonitorConfig();
					context = monitorConfig.getContext();
					logicalEnv = beanPM.getLogicalEnvBean(monitorConfig.getLogicalEnvId()).getLogicalEnv();
					domainView = monitorConfig.getDomainView();
				}
				else {
					logger.warn("Ikr Definition id="+def.getId()+" is linked to a non-existing collector id="+def.getMonitorId());
				}
			} 
			else if (ikrDefinition instanceof StaticData) {
				StaticData sd = (StaticData)ikrDefinition;
				context = StaticData.STATIC_DATA_CONTEXT;
				logicalEnv = beanPM.getLogicalEnvBean(sd.getLogicalEnvId()).getLogicalEnv();
			}
			else {
				CrossComputeDefinition def = (CrossComputeDefinition)ikrDefinition;
				context = CrossComputeDefinition.CROSS_COMPUTE_CONTEXT;
				logicalEnv = beanPM.getLogicalEnvBean(def.getLogicalEnvId()).getLogicalEnv();
			}

			res = new IkrDefinitionBean(ikrDefinition,
										ikrCategory,
										context,
										logicalEnv,
										domainView);
			res.setDomainType(domainType);
			res.setMetricDomain(metricDomain);
		} catch(Exception exc) {
			logger.error("IkrDefinitionBeanCacheLoader.instanciateIkrDefinitionBean",exc);
		}
		return res;
	}

}
