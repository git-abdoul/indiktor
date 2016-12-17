package com.fsi.monitoring.indiktor.dao;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.IkrVersion;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

public interface DataModelDAO {
	
	/* IKR VERSION */
	IkrVersion getIkrVersion() throws PersistenceException;
	
	/* STATIC DOMAIN*/
	void createIkrStaticDomain(IkrStaticDomain staticDomain, int nextId) throws PersistenceException;
	void updateIkrStaticDomain(IkrStaticDomain staticDomain) throws PersistenceException;
	void deleteIkrStaticDomain(int id, int level) throws PersistenceException;
	void cleanIkrStaticDomains() throws PersistenceException;
	List<Integer> getIkrStaticDomain(int id, int level) throws PersistenceException;
	List<IkrStaticDomain> getIkrStaticDomains() throws PersistenceException;
	List<Integer> loadIkrStaticDomains() throws PersistenceException;
	List<Integer> getIkrStaticDomainIds(int parentId)throws PersistenceException;
	IkrStaticDomain getIkrStaticDomain(int ikrStaticDomainId) throws PersistenceException;
	Map<Integer, IkrStaticDomain>  getIkrStaticDomains(Collection<Integer> ikrStaticDomainIds) throws PersistenceException;
	int getIkrStaticDomainIdByValue(String value) throws PersistenceException;
	
	/* JOB SCHEDULER STATIC DOMAIN*/
	IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(int jobSchedulerStaticDomainId) throws PersistenceException;
	List<IkrJobSchedulerStaticDomain> getJobSchedulerStaticDomains() throws PersistenceException;
	IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(String jobSchedulerStaticDomainName) throws PersistenceException;
	void createJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void updateJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void deleteJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void deleteJobSchedulerStaticDomains() throws PersistenceException;	
	
	/* METRIC DOMAIN*/
//	Map<String, List<String>> getMetricDomainItems() throws PersistenceException;
//	List<String> getMetricDomainItems(int metricDomainConfigId) throws PersistenceException;
//	void addMetricDomainItems(int metricDomainConfigId, List<String> items) throws PersistenceException;
//	void cleanMetricDomainItems() throws PersistenceException;
	
	/* METRIC DOMAIN RESOURCE*/
	List<MetricDomainResource> getMetricDomainResources() throws PersistenceException;
	List<MetricDomainResource> getMetricDomainResources(int ikrStaticDomainId) throws PersistenceException;
	MetricDomainResource getMetricDomainResource(int id) throws PersistenceException;
	MetricDomainResource getMetricDomainResource(int metricDomainId, String resourceName) throws PersistenceException;
	void addMetricDomainResource(MetricDomainResource resource, int nextId) throws PersistenceException;
	void updateMetricDomainResource(MetricDomainResource resource) throws PersistenceException;
	void removeMetricDomainResource(int resourceId) throws PersistenceException;
	void cleanMetricDomainResource() throws PersistenceException;
	
	/* METRIC CATEGORY RESOURCE*/
	List<IkrCategoryResource> getIkrCategoryResources(List<Integer> categoryResourceIds) throws PersistenceException;
	IkrCategoryResource getIkrCategoryResource(int ikrCategoryId) throws PersistenceException;
	Map<String, IkrCategoryResource> getIkrCategoryResources(int metricDomainResourceId) throws PersistenceException;
	int getIkrCategoryResourceId(int ikrCategoryId, int metricDomainResourceId, String name) throws PersistenceException;
	Map<Integer, IkrCategoryResource> getIkrCategoryResourcesById(int metricDomainResourceId) throws PersistenceException;
	Map<Integer, IkrCategoryResource> loadIkrCategoryResources() throws PersistenceException;
	void saveIkrCategoryResource(IkrCategoryResource resource, int nextId) throws PersistenceException;
	void updateIkrCategoryResource(IkrCategoryResource resource) throws PersistenceException;
	void cleanIkrCategoryResource() throws PersistenceException;
	void deleteIkrCategoryResourceById(int resourceId) throws PersistenceException;
	void deleteIkrCategoryResourceByStaticDomainId(int ikrStaticDomainId) throws PersistenceException;
	
//	void addIkrService(IkrService service) throws PersistenceException;
//	void updateIkrService(IkrService service) throws PersistenceException;
//	void deleteIkrService(String serviceName) throws PersistenceException;
//	List<IkrService> getIkrServices() throws PersistenceException;
//	IkrService getIkrService(String serviceName) throws PersistenceException;
//	void resetIkrService() throws PersistenceException;
	
	/* METRIC DOMAIN CONFIG FIELD*/
//	void addMetricDomainConfigField(MetricDomainConfigField field) throws PersistenceException;
//	void updateMetricDomainConfigField(MetricDomainConfigField field) throws PersistenceException;
//	void deleteMetricDomainConfigField(int id) throws PersistenceException;
//	void cleanMetricDomainConfigField() throws PersistenceException;
//	Map<String, MetricDomainConfigField> getMetricDomainConfigFields(int metricDomainConfigId) throws PersistenceException;
	
//	void createIkrCategory(IkrCategory category) throws PersistenceException;	
	
	
//	void updateIkrCategory(IkrCategory category) throws PersistenceException;
//	void deleteIkrCategory(int id) throws PersistenceException;
//	void deleteIkrCategories(String ikrCategoryGroup) throws PersistenceException;
//	void resetIkrCategory() throws PersistenceException;
//	void resetActivatedIkrCategory() throws PersistenceException;
	
