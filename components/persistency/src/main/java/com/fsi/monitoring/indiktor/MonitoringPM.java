package com.fsi.monitoring.indiktor;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;

public interface MonitoringPM {	
	// ------- IKR DEFINITION  -------
	AbstractIkrDefinition getIkrDefinition(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException;
	long getIkrDefinitionId(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException;
	AbstractIkrDefinition getIkrDefinition(long ikrDefinitionId) throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getIkrDefinitions(Collection<Long> ikrDefinitionIds) throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getIkrDefinitions(long monitorId) throws PersistenceException;
	List<Long> getIkrDefinitionIds(long monitorId) throws PersistenceException;
	List<Long> getIkrDefinitionIds(int ikrStaticDomainId) throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getIkrDefinitions() throws PersistenceException;
	List<Long> getIkrDefinitionIds() throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getIkrDefinitions(long monitorId, int metricGroupId) throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getIkrDefinitions(int logicalEnvId, String context, int metricGroupId) throws PersistenceException;
	Collection<Long> getLastIkrDefinitionIds(int nbIds) throws PersistenceException;
	long createIkrDefinition(IkrDefinition ikrDefinition) throws PersistenceException;	
	void updateIkrDefinitions(Collection<IkrDefinition> ikrDefinition) throws PersistenceException;	
	void deleteIkrDefinitions(long monitorId) throws PersistenceException;
	void deleteIkrDefinition(long id) throws PersistenceException;
	
	// ------ CROSS COMPUTE ------
	Map<Long, AbstractIkrDefinition> getCrossComputeDefinitions(int logicalEnvId) throws PersistenceException;
	long createCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition) throws PersistenceException;
	void updateCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition) throws PersistenceException;
	void deleteCrossComputeDefinitions(int logicalEnvId) throws PersistenceException;
	
	// ------ STATIC DATA ------
	Map<Long, AbstractIkrDefinition> loadStaticDataDefinitions() throws PersistenceException;
	Map<Long, AbstractIkrDefinition> getStaticDataDefinitions(int logicalEnvId) throws PersistenceException;
	long createStaticDataDefinition(StaticData staticData) throws PersistenceException;
	void updateStaticDataDefinition(StaticData staticData) throws PersistenceException;
	
	// ------ IKR VALUE ------
	void saveIkrValues(Collection<IkrValue> ikrValues, boolean archive) throws Exception;
	IkrValue getIkrValue(long ikrValueId) throws PersistenceException;
	Collection<Long> getLastIkrValueIds(int nbIds) throws PersistenceException;
	void cleanIkrValues(Date beforeDate) throws Exception;
}
