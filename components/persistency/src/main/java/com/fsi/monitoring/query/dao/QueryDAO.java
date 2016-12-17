package com.fsi.monitoring.query.dao;

import java.util.Collection;

import com.fsi.fwk.exception.persistence.PersistenceException;

public interface QueryDAO {
	
	Collection<Long> getMonitorIdsByLogicalEnv(int logicalEnvId)
	throws PersistenceException;	
	
	Collection<Integer> getIkrCategoryIdsByGroup(String ikrCategoryGroup)
	throws PersistenceException;
	
	Collection<Integer> getIkrCategoryIds()
	throws PersistenceException;	
	
	Collection<Long> getIkrDefinitionIdsByIkrInstance(long monitorId, 
													  String ikrCategoryGroup,
		   		 									  String ikrInstance,
		   		 									  String ikrEnv) 
	throws PersistenceException;
	
	Collection<Long> getIkrDefinitionIds(long monitorId, String ikrCategoryGroup) 
	throws PersistenceException;
	
	Collection<Long> getIkrDefinitionIds(String ikrCategory, 
			 					 		 String ikrInstance,
			 					 		 String ikrEnv) 
	throws PersistenceException;	
	
	Collection<Long> getIkrDefinitionIds() 
	throws PersistenceException;
	
	Collection<Long> getAlertDefinitions(long ikrDefinitionId)
	throws PersistenceException;
	
	Collection<Long> getAlertDefinitionIds()
	throws PersistenceException;
}