	/* MONITOR*/
	Collection<Long> loadMonitorConfigs(int logicalEnvId) throws PersistenceException;
	Map<Long,MonitorConfig> loadMonitorConfigs(Collection<Long> monitorIds) throws PersistenceException;
	MonitorConfig loadMonitorConfig(long monitorId) throws PersistenceException;	
	
	void updateMonitor(MonitorConfig config) throws PersistenceException;
	long createMonitor(MonitorConfig config) throws PersistenceException;	
	void deleteMonitor(long monitorId) throws PersistenceException;	
	
	void createLogicalEnv(LogicalEnv env) throws PersistenceException;
	void updateLogicalEnv(LogicalEnv env) throws PersistenceException;
	void deleteLogicalEnv(int envId) throws PersistenceException;
	
	/* JOB SCHEULER */
	Map<Integer, IkrJobSchedulerConfig> loadJobSchedulerConfigs(int logicalEnvId) throws PersistenceException;
	Map<Integer, IkrJobSchedulerConfig> loadJobSchedulerConfigs(Collection<Integer> jobSchedulerIds) throws PersistenceException;
	IkrJobSchedulerConfig getJobSchedulerConfig(int jobSchedulerId) throws PersistenceException;
	
	int createJobScheduler(IkrJobSchedulerConfig config) throws PersistenceException;	
	void updateJobScheduler(IkrJobSchedulerConfig config) throws PersistenceException;
	void deleteJobScheduler(int jobSchedulerId) throws PersistenceException;	
	
	void addJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig) throws PersistenceException;
	void updateJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig) throws PersistenceException;
	void deleteJobSchedulerAttributeConfig(int id) throws PersistenceException;
	Map<String, IkrJobSchedulerAttributeConfig> getJobSchedulerAttributeConfigs(int jobSchedulerStaticDomainId) throws PersistenceException;	
	
	List<MetricDomainConfig> loadMetricDomainConfigs() throws PersistenceException;
	List<MetricDomainConfig> getMetricDomainConfigs(int metricDomainId) throws PersistenceException;
	MetricDomainConfig getMetricDomainConfig(int ikrStaticDomainId, String classname, String description) throws PersistenceException;
	MetricDomainConfig getMetricDomainConfig(int metricDomainConfigId) throws PersistenceException;
	void addMetricDomainConfig(MetricDomainConfig config, long nextId) throws PersistenceException;
	void updateMetricDomainConfig(MetricDomainConfig config) throws PersistenceException;	
	void removeMetricDomainConfig(long configId) throws PersistenceException;
	void cleanMetricDomainConfig() throws PersistenceException;
	
//	Map<Integer,Boolean> getIkrCategoriesActivation(long monitorId,String ikrCategoryGroup)
//	throws PersistenceException;
	
	Map<Integer,List<IkrCategoryResource>> getMonitorActivities(long monitorId) throws PersistenceException;
	
//	void createActivatedCategory(long monitorId,String ikrCategoryGroup)
//	throws PersistenceException;
	
	void updateIkrDefinitionsActivation(Map<Long, Boolean> status)
	throws PersistenceException;

//	void updateIkrCategoriesActivation(long monitorId, Map<Integer, Boolean> status)
//	throws PersistenceException;
	
	List<LogicalEnv> getLogicalEnvs() throws PersistenceException;
	
//	void resetMonitorEnv(String monitorEnv)throws PersistenceException;
	
	Map<Integer, ConnectorConfig> getConnectorConfigs() throws PersistenceException;
	Map<Integer, ConnectorConfig> getConnectorConfigs(Collection<Integer> connectorConfigIds) throws PersistenceException;
	List<Integer> getConnectorConfigIds() throws PersistenceException;
	ConnectorConfig getConnectorConfig(int connectorId) throws PersistenceException;
	void createConnector(ConnectorConfig connector) throws PersistenceException;
	void updateConnector(ConnectorConfig connector) throws PersistenceException;
	void deleteConnector(int connectorId) throws PersistenceException;
	
	List<Integer> getLastIkrStaticDomainIds(int maxSize)
	throws PersistenceException;

	List<Long> getLastMonitorConfigIds(int maxSize)
	throws PersistenceException;

	List<Integer> getLastLogicalenvIds(int maxSize)
	throws PersistenceException;

	List<Integer> getLastConnectorConfigIds(int maxSize)
	throws PersistenceException ;
	
//	Map<Long, StaticData> getStaticData() throws PersistenceException;
//	Map<Long,StaticData> getStaticData(String searchQuery, IkrUnitType searchIkrUnitType) throws PersistenceException;
//	void updateStaticData(StaticData staticData) throws PersistenceException;
//	void createStaticData(StaticData staticData) throws PersistenceException;	
}