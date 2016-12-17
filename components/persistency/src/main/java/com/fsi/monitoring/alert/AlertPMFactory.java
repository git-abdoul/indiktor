package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.dao.AlertDAO;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.cache.CacheName;
import com.fsi.monitoring.global.IdGenerator;
import com.fsi.monitoring.query.QueryName;
import com.fsi.monitoring.snmp.SnmpConfig;

class AlertPMFactory 
implements AlertPM, Serializable{ 

	private static final long serialVersionUID = 2142001342996327138L;

	private static final Logger logger = Logger.getLogger(AlertPMFactory.class);	
	
	private AlertDAO alertDAO;	
	private CacheManager cacheManager;
	private IdGenerator alertDefIdGenerator;
	private IdGenerator alertEventIdGenerator;
	
	public void setAlertDAO(AlertDAO alertDAO) {
		this.alertDAO = alertDAO;
	}
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	public void setAlertDefIdGenerator (IdGenerator alertDefIdGenerator) {
		this.alertDefIdGenerator = alertDefIdGenerator;
	}

	public void setAlertEventIdGenerator(IdGenerator alertEventIdGenerator) {
		this.alertEventIdGenerator = alertEventIdGenerator;
	}

	public Map<Long, AlertDefinition> getAlertDefinitionsByIkrDefinitionIds(Collection<Long> ikrDefinitionIds) {		
		Collection<Long> alertDefinitionIds = new HashSet<Long>();
		
//		Ehcache queryCache = cacheManager.getEhcache("QueryCache");
//		
//		for (Long ikrDefinitionId : ikrDefinitionIds) {
//			String queryId = QueryName.createAlertDefinitionsByIkrDefinitionId(ikrDefinitionId);
//			Element element = queryCache.getWithLoader(queryId, null, null);
//			Collection<Long> tmpAlertDefinitionIds = (Collection<Long>)element.getObjectValue();
//			if (tmpAlertDefinitionIds != null && !tmpAlertDefinitionIds.isEmpty()) {
//				alertDefinitionIds.addAll(tmpAlertDefinitionIds);
//			}
//		}
		
		for (Long ikrDefinitionId : ikrDefinitionIds) {
			Collection<Long> tmpAlertDefinitionIds;
			try {
				tmpAlertDefinitionIds = alertDAO.getAlertDefinitions(ikrDefinitionId);
				if (tmpAlertDefinitionIds != null && !tmpAlertDefinitionIds.isEmpty()) {
					alertDefinitionIds.addAll(tmpAlertDefinitionIds);
				}
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}			
		}
		
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());		
		Map<Long, AlertDefinition> alertDefinitions = alertDefinitionCache.getAllWithLoader(alertDefinitionIds, null);
		
		return alertDefinitions;
	}
	
	public Collection<Long> getLastAlertDefinitionIds(int nbIds) throws PersistenceException {
		Collection<Long> res = alertDAO.getLastAlertDefinitionIds(nbIds);
		return res;
	}
	
//	public Collection<Long> getAlertDefinitionIdsByLabel(String label) {
//		Collection<Long> res = null;		
//		try {
//			res  = alertDAO.getAlertDefinitionIdsByLabel(label);		
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}		
//		return res;
//	}
	
	public Collection<Long> getAlertDefinitionIdsByLabelAndEnv(String label, String env) {
		Collection<Long> res = null;		
		try {
			res  = alertDAO.getAlertDefinitionIdsByLabelAndEnv(label, env);		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
		return res;
	}
	
	public Map<Long, AlertDefinition> getAlertDefinitions() {
//		
//		Ehcache queryCache = cacheManager.getEhcache("QueryCache");
//
//		String queryId = QueryName.createAlertDefinitionsALLQueryId();
//		Element element = queryCache.getWithLoader(queryId, null, null);
//		
//		Collection<Long> alertDefinitionIds = (Collection<Long>)element.getObjectValue();	
		
		Collection<Long> alertDefinitionIds = null;
		try {
			alertDefinitionIds = alertDAO.loadAlertDefinitionIds();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());		
		Map<Long, AlertDefinition> alertDefinitions = alertDefinitionCache.getAllWithLoader(alertDefinitionIds, null);
		return alertDefinitions;
	}
	
	public Map<Long, AlertDefinition> getAlertDefinitionsByIds(Collection<Long> alertDefinitionIds) {
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());		
		Map<Long, AlertDefinition> alertDefinitions = alertDefinitionCache.getAllWithLoader(alertDefinitionIds, null);
		
		return alertDefinitions;
	}	
	
	public AlertDefinition getAlertDefinitionById(long id) {
		if (id == -1) {
			return null;
		}
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());		
		Element element = alertDefinitionCache.getWithLoader(id, null, null);
		return (AlertDefinition)element.getValue();
	}
	
	public void deleteAlertDefinitionsForEnv(int logicalEnvId) throws PersistenceException {
		Map<Long, AlertDefinition> tmp = getAlertDefinitions();
		for(AlertDefinition def : tmp.values()) {
			if (def.getLogicalEnv() == logicalEnvId) {
				deleteAlertDefinition(def.getId());
			}
		}		
	}
	
	public boolean deleteAlertDefinition(long alertDefinitionId) throws PersistenceException {		
		alertDAO.deleteAlertDefinition(alertDefinitionId);		
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());
		return alertDefinitionCache.remove(alertDefinitionId);	
	}
	
	public void deleteAlertDefinitions(Collection<Long> alertDefIds) throws PersistenceException {	
//		alertDAO.deleteAlertDefinitions(alertDefIds);
//		
//		for (long alertDefinitionId : alertDefIds) {		
//			Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());
//			AlertDefinition alertDefinition = getAlertDefinitionById(alertDefinitionId);
//			
//			Ehcache queryCache = cacheManager.getEhcache("QueryCache");
//			queryCache.remove(QueryName.createAlertDefinitionsALLQueryId());
//			if (alertDefinition != null) {
//				for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
//					long ikrDefinitionId = alertCondition.getIkrDefinitionId();
//					String queryId = QueryName.createAlertDefinitionsByIkrDefinitionId(ikrDefinitionId);
//					queryCache.remove(queryId);
//				}			
//				 alertDefinitionCache.remove(alertDefinitionId);
//			}
//		}
		
		alertDAO.deleteAlertDefinitions(alertDefIds);	
		Ehcache alertDefinitionCache = cacheManager.getEhcache(CacheName.AlertDefinitionCache.name());		
		for (long alertDefinitionId : alertDefIds) {		
			 alertDefinitionCache.remove(alertDefinitionId);
		}
	}

	public void createAlertDefinition(AlertDefinition alertDefinition) throws PersistenceException {		
		alertDAO.createAlertDefinition(alertDefinition, alertDefIdGenerator.getNextId(1));		
//		try {
//			alertDAO.createAlertDefinition(alertDefinition, alertDefIdGenerator.getNextId(1));
//			
//			Ehcache queryCache = cacheManager.getEhcache("QueryCache");
//			queryCache.remove(QueryName.createAlertDefinitionsALLQueryId());
//			
//			for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
//				long ikrDefinitionId = alertCondition.getIkrDefinitionId();
//				queryCache.remove(QueryName.createAlertDefinitionsByIkrDefinitionId(ikrDefinitionId));	
//			}
//			
//			String queryId = QueryName.createAlertDefinitionsALLQueryId();
//			queryCache.remove(queryId);	
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
	}
	
	public Alert getAlert(long alertDefinitionId) {
		Alert res = null;
		Ehcache activeAlertCache = cacheManager.getEhcache(CacheName.AlertCache.name());		
		Element element = activeAlertCache.get(alertDefinitionId);
		if (element != null) {
			res = (Alert)element.getObjectValue();
		}
		return res;
	}	
	
	public Map<Long, Alert> getAlerts() {
		Ehcache activeAlertCache = cacheManager.getEhcache(CacheName.AlertCache.name());		
		List<Long> alertDefinitionIds = activeAlertCache.getKeys();
		Map<Long, Alert> alerts = activeAlertCache.getAllWithLoader(alertDefinitionIds, null);
		return alerts;
	}
	
	public long addAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException {
		return updateAlertEvent(alert, alertEvent, false);
	}
	
	public long removeAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException {
		return updateAlertEvent(alert, alertEvent, true);
	}
	
	public long modifyAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException {
		long nextId = 0;
		long alertDefinitionId = alert.getValueDefinitionId();
		if (alertEvent != null) {	
			alertDAO.updateAlertEvent(alertEvent);	
		}
		
		Ehcache activeAlertCache = cacheManager.getEhcache(CacheName.AlertCache.name());
		Element element = new Element(alertDefinitionId, alert);
		activeAlertCache.put(element);
		
		return nextId;
	}
	

	private long updateAlertEvent(Alert alert, AlertEvent alertEvent, boolean onDeletion) throws PersistenceException {	
		long nextId = 0;
		long alertDefinitionId = alert.getValueDefinitionId();
		if (alertEvent != null) {	
			if (!onDeletion) {
				nextId = alertEventIdGenerator.getNextId(1);
				alertEvent.setId(nextId);
				alertDAO.createAlertEvent(alertDefinitionId, alertEvent);	
			}
			else {
				alertDAO.deleteAlertEvent(alertEvent);	
			}
		}
		
		Ehcache activeAlertCache = cacheManager.getEhcache(CacheName.AlertCache.name());
		Element element = new Element(alertDefinitionId, alert);
		activeAlertCache.put(element);
		
		return nextId;
	}
	
	public List<AlertEvent> getAlertEvents(long alertDefinitionId, String eventType) throws PersistenceException {
		return alertDAO.getAlertEvents(alertDefinitionId, eventType);
	}

	public void updateAlertDefinition(AlertDefinition alert) throws PersistenceException {
		alertDAO.updateAlertDefinition(alert);
		Ehcache alertCache = cacheManager.getCache(CacheName.AlertDefinitionCache.name());
		alertCache.remove(alert.getId());
			
//		try {
//			alertDAO.updateAlertDefinition(alert);
//			Ehcache alertCache = cacheManager.getCache(CacheName.AlertDefinitionCache.name());
//			alertCache.remove(alert.getId());
//			
//			Ehcache queryCache = cacheManager.getEhcache("QueryCache");
//			
//			for (AlertCondition alertCondition : alert.getAlertConditions()) {
//				long ikrDefinitionId = alertCondition.getIkrDefinitionId();
//				queryCache.remove(QueryName.createAlertDefinitionsByIkrDefinitionId(ikrDefinitionId));	
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
	}

	public Map<String, Integer> getAlertLevels() throws PersistenceException {
		return alertDAO.getAlertLevels();
	}

	public List<String> getAlertEnvs() throws PersistenceException {
		return alertDAO.getAlertEnvs();
	}
	
	public Map<Integer, AlertGroup> getAllAlertGroups() throws PersistenceException {
		return alertDAO.getAllAlertGroups();
	}
	
	public Map<Integer, String> getExistingAlertGroups(int logicalEnvId) throws PersistenceException {
		return alertDAO.getExistingAlertGroups(logicalEnvId);
	}	
	
	public Map<Integer, String> getExistingAlertDomains(int logicalEnvId, int group) throws PersistenceException {
		return alertDAO.getExistingAlertDomains(logicalEnvId, group);
	}		
	
	public Map<Integer, String> getExistingAlertSubDomains(int logicalEnvId, int group, int domain) throws PersistenceException {
		return alertDAO.getExistingAlertSubDomains(logicalEnvId, group, domain);
	}		
	
	public Map<Integer, AlertDomain> getAllAlertDomains() throws PersistenceException {
		return alertDAO.getAllAlertDomains();
	}
	
	public Map<Integer, AlertSubDomain> getAllAlertSubDomains() throws PersistenceException {
		return alertDAO.getAllAlertSubDomains();
	}
	
	public Map<Integer, AlertDomain> getAllAlertDomainsByGroupId(int groupId) throws PersistenceException {
		return alertDAO.getAllAlertDomainsByGroupId(groupId);
	}
	
	public Collection<AlertSubDomain> getAllAlertSubDomainsByDomainId(int domainId) throws PersistenceException {
		return alertDAO.getAllAlertSubDomainsByDomainId(domainId);
	}

	public void createSnmpConfig(SnmpConfig config) throws PersistenceException {
		alertDAO.createSnmpConfig(config);		
	}

	public void deleteSnmpConfig(Collection<Long> ids)
			throws PersistenceException {
		alertDAO.deleteSnmpConfig(ids);		
	}

	public void deleteSnmpConfig(long id) throws PersistenceException {
		alertDAO.deleteSnmpConfig(id);
	}

	public Collection<SnmpConfig> getSnmpConfigs() throws PersistenceException {
		return alertDAO.getSnmpConfigs();
	}

	public Collection<SnmpConfig> getSnmpConfigs(Collection<Long> ids)
			throws PersistenceException {
		return alertDAO.getSnmpConfigs(ids);
	}

	public void updateSnmpConfig(SnmpConfig config) throws PersistenceException {
		alertDAO.updateSnmpConfig(config);
	}
	
	
}
