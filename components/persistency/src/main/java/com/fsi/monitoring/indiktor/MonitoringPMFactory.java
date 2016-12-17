package com.fsi.monitoring.indiktor;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.global.IdGenerator;
import com.fsi.monitoring.indiktor.dao.MonitorDAO;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;

public class MonitoringPMFactory 
implements MonitoringPM {

	private static final Logger logger = Logger.getLogger(MonitoringPMFactory.class);
		
	private MonitorDAO monitorDAO;
	
	private CacheManager cacheManager;	
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	public void setMonitorDAO(MonitorDAO monitorDAO) {
		this.monitorDAO = monitorDAO;
	}

	public Map<Long, AbstractIkrDefinition> getIkrDefinitions(long monitorId)
	throws PersistenceException {
		List<Long> ids = monitorDAO.getIkrDefinitionIds(monitorId);		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}
	
	public List<Long> getIkrDefinitionIds(long monitorId) throws PersistenceException {
		return monitorDAO.getIkrDefinitionIds(monitorId);
	}
	
	public List<Long> getIkrDefinitionIds(int ikrStaticDomainId) throws PersistenceException {
		return monitorDAO.getIkrDefinitionIds(ikrStaticDomainId);
	}
	
	public Map<Long, AbstractIkrDefinition> getIkrDefinitions()
	throws PersistenceException {
		List<Long> ids = monitorDAO.getIkrDefinitionIds();		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}
	
	public List<Long> getIkrDefinitionIds() throws PersistenceException {
		return monitorDAO.getIkrDefinitionIds();
	}
	
	public Map<Long, AbstractIkrDefinition> getIkrDefinitions(long monitorId, int metricGroupId)
	throws PersistenceException {
		Map<Long, AbstractIkrDefinition> res = new HashMap<Long, AbstractIkrDefinition>();
		
		List<Long> ids = monitorDAO.getIkrDefinitionIds(monitorId);		
		Map<Long, AbstractIkrDefinition> defs = getIkrDefinitions(ids);
		
		for(AbstractIkrDefinition ikrDef : defs.values()) {
			if (metricGroupId == ikrDef.getIkrCategoryId())
				res.put(ikrDef.getId(), ikrDef);
		}
		
		return res;
	}
	
	public Map<Long,AbstractIkrDefinition> getIkrDefinitions(int logicalEnvId, String context, int metricGroupId)
	throws PersistenceException {
		List<Long> ids = null;
		if (context.equals(CrossComputeDefinition.CROSS_COMPUTE_CONTEXT)) {
			ids = monitorDAO.getCrossComputeDefinitionIds(logicalEnvId, metricGroupId);
		} 
		else if (context.equals(StaticData.STATIC_DATA_CONTEXT)) {
			ids = monitorDAO.getStaticDataDefinitionIds(logicalEnvId, metricGroupId);
		}
		else {
			ids = monitorDAO.getIkrDefinitionIds(logicalEnvId, context, metricGroupId); 
		}
		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}
	
	public Map<Long, AbstractIkrDefinition> getCrossComputeDefinitions(int logicalEnvId) 
	throws PersistenceException {
		List<Long> ids = monitorDAO.getCrossComputeDefinitionIds(logicalEnvId);
		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}	
	
	public void deleteCrossComputeDefinitions(int logicalEnvId) throws PersistenceException {
		Collection<Long> ids = monitorDAO.getCrossComputeDefinitionIds(logicalEnvId);
		monitorDAO.deleteIkrDefinitions(logicalEnvId, true);
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");	
		
		for (long id: ids) {
			ikrDefinitionCache.remove(id);
		}
	}
	
	public Map<Long, AbstractIkrDefinition> loadStaticDataDefinitions() 
	throws PersistenceException {
		List<Long> ids = monitorDAO.loadStaticDataDefinitionIds();		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}	
	
	public Map<Long, AbstractIkrDefinition> getStaticDataDefinitions(int logicalEnvId) 
	throws PersistenceException {
		List<Long> ids = monitorDAO.getStaticDataDefinitionIds(logicalEnvId);		
		Map<Long, AbstractIkrDefinition> res = getIkrDefinitions(ids);
		return res;
	}	
	
	public Map<Long, AbstractIkrDefinition> getIkrDefinitions(Collection<Long> ikrDefinitionIds) {
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");	
		Map<Long, AbstractIkrDefinition> ikrDefinitions = ikrDefinitionCache.getAllWithLoader(ikrDefinitionIds, null);
		return ikrDefinitions;
	}	
	
	
	public AbstractIkrDefinition getIkrDefinition(long ikrDefinitionId) {
		Element element = null;
		try {
			Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");				
			element = ikrDefinitionCache.getWithLoader(ikrDefinitionId, null, null);
		} catch (Exception exc) {
			logger.error(exc);
		}
		
		AbstractIkrDefinition ikrDefinition = null;
		if (element!=null)
			ikrDefinition = (AbstractIkrDefinition)element.getValue();
	
		return ikrDefinition;
	}	
	

	public AbstractIkrDefinition getIkrDefinition(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException {
		long id = monitorDAO.getIkrDefinition(monitorId, metricGroupId, instance, compute);
		return getIkrDefinition(id);
	}	
	
	public long getIkrDefinitionId(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException {
		return monitorDAO.getIkrDefinition(monitorId, metricGroupId, instance, compute);
	}
	
	
	public void updateIkrDefinitions(Collection<IkrDefinition> ikrDefinitions) 
	throws PersistenceException {
		monitorDAO.updateIkrDefinitions(ikrDefinitions);
		
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
		for (IkrDefinition ikrDefinition: ikrDefinitions) {
			ikrDefinitionCache.remove(ikrDefinition.getId());
		}
	}
	
	public void deleteIkrDefinitions(long monitorId) 
	throws PersistenceException {
		List<Long> ids = monitorDAO.getIkrDefinitionIds(monitorId);
		if (ids!=null && ids.size()>0) {
			monitorDAO.deleteIkrDefinitions(ids);
			
			Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
			for (long id: ids) {
				ikrDefinitionCache.remove(id);
			}
		}
	}
	
	public void deleteIkrDefinition(long ikrDefinitionId) 
	throws PersistenceException {
		monitorDAO.deleteIkrDefinition(ikrDefinitionId);
		
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
		ikrDefinitionCache.remove(ikrDefinitionId);
	}

	public long createIkrDefinition(IkrDefinition ikrDefinition) 
	throws PersistenceException {
		long id = monitorDAO.createIkrDefinition(ikrDefinition);
//		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
//		ikrDefinition.setId(id);
//		ikrDefinitionCache.put(new Element(id, ikrDefinition));
		return id;
	}
	
	public void saveIkrValues(Collection<IkrValue> ikrValues, boolean archive) 
	throws Exception {
		// try to delete values that do not satisfy the threshold persistence requirement.
		
		if (ikrValues == null) {
			logger.error("Received null values in saveIkrValues");
			return;
		}
		
		if (ikrValues.isEmpty()) {
			return;
		}
		
		// Get a range of free ids;
		IdGenerator ikrValueGenerator = (IdGenerator)AbstractApplicationContext.getBean(PersistencyBeanName.ikrValueIdGenerator);
		if (archive)
			ikrValueGenerator = (IdGenerator)AbstractApplicationContext.getBean("ikrValueArchiveIdGenerator");
		long nextId = ikrValueGenerator.getNextId(ikrValues.size());
			
		monitorDAO.saveIkrValues(ikrValues, nextId, archive);
	}
	
	public IkrValue getIkrValue(long ikrValueId) 
	throws PersistenceException {		
		IkrValue ikrValue = monitorDAO.getIkrValue(ikrValueId);
		return ikrValue;
	}
	
	public Collection<Long> getLastIkrDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = monitorDAO.getLastIkrDefinitionIds(nbIds);
		return res;
	}

	public Collection<Long> getLastIkrValueIds(int nbIds)
	throws PersistenceException {		
		Collection<Long> res = monitorDAO.getLastIkrValueIds(nbIds);		
		return res;
	}	
	
	public void cleanIkrValues(Date beforeDate) throws Exception {
		monitorDAO.cleanIkrValues(beforeDate);
	}

	public long createCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition)
	throws PersistenceException {
		long res = monitorDAO.createCrossComputeDefinition(crossComputeDefinition);
		
		Collection<Long> linkedDefinitionIds = crossComputeDefinition.parse();
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
		
		for (Long linkedDefId : linkedDefinitionIds) {
			ikrDefinitionCache.remove(linkedDefId);
		}
		
		return res;
	}
	
	public void updateCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition)
	throws PersistenceException {
		monitorDAO.updateCrossComputeDefinition(crossComputeDefinition);
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
		ikrDefinitionCache.remove(crossComputeDefinition.getId());

		Collection<Long> linkedDefinitionIds = crossComputeDefinition.parse();		
		for (Long linkedDefId : linkedDefinitionIds) {
			ikrDefinitionCache.remove(linkedDefId);
		}		
	}	
	
	public long createStaticDataDefinition(StaticData staticData)
	throws PersistenceException {
		long res = monitorDAO.createStaticDataDefinition(staticData);		
//		staticData.setId(res);
//		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");		
//		ikrDefinitionCache.put(new Element(res, staticData));
		return res;
	}
	
	public void updateStaticDataDefinition(StaticData staticData)
	throws PersistenceException {
		monitorDAO.updateStaticDataDefinition(staticData);
		Ehcache ikrDefinitionCache = cacheManager.getEhcache("IkrDefinitionCache");
		ikrDefinitionCache.remove(staticData.getId());
	}
}
