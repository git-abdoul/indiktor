package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class IkrValueBeanCacheLoader
implements CacheLoader {

	private static final Logger logger = Logger.getLogger(IkrValueBeanCacheLoader.class);
	
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
		logger.error("IkrValueBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection arg0, Object arg1) throws CacheException {
		logger.error("IkrValueBeanCacheLoader - loadAll");
		return null;
	}

	public Object load(Object arg0) throws CacheException {
		Long ikrValueId = (Long)arg0;
		
		IkrValueBean bean = instanciateIkrValueBean(ikrValueId);
		return bean;
	}

	public Map loadAll(Collection arg0) throws CacheException {
		if (arg0 == null || arg0.isEmpty()) {
			return new HashMap<Long,IkrValueBean>();
		}
		
		Map<Long,IkrValueBean> res = new HashMap<Long,IkrValueBean>();
		
		try {
			System.out.println("IkrValueBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			long now = System.currentTimeMillis();
			int i = 0;
			for (Object objId : arg0) {
				Long ikrValueId = (Long)objId;
				
				IkrValueBean bean = instanciateIkrValueBean(ikrValueId);
				res.put(ikrValueId, bean);
				
				if (i%1000==0){
					long t = System.currentTimeMillis();
					long dt = t-now;
					System.out.println("IkrValueBeanCacheLoader - "+ new Date() + " - count = " + i + "  - dt = " + dt);
				}
				
				i++;
			}
			
			System.out.println("IkrValueBeanCacheLoader - "+ new Date() + " - Bean loaded");
		
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("IkrValueBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}
	
	private IkrValueBean instanciateIkrValueBean(long ikrValueId) {
		IkrValueBean res = null;
		try {			

			IkrValue ikrValue = monitoringPM.getIkrValue(ikrValueId);

			if (ikrValue != null) {
				IkrDefinitionBean ikrDefinitionBean = beanPM.getIkrDefinitionBean(ikrValue.getValueDefinitionId());
				res = new IkrValueBean(ikrDefinitionBean,ikrValue);
			}
			
		} catch(Exception exc) {
			logger.error("IkrValueBeanCacheLoader.instanciateIkrValueBean",exc);
		}
		return res;
	}

}
