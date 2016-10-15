package com.fsi.monitoring.alert.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertDomain;
import com.fsi.monitoring.alert.AlertGroup;
import com.fsi.monitoring.alert.AlertSubDomain;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.snmp.SnmpConfig;

public interface AlertDAO {
	AlertDefinition getAlertDefinition(long alertDefinitionId) throws PersistenceException;	
	Map<Long,AlertDefinition> getAlertDefinitions(Collection<Long> ids)throws PersistenceException;	
	Collection<Long> loadAlertDefinitionIds() throws PersistenceException;
	Collection<Long> getAlertDefinitions(long ikrDefinitionId) throws PersistenceException;
	Collection<Long> getLastAlertDefinitionIds(int nbIds) throws PersistenceException;
	Collection<Long> getAlertDefinitionIdsByLabel(String label)	throws PersistenceException;	
	Collection<Long> getAlertDefinitionIdsByLabelAndEnv(String label, String env)throws PersistenceException;
	void createAlertDefinition(AlertDefinition alertDef, long id) throws PersistenceException;	
	void deleteAlertDefinitions(Collection<Long> alertDefIds) throws PersistenceException;	
	void deleteAlertDefinition(long alertId)throws PersistenceException;	
	void updateAlertDefinition(AlertDefinition alertDef) throws PersistenceException;
	
	void createAlertEvent(long alertDefinitionId, AlertEvent event) throws PersistenceException;
	void updateAlertEvent(AlertEvent event) throws PersistenceException;
	void deleteAlertEvent(AlertEvent event) throws PersistenceException;
	List<AlertEvent> getAlertEvents(long alertDefinitionId, String eventType) throws PersistenceException;
	
	Map<String, Integer> getAlertLevels() throws PersistenceException;
	
	List<String> getAlertEnvs() throws PersistenceException;
	
	Map<Integer, String> getExistingAlertGroups(int logicalEnvId) throws PersistenceException;
	Map<Integer, String> getExistingAlertDomains(int logicalEnvId, int group) throws PersistenceException;
	Map<Integer, String> getExistingAlertSubDomains(int logicalEnvId, int group, int domain) throws PersistenceException;	
	
	Map<Integer, AlertGroup> getAllAlertGroups() throws PersistenceException;
	Map<Integer, AlertDomain> getAllAlertDomains() throws PersistenceException;
	Map<Integer, AlertSubDomain> getAllAlertSubDomains() throws PersistenceException;
	Map<Integer, AlertDomain> getAllAlertDomainsByGroupId(int groupId) throws PersistenceException;
	Collection<AlertSubDomain> getAllAlertSubDomainsByDomainId(int domainId) throws PersistenceException;	
	
	void createSnmpConfig(SnmpConfig config) throws PersistenceException;
	void deleteSnmpConfig(long id) throws PersistenceException;
	void deleteSnmpConfig(Collection<Long>  ids) throws PersistenceException;
	void updateSnmpConfig(SnmpConfig config) throws PersistenceException;
	Collection<SnmpConfig> getSnmpConfigs(Collection<Long>  ids) throws PersistenceException;
	Collection<SnmpConfig> getSnmpConfigs() throws PersistenceException;
}
