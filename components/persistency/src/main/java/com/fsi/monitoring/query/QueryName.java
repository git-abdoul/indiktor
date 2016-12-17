package com.fsi.monitoring.query;

import com.fsi.monitoring.kpi.metrics.IkrCategory;

public class QueryName {

	public enum queries {
		Monitors,
		MonitorsByEnv,
		IkrCategories,
		IkrCategoriesByGroup,
		IkrDefinitions,
		IkrDefinitionsByIkrIntance,
		IkrDefinitionsALL,
		IkrDefinitionSelector,
		AlertDefinitionsByIkrDefinitionId,
		AlertDefinitionIdsALL
	}
	
	public static String createIkrDefinitionsQueryId(long monitorId,
													 String ikrCategoryGroup,
	   												 String ikrInstance,
	   												 String ikrEnv) {
		
		String queryId = queries.IkrDefinitionsByIkrIntance + ";" 
			+ monitorId + ";" 
			+ ikrCategoryGroup + ";" 
			+ ikrInstance.replaceAll(";", "") + ";"
			+ ikrEnv.replaceAll(";", "");
		return queryId;
	}
	
//	public static String createIkrDefinitionsQueryIdForSelector(IkrCategory ikrCategory,
//																String ikrInstance,
//																String ikrEnv) {
//		String queryId = queries.IkrDefinitionSelector + ";" 
//		+ ikrCategory.getName() + ";" 
//		+ ikrInstance.replaceAll(";", "") + ";"
//		+ ikrEnv.replaceAll(";", "");
//		return queryId;
//	}	

	public static String createIkrDefinitionsQueryId(long monitorId, String ikrCategoryGroup) {
		String queryId = queries.IkrDefinitions + ";" 
			+ monitorId + ";" 
			+ ikrCategoryGroup;
		return queryId;
	}		
	
	public static String createIkrDefinitionsALLQueryId() {
		return queries.IkrDefinitionsALL.name();
	}
	
	public static String createAlertDefinitionsByIkrDefinitionId(long ikrDefinitionId) {
		return queries.AlertDefinitionsByIkrDefinitionId.name() + ";" + ikrDefinitionId;
	}

	public static String createAlertDefinitionsALLQueryId() {
		return queries.AlertDefinitionIdsALL.name();
	}
	
//	public static String createMonitorQuieryId(long agentId) {
//		return queries.Monitors.name() + ";" + agentId;
//	}
//	
	public static String createMonitorByLogicalEnvQuieryId(int logicalEnvId) {
		return queries.MonitorsByEnv.name() + ";" + logicalEnvId;
	}	
}
