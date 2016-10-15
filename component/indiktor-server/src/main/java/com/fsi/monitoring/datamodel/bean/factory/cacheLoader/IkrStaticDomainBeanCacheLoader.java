package com.fsi.monitoring.datamodel.bean.factory.cacheLoader;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.jsr107cache.CacheException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrStaticDomainBean;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;

public class IkrStaticDomainBeanCacheLoader implements CacheLoader {
	private static final Logger logger = Logger.getLogger(IkrStaticDomainBeanCacheLoader.class);
	
	private DataModelPM dataModelPM;	
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public Object load(Object key) throws CacheException {
		Integer domainId = (Integer)key;		
		IkrStaticDomainBean bean = instanciateIkrStaticDomainBean(domainId);
		return bean;
	}

	public Map loadAll(Collection keys) throws CacheException {
		if (keys == null || keys.isEmpty()) {
			return new HashMap<Integer,IkrStaticDomainBean>();
		}
		
		Map<Integer,IkrStaticDomainBean> res = new HashMap<Integer,IkrStaticDomainBean>();
		
		try {
			System.out.println("IkrStaticDomainBeanCacheLoader - "+ new Date() + " - Start loading Bean ...");
			for (Object objId : keys) {
				Integer domainId = (Integer)objId;				
				IkrStaticDomainBean bean = instanciateIkrStaticDomainBean(domainId);
				res.put(domainId, bean);
			}
			
			System.out.println("IkrStaticDomainBeanCacheLoader - "+ new Date() + " - Bean loaded");
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println("IkrStaticDomainBeanCacheLoader - "+ new Date() + " - Bean Error");
		}
		
		return res;
	}

	public Object load(Object key, Object argument) throws CacheException {
		logger.error("IkrStaticDomainBeanCacheLoader - load");
		return null;
	}

	public Map loadAll(Collection keys, Object argument) throws CacheException {
		logger.error("IkrStaticDomainBeanCacheLoader - loadAll");
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private IkrStaticDomainBean instanciateIkrStaticDomainBean(int domainId) {
		IkrStaticDomainBean res = null;
		try {
			IkrStaticDomain domain = dataModelPM.getIkrStaticDomain(domainId);	
			res = new IkrStaticDomainBean(domain);
		} catch(Exception exc) {
			logger.error("IkrStaticDomainBeanCacheLoader.instanciateConnectorConfigBean",exc);
		}
		return res;
	}

}
