package com.fsi.monitoring.indiktor.dao;

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

public interface MonitorDAO {
	
//	public IkrDefinition getIkrDefinition(long id) 
//	throws PersistenceException;
	
	// ------ IKR DEFINITION ------	
	Map<Long,AbstractIkrDefinition> getIkrDefinitions(Collection<Long> ids) throws PersistenceException;
	AbstractIkrDefinition getIkrDefinition(long ikrDefinitionId) throws PersistenceException;
	List<Long> getIkrDefinitionIds(long monitorId) throws PersistenceException;	
	List<Long> getIkrDefinitionIds(int ikrStaticDomainId) throws PersistenceException;
	List<Long> getIkrDefinitionIds() throws PersistenceException;
	List<Long> getIkrDefinitionIds(int logicalEnvId, String context, int metricGroupId) throws PersistenceException;
	public Collection<Long> getLastIkrDefinitionIds(int nbIds) throws PersistenceException;	
	long getIkrDefinition(long monitorId,int metricGroupId, String instance, MetricCompute compute) throws PersistenceException;
	long createIkrDefinition(IkrDefinition ikrDefinition) throws PersistenceException;	
	void updateIkrDefinitions(Collection<IkrDefinition> ikrDefinitions)  throws PersistenceException;
	void deleteIkrDefinitions(Collection<Long> ids) throws PersistenceException;
	public void deleteIkrDefinitions(int logicalEnvId, boolean isComputed) throws PersistenceException;
	void deleteIkrDefinition(long ikrDefinitionId) throws PersistenceException;
	
	// ------ CROSS COMPUTE ------
//	List<CrossComputeDefinition> getCrossComputeDefinitions() throws PersistenceException;
	List<Long> getCrossComputeDefinitionIds(int logicalEnvId, int metricGroupId) throws PersistenceException;
	List<Long> getCrossComputeDefinitionIds(int logicalEnvId) throws PersistenceException;
	long createCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition) throws PersistenceException;
	void updateCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition) throws PersistenceException;
	
	// ------ STATIC DATA ------
	List<Long> loadStaticDataDefinitionIds() throws PersistenceException;
	List<Long> getStaticDataDefinitionIds(int logicalEnvId, int metricGroupId) throws PersistenceException;
	List<Long> getStaticDataDefinitionIds(int logicalEnvId) throws PersistenceException;
	long createStaticDataDefinition(StaticData staticData) throws PersistenceException;
	void updateStaticDataDefinition(StaticData staticData) throws PersistenceException;
	

	
	public IkrValue getIkrValue(long id) 
	throws PersistenceException;
	
	public Collection<Long> getLastIkrValueIds(int nbIds) 
	throws PersistenceException;		
	
	void saveIkrValues(Collection<IkrValue> values, long nextId, boolean archive) 
	throws PersistenceException;	
	
	void cleanIkrValues(Date beforeDate) throws Exception;

	

}