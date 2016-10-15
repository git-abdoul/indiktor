package com.fsi.monitoring.alert;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.snmp.SnmpConfig;

public interface AlertPM {
	
	String ALERT_PM_NAME = "alertPM";

	// ALERT CONFIGURATION
	Map<Long, AlertDefinition> getAlertDefinitions();
	Map<Long, AlertDefinition> getAlertDefinitionsByIds(Collection<Long> alertDefinitionIds);
	Map<Long, AlertDefinition> getAlertDefinitionsByIkrDefinitionIds(Collection<Long> ikrDefinitionIds);
	Collection<Long> getLastAlertDefinitionIds(int nbIds) throws PersistenceException;
//	Collection<Long> getAlertDefinitionIdsByLabel(String label);
	Collection<Long> getAlertDefinitionIdsByLabelAndEnv(String label, String env);
	AlertDefinition getAlertDefinitionById(long id);
	
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
	
	void deleteAlertDefinitionsForEnv(int logicalEnvId) throws PersistenceException;
	void deleteAlertDefinitions(Collection<Long> alertDefIds) throws PersistenceException;
	boolean deleteAlertDefinition(long alertDefinitionId) throws PersistenceException;
	void createAlertDefinition(AlertDefinition alertDefinition) throws PersistenceException;
	void updateAlertDefinition(AlertDefinition alertDefinition)throws PersistenceException;
	
	void createSnmpConfig(SnmpConfig config) throws PersistenceException;
	void deleteSnmpConfig(long id) throws PersistenceException;
	void deleteSnmpConfig(Collection<Long>  ids) throws PersistenceException;
	void updateSnmpConfig(SnmpConfig config) throws PersistenceException;
	Collection<SnmpConfig> getSnmpConfigs(Collection<Long>  ids) throws PersistenceException;
	Collection<SnmpConfig> getSnmpConfigs() throws PersistenceException;
	
	// ALERT EVENTS	
	Map<Long, Alert> getAlerts() throws PersistenceException;
	Alert getAlert(long alertDefinitionId) throws PersistenceException;
	List<AlertEvent> getAlertEvents(long alertDefinitionId, String eventType) throws PersistenceException;
//	void createAlertEvent(long alertDefinitionId, List<AlertEvent> alertEvents) throws PersistenceException;
//	long updateAlertEvent(Alert alert, AlertEvent alertEvent, boolean onDeletion) throws PersistenceException;
	long addAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException;
	long removeAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException;
	long modifyAlertEvent(Alert alert, AlertEvent alertEvent) throws PersistenceException;
}
