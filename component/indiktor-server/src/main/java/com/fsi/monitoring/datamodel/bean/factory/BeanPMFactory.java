package com.fsi.monitoring.datamodel.bean.factory;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrStaticDomainBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.AlertDefinitionBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.ConnectorConfigBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.IkrDefinitionBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.IkrStaticDomainBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.IkrValueBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.LogicalEnvBeanCacheLoader;
import com.fsi.monitoring.datamodel.bean.factory.cacheLoader.MonitorConfigBeanCacheLoader;
import com.fsi.monitoring.datamodel.connector.ConnectorConfigSelectionBean;
import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.histo.HistoPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.user.UserPM;

public class BeanPMFactory
implements BeanPM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(BeanPMFactory.class);	
	
	private CacheManager cacheManager;	

	private MonitoringPM monitoringPM;
	private DataModelPM dataModelPM;
	private AlertPM alertPM;
	private UserPM userPM;
	private HistoPM histoPM;
	
	private int ikrValuePreloadCacheSize;
	private int ikrDefinitionPreloadCacheSize;
	private int alertDefinitionPreloadCacheSize;
	private int monitorConfigPreloadCacheSize;
	private int connectorConfigPreloadCacheSize;
	private int ikrStaticDomainPreloadCacheSize;
	private int logicalEnvPreloadCacheSize;
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}	
	
	public void setAlertPM(AlertPM alertPM) {
		this.alertPM = alertPM;
	}
	
	public void setUserPM(UserPM userPM) {
		this.userPM = userPM;
	}
	
	public void setHistoPM(HistoPM histoPM) {
		this.histoPM = histoPM;
	}	

	public int getIkrValuePreloadCacheSize() {
		return ikrValuePreloadCacheSize;
	}

	public void setIkrValuePreloadCacheSize(int ikrValuePreloadCacheSize) {
		this.ikrValuePreloadCacheSize = ikrValuePreloadCacheSize;
	}

	public int getIkrDefinitionPreloadCacheSize() {
		return ikrDefinitionPreloadCacheSize;
	}

	public void setIkrDefinitionPreloadCacheSize(int ikrDefinitionPreloadCacheSize) {
		this.ikrDefinitionPreloadCacheSize = ikrDefinitionPreloadCacheSize;
	}

	public int getAlertDefinitionPreloadCacheSize() {
		return alertDefinitionPreloadCacheSize;
	}

	public void setAlertDefinitionPreloadCacheSize(
			int alertDefinitionPreloadCacheSize) {
		this.alertDefinitionPreloadCacheSize = alertDefinitionPreloadCacheSize;
	}	

	public int getMonitorConfigPreloadCacheSize() {
		return monitorConfigPreloadCacheSize;
	}

	public void setMonitorConfigPreloadCacheSize(int monitorConfigPreloadCacheSize) {
		this.monitorConfigPreloadCacheSize = monitorConfigPreloadCacheSize;
	}

	public int getConnectorConfigPreloadCacheSize() {
		return connectorConfigPreloadCacheSize;
	}

	public void setConnectorConfigPreloadCacheSize(
			int connectorConfigPreloadCacheSize) {
		this.connectorConfigPreloadCacheSize = connectorConfigPreloadCacheSize;
	}

	public int getIkrStaticDomainPreloadCacheSize() {
		return ikrStaticDomainPreloadCacheSize;
	}

	public void setIkrStaticDomainPreloadCacheSize(
			int ikrStaticDomainPreloadCacheSize) {
		this.ikrStaticDomainPreloadCacheSize = ikrStaticDomainPreloadCacheSize;
	}

	public int getLogicalEnvPreloadCacheSize() {
		return logicalEnvPreloadCacheSize;
	}

	public void setLogicalEnvPreloadCacheSize(int logicalEnvPreloadCacheSize) {
		this.logicalEnvPreloadCacheSize = logicalEnvPreloadCacheSize;
	}

	public void init() {
		initCacheLoaders();	
		preloadLogicalEnvBeanCache();
		preloadIkrStaticDomainBeanCache();
		preloadConnectorConfigBeanCache();
		preloadMonitorConfigBeanCache();
		preloadIkrDefinitionBeanCache();
		preloadAlertDefinitionBeanCache();	
		preloadIkrValueBeanCache();
	}
	
	private void preloadMonitorConfigBeanCache() {
		Ehcache cache = cacheManager.getEhcache("MonitorConfigBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading MonitorConfigBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " MonitorConfigBean - Preload Cache Size: " + monitorConfigPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading MonitorConfigBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " MonitorConfigBean - Preload Cache Size: " + monitorConfigPreloadCacheSize);			
			AschynMonitorConfigBeanLoader loader = new AschynMonitorConfigBeanLoader(monitorConfigPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadConnectorConfigBeanCache() {
		Ehcache cache = cacheManager.getEhcache("ConnectorConfigBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading ConnectorConfigBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " ConnectorConfigBean - Preload Cache Size: " + connectorConfigPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading ConnectorConfigBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " ConnectorConfigBean - Preload Cache Size: " + connectorConfigPreloadCacheSize);			
			AschynConnectorConfigBeanLoader loader = new AschynConnectorConfigBeanLoader(connectorConfigPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadIkrStaticDomainBeanCache() {
		Ehcache cache = cacheManager.getEhcache("IkrStaticDomainBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading IkrStaticDomainBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " IkrStaticDomainBean - Preload Cache Size: " + ikrStaticDomainPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading IkrStaticDomainBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " IkrStaticDomainBean - Preload Cache Size: " + ikrStaticDomainPreloadCacheSize);			
			AschynIkrStaticDomainBeanLoader loader = new AschynIkrStaticDomainBeanLoader(ikrStaticDomainPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadLogicalEnvBeanCache() {
		Ehcache cache = cacheManager.getEhcache("LogicalEnvBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading LogicalEnvBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " LogicalEnvBean - Preload Cache Size: " + logicalEnvPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading LogicalEnvBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " LogicalEnvBean - Preload Cache Size: " + logicalEnvPreloadCacheSize);			
			AschynIkrStaticDomainBeanLoader loader = new AschynIkrStaticDomainBeanLoader(logicalEnvPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadIkrValueBeanCache() {
		Ehcache cache = cacheManager.getEhcache("IkrValueBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading IkrValueBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " IkrValueBean - Preload Cache Size: " + ikrValuePreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading IkrValueBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " IkrValueBean - Preload Cache Size: " + ikrValuePreloadCacheSize);			
			AschynIkrValueBeanLoader loader = new AschynIkrValueBeanLoader(ikrValuePreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadIkrDefinitionBeanCache() {
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");
		try {		
			logger.info("BeanPMFactory "  + new Date() + " Preloading IkrDefinitionBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " IkrDefinitionBean - Preload Cache Size : " + ikrDefinitionPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading IkrDefinitionBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " IkrDefinitionBean - Preload Cache Size : " + ikrDefinitionPreloadCacheSize);			
			AschynIkrDefinitionBeanLoader loader = new AschynIkrDefinitionBeanLoader(ikrDefinitionPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void preloadAlertDefinitionBeanCache() {
		Ehcache cache = cacheManager.getEhcache("AlertDefinitionBean");
		try {	
			logger.info("BeanPMFactory "  + new Date() + " Preloading AlertDefinitionBean in Cache");
			logger.info("BeanPMFactory "  + new Date() + " AlertDefinitionBean - Preload Cache Size : " + alertDefinitionPreloadCacheSize);
			System.out.println("BeanPMFactory "  + new Date() + " Preloading AlertDefinitionBean in Cache");
			System.out.println("BeanPMFactory "  + new Date() + " AlertDefinitionBean - Preload Cache Size : " + alertDefinitionPreloadCacheSize);
			AschynAlertDefinitionBeanLoader loader = new AschynAlertDefinitionBeanLoader(alertDefinitionPreloadCacheSize, cache);
			loader.start();
		} catch(Exception exc) {
			logger.error(exc);
			System.out.println(exc);
		}
	}
	
	private void initCacheLoaders() {
		MonitorConfigBeanCacheLoader monitorConfigBeanCacheLoader = new MonitorConfigBeanCacheLoader();
		monitorConfigBeanCacheLoader.setDataModelPM(dataModelPM);
		Ehcache monitorConfigBeanCache = cacheManager.getEhcache("MonitorConfigBean");
		monitorConfigBeanCache.setCacheLoader(monitorConfigBeanCacheLoader);
		
		ConnectorConfigBeanCacheLoader connectorConfigBeanCacheLoader = new ConnectorConfigBeanCacheLoader();
		connectorConfigBeanCacheLoader.setDataModelPM(dataModelPM);
		Ehcache connectorConfigBeanCache = cacheManager.getEhcache("ConnectorConfigBean");
		connectorConfigBeanCache.setCacheLoader(connectorConfigBeanCacheLoader);
		
		LogicalEnvBeanCacheLoader logicalEnvBeanCacheLoader = new LogicalEnvBeanCacheLoader();
		logicalEnvBeanCacheLoader.setDataModelPM(dataModelPM);
		Ehcache logicalEnvBeanCache = cacheManager.getEhcache("LogicalEnvBean");
		logicalEnvBeanCache.setCacheLoader(logicalEnvBeanCacheLoader);
		
		IkrStaticDomainBeanCacheLoader ikrStaticDomainBeanCacheLoader = new IkrStaticDomainBeanCacheLoader();
		ikrStaticDomainBeanCacheLoader.setDataModelPM(dataModelPM);
		Ehcache ikrStaticDomainBeanCache = cacheManager.getEhcache("IkrStaticDomainBean");
		ikrStaticDomainBeanCache.setCacheLoader(ikrStaticDomainBeanCacheLoader);
		
		IkrValueBeanCacheLoader ikrValueBeanCacheLoader = new IkrValueBeanCacheLoader();
		ikrValueBeanCacheLoader.setDataModelPM(dataModelPM);
		ikrValueBeanCacheLoader.setMonitoringPM(monitoringPM);
		ikrValueBeanCacheLoader.setBeanPM(this);
		Ehcache irkValueBeanCache = cacheManager.getEhcache("IkrValueBean");
		irkValueBeanCache.setCacheLoader(ikrValueBeanCacheLoader);
		
		IkrDefinitionBeanCacheLoader ikrDefinitionBeanCacheLoader = new IkrDefinitionBeanCacheLoader();
		ikrDefinitionBeanCacheLoader.setDataModelPM(dataModelPM);
		ikrDefinitionBeanCacheLoader.setMonitoringPM(monitoringPM);
		ikrDefinitionBeanCacheLoader.setBeanPM(this);
		Ehcache irkDefinitionBeanCache = cacheManager.getEhcache("IkrDefinitionBean");
		irkDefinitionBeanCache.setCacheLoader(ikrDefinitionBeanCacheLoader);
		
		AlertDefinitionBeanCacheLoader alertDefinitionBeanCacheLoader = new AlertDefinitionBeanCacheLoader();
		alertDefinitionBeanCacheLoader.setAlertPM(alertPM);
		alertDefinitionBeanCacheLoader.setDataModelPM(dataModelPM);
		alertDefinitionBeanCacheLoader.setMonitoringPM(monitoringPM);
		alertDefinitionBeanCacheLoader.setUserPM(userPM);
		alertDefinitionBeanCacheLoader.setBeanPM(this);
		Ehcache alertDefinitionCache = cacheManager.getCache("AlertDefinitionBean");
		alertDefinitionCache.setCacheLoader(alertDefinitionBeanCacheLoader);
	}
	
	public void flushIkrDefinitionBeans() {
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");
		cache.flush();
	}
	
	public void flushIkrDefinitionBean(long ikrDefinitionId) {
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");	
		cache.remove(ikrDefinitionId);
	}
	
	public void flushIkrDefinitionBeans(Collection<Long> ikrDefinitionIds) {
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");
		for (long id : ikrDefinitionIds) {
			cache.remove(id);
		}
	}
	
	public void flushAlertDefinitionBean(long alertDefinitionBeanId) {
		Ehcache cache = cacheManager.getEhcache("AlertDefinitionBean");
		cache.remove(alertDefinitionBeanId);
	}	
	
	public void flushLogicalEnvBean(long logicalEnvId) {
		Ehcache cache = cacheManager.getEhcache("LogicalEnvBean");		
		cache.remove(logicalEnvId);
	}

	public void flushMonitorConfigBean(long monitorCongifId) {
		Ehcache cache = cacheManager.getEhcache("MonitorConfigBean");		
		cache.remove(monitorCongifId);
	}

	public void flushConnectorConfigBean(long connectorCongifId) {
		Ehcache cache = cacheManager.getEhcache("ConnectorConfigBean");		
		cache.remove(connectorCongifId);
		
	}

	public void flushIkrStaticDomainBean(int domainId) {
		Ehcache cache = cacheManager.getEhcache("IkrStaticDomainBean");		
		cache.remove(domainId);
	}

	public IkrValueBean getIkrValueBean(long ikrValueId) {
		IkrValueBean res = null;		
		Ehcache cache = cacheManager.getEhcache("IkrValueBean");		
		Element element = cache.getWithLoader(ikrValueId, null, null);		
		if (element != null) {
			res = (IkrValueBean)element.getObjectValue();
		}
		return res;
	}
	
	public AlertDefinitionBean getAlertDefinitionBean(long alertDefinitionId) {
		AlertDefinitionBean res = null;		
		Ehcache cache = cacheManager.getEhcache("AlertDefinitionBean");		
		Element element = cache.getWithLoader(alertDefinitionId, null, null);		
		if (element != null) {
			res = (AlertDefinitionBean)element.getObjectValue();
		}
		return res;
	}
	
	public List<IkrValueBean> getIkrValueBeans(Collection<Long> ikrValueIds) { 
		Ehcache cache = cacheManager.getEhcache("IkrValueBean");				
		Map<Long, IkrValueBean> beans = cache.getAllWithLoader(ikrValueIds, null);		
		return new ArrayList<IkrValueBean>(beans.values());
	}
	
	public abstract class AschynLoader extends Thread {		
		protected int maxSize;		
		protected Ehcache cache;
		
		public AschynLoader(int maxSize, Ehcache cache) {
			this.maxSize = maxSize;
			this.cache = cache;
		}
		
		public void run() {
			load();
		}
		
		protected abstract void load();
	}
	
	public class AschynMonitorConfigBeanLoader extends AschynLoader {
		private Map<Long,MonitorConfigBean> beans;
		
		public AschynMonitorConfigBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Long, MonitorConfigBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Long> ids = null;
			try {
				ids = dataModelPM.getLastMonitorConfigIds(maxSize);
				logger.info("AschynMonitorConfigBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynMonitorConfigBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynMonitorConfigBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());			
			System.out.println("AschynMonitorConfigBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynMonitorConfigBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynMonitorConfigBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Long,MonitorConfigBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynConnectorConfigBeanLoader extends AschynLoader {
		private Map<Integer,ConnectorConfigSelectionBean> beans;
		
		public AschynConnectorConfigBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Integer, ConnectorConfigSelectionBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Integer> ids = null;
			try {
				ids = dataModelPM.getLastConnectorConfigIds(maxSize);
				logger.info("AschynConnectorConfigBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynConnectorConfigBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynConnectorConfigBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());			
			System.out.println("AschynConnectorConfigBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynConnectorConfigBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynConnectorConfigBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Integer,ConnectorConfigSelectionBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynIkrStaticDomainBeanLoader extends AschynLoader {
		private Map<Integer,IkrStaticDomainBean> beans;
		
		public AschynIkrStaticDomainBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Integer, IkrStaticDomainBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Integer> ids = null;
			try {
				ids = dataModelPM.getLastIkrStaticDomainIds(maxSize);
				logger.info("AschynIkrStaticDomainBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynIkrStaticDomainBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynIkrStaticDomainBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());			
			System.out.println("AschynIkrStaticDomainBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynIkrStaticDomainBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynIkrStaticDomainBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Integer,IkrStaticDomainBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynLogicalEnvBeanLoader extends AschynLoader {
		private Map<Integer,LogicalEnvBean> beans;
		
		public AschynLogicalEnvBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Integer, LogicalEnvBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Integer> ids = null;
			try {
				ids = dataModelPM.getLastLogicalenvIds(maxSize);
				logger.info("AschynLogicalEnvBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynLogicalEnvBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynLogicalEnvBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());			
			System.out.println("AschynLogicalEnvBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynLogicalEnvBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynLogicalEnvBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Integer,LogicalEnvBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynIkrValueBeanLoader extends AschynLoader {
		private Map<Long,IkrValueBean> beans;
		
		public AschynIkrValueBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Long, IkrValueBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Long> ids = null;
			try {
				ids = monitoringPM.getLastIkrValueIds(maxSize);
				logger.info("AschynIkrValueBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynIkrValueBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynIkrValueBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());			
			System.out.println("AschynIkrValueBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynIkrValueBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynIkrValueBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Long,IkrValueBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynIkrDefinitionBeanLoader extends AschynLoader {
		private Map<Long,IkrDefinitionBean> beans;
		
		public AschynIkrDefinitionBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Long, IkrDefinitionBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Long> ids = null;
			try {
				ids = monitoringPM.getLastIkrDefinitionIds(maxSize);
				logger.info("AschynIkrDefinitionBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynIkrDefinitionBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}			
			logger.info("AschynIkrDefinitionBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			System.out.println("AschynIkrDefinitionBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynIkrDefinitionBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynIkrDefinitionBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Long,IkrDefinitionBean> getBeans() {
			return beans;
		}
	}
	
	public class AschynAlertDefinitionBeanLoader extends AschynLoader {
		private Map<Long,AlertDefinitionBean> beans;
		
		public AschynAlertDefinitionBeanLoader(int maxSize, Ehcache cache) {
			super(maxSize, cache);
			beans = new HashMap<Long, AlertDefinitionBean>();
		}	
		
		@Override
		protected void load() {
			Collection<Long> ids = null;
			try {
				ids = alertPM.getLastAlertDefinitionIds(maxSize);
				logger.info("AschynAlertDefinitionBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
				System.out.println("AschynAlertDefinitionBeanLoader "  + new Date() + "IDs brought up : " + ids.size());
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("AschynAlertDefinitionBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			System.out.println("AschynAlertDefinitionBeanLoader " + new Date() + " CACHE SIZE BEFORE: " + cache.getSize());
			if (ids!=null && ids.size()>0)
				beans = cache.getAllWithLoader(ids, null);
			logger.info("AschynAlertDefinitionBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());
			System.out.println("AschynAlertDefinitionBeanLoader " + new Date() + " CACHE SIZE AFTER: " + cache.getSize());			
		}
		
		public Map<Long,AlertDefinitionBean> getBeans() {
			return beans;
		}
	}
	
	public List<IkrValueBean> getLastIkrValuesBeanByIkrDefinition (long ikrDefinitionId, int nbOfValues) throws PersistenceException {
		List<Long> ikrValueIds = histoPM.getLastIkrValuesId(ikrDefinitionId, nbOfValues);
		List<IkrValueBean> ikrValueBeans = getIkrValueBeans(ikrValueIds);
		Collections.sort(ikrValueBeans, new Comparator<IkrValueBean>() {
			public int compare(IkrValueBean o1, IkrValueBean o2) {
				return o1.getIkrValue().getCaptureTime().compareTo(o2.getIkrValue().getCaptureTime());
			}
		});		
		return ikrValueBeans;
	}
	
	public List<IkrValueBean> getIkrValueBeansByIkrDefinition(long ikrDefinitionId, Date fromDate) throws PersistenceException {
		List<Long> ikrDefinitionIds = new ArrayList<Long>();
		ikrDefinitionIds.add(ikrDefinitionId);
		Collection<Long> ikrValueIds = histoPM.getIkrValueIds(ikrDefinitionIds, fromDate, null);
		List<IkrValueBean> ikrValueBeans = getIkrValueBeans(ikrValueIds);
		Collections.sort(ikrValueBeans, new Comparator<IkrValueBean>() {
			public int compare(IkrValueBean o1, IkrValueBean o2) {
				return o1.getIkrValue().getCaptureTime().compareTo(o2.getIkrValue().getCaptureTime());
			}
		});		
		return ikrValueBeans;
	}

	public IkrDefinitionBean getIkrDefinitionBean(long ikrDefinitionId) {
		IkrDefinitionBean res = null;		
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");		
		Element element = cache.getWithLoader(ikrDefinitionId, null, null);		
		if (element != null) {
			res = (IkrDefinitionBean)element.getObjectValue();
		}
		return res;	
	}

	public List<IkrDefinitionBean> getIkrDefinitionBeans(Collection<Long> ikrDefinitionIds) {
		Ehcache cache = cacheManager.getEhcache("IkrDefinitionBean");				
		Map<Long, IkrDefinitionBean> beans = cache.getAllWithLoader(ikrDefinitionIds, null);		
		return new ArrayList<IkrDefinitionBean>(beans.values());
	}

	public List<AlertDefinitionBean> getAlertDefinitionBeans(Collection<Long> alertDefinitionIds) {
		Ehcache cache = cacheManager.getEhcache("AlertDefinitionBean");				
		Map<Long, AlertDefinitionBean> beans = cache.getAllWithLoader(alertDefinitionIds, null);		
		return new ArrayList<AlertDefinitionBean>(beans.values());
	}

	public LogicalEnvBean getLogicalEnvBean(int logicalenvId) {
		LogicalEnvBean res = null;		
		Ehcache cache = cacheManager.getEhcache("LogicalEnvBean");		
		Element element = cache.getWithLoader(logicalenvId, null, null);		
		if (element != null) {
			res = (LogicalEnvBean)element.getObjectValue();
		}
		return res;	
	}

	public List<LogicalEnvBean> getLogicalEnvBeans(Collection<Integer> logicalenvIds) {
		Ehcache cache = cacheManager.getEhcache("LogicalEnvBean");				
		Map<Integer, LogicalEnvBean> beans = cache.getAllWithLoader(logicalenvIds, null);		
		return new ArrayList<LogicalEnvBean>(beans.values());
	}

	public MonitorConfigBean getMonitorConfigBean(long monitorCongifId) {
		MonitorConfigBean res = null;		
		Ehcache cache = cacheManager.getEhcache("MonitorConfigBean");		
		Element element = cache.getWithLoader(monitorCongifId, null, null);		
		if (element != null) {
			res = (MonitorConfigBean)element.getObjectValue();
		}
		return res;	
	}

	public List<MonitorConfigBean> getMonitorConfigBeans(Collection<Long> monitorCongifIds) {
		Ehcache cache = cacheManager.getEhcache("MonitorConfigBean");				
		Map<Long, MonitorConfigBean> beans = cache.getAllWithLoader(monitorCongifIds, null);		
		return new ArrayList<MonitorConfigBean>(beans.values());
	}

	public ConnectorConfigSelectionBean getConnectorConfigBean(int connectorCongifId) {
		ConnectorConfigSelectionBean res = null;		
		Ehcache cache = cacheManager.getEhcache("ConnectorConfigBean");		
		Element element = cache.getWithLoader(connectorCongifId, null, null);		
		if (element != null) {
			res = (ConnectorConfigSelectionBean)element.getObjectValue();
		}
		return res;	
	}

	public List<ConnectorConfigSelectionBean> getConnectorConfigBeans(Collection<Integer> connectorCongifIds) {
		Ehcache cache = cacheManager.getEhcache("ConnectorConfigBean");				
		Map<Integer, ConnectorConfigSelectionBean> beans = cache.getAllWithLoader(connectorCongifIds, null);		
		return new ArrayList<ConnectorConfigSelectionBean>(beans.values());
	}

	public IkrStaticDomainBean getIkrStaticDomainBean(int domainId) {
		IkrStaticDomainBean res = null;		
		Ehcache cache = cacheManager.getEhcache("IkrStaticDomainBean");		
		Element element = cache.getWithLoader(domainId, null, null);		
		if (element != null) {
			res = (IkrStaticDomainBean)element.getObjectValue();
		}
		return res;	
	}

	public List<IkrStaticDomainBean> getIkrStaticDomainBeans(Collection<Integer> domainIdIds) {
		Ehcache cache = cacheManager.getEhcache("IkrStaticDomainBean");				
		Map<Integer, IkrStaticDomainBean> beans = cache.getAllWithLoader(domainIdIds, null);		
		return new ArrayList<IkrStaticDomainBean>(beans.values());
	}	
}
