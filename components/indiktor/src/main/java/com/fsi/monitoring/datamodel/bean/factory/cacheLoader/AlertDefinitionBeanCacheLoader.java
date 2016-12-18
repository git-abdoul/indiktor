package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.user.UserPM;

public class AlertDefinitionBeanCacheLoader
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(AlertDefinitionBeanCacheLoader.class);
	
	private AlertPM alertPM;
	private MonitoringPM monitoringPM;
	private DataModelPM dataModelPM;
	private UserPM userPM;
	private BeanPM beanPM;
	
	public void setAlertPM(AlertPM alertPM) {
		this.alertPM = alertPM;
	}
	
	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}	
	
	public void setUserPM(UserPM userPM) {
		this.userPM = userPM;
	}
	
	public void setBeanPM(BeanPM beanPM) {
		this.beanPM = beanPM;
	}
	
	public String getName() {
		return null;
	}

	public Object load(Object arg0, Object arg1) throws CacheException {
		logger.error("AlertDefinitionBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection arg0, Object arg1) throws CacheException {
		logger.error("AlertDefinitionBeanCacheLoader - loadAll");
		return null;
	}

	public Object load(Object arg0) throws CacheException {
		Long alertDefinitionId = (Long)arg0;
		
		AlertDefinitionBean bean = instanciateAlertDefinitionBean(alertDefinitionId);
		return bean;
	}

	public Map loadAll(Collection arg0) throws CacheException {
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,AlertDefinitionBean>();
		}
		
		Map<Long,AlertDefinitionBean> res = new HashMap<Long,AlertDefinitionBean>();
		
		try {
			System.out.println("AlertDefinitionBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : arg0) {
				Long alertDefinitionId = (Long)objId;			
				AlertDefinitionBean bean = instanciateAlertDefinitionBean(alertDefinitionId);
				res.put(alertDefinitionId, bean);
			}		
			System.out.println("AlertDefinitionBeanCacheLoader - "+ new Date() + " - Bean loaded");
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("AlertDefinitionBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}
	
	private AlertDefinitionBean instanciateAlertDefinitionBean(long alertDefinitionId) {
		AlertDefinitionBean res = null;
		try {			
			AlertDefinition alertDefinition = alertPM.getAlertDefinitionById(alertDefinitionId);
			
			res = new AlertDefinitionBean(alertDefinition,alertPM,dataModelPM,monitoringPM,userPM,beanPM);
		} catch(Exception exc) {
			logger.error("AlertDefinitionBeanCacheLoader.instanciateAlertDefinitionBean",exc);
		}
		return res;
	}

}
