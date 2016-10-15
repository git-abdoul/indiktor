package com.fsi.monitoring.indiktor;

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
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

/**
 * @author Abdou
 *
 */
/**
 * @author Abdou
 *
 */
public interface DataModelPM {	
	
	//------------------ IKR VERSION ------------------
	IkrVersion getIkrVersion() throws PersistenceException;
	
	//------------------ IKR STATIC DOMAIN ------------------
	void initIkrStaticDomains(String staticDomainModelFileName, String metricDomainConfigFileName) throws PersistenceException;
	Map<Integer, IkrStaticDomain> loadIkrStaticDomains();
	List<IkrStaticDomain> getIkrStaticDomains(int parentId);
	List<Integer> getIkrStaticDomainIds(int parentId)throws PersistenceException;
	List<Integer> getLastIkrStaticDomainIds(int maxSize) throws PersistenceException;
	IkrStaticDomain getIkrStaticDomain(int ikrStaticDomainId) throws PersistenceException;
	Map<Integer, IkrStaticDomain> getIkrStaticDomains(Collection<Integer> ikrStaticDomainIds) throws PersistenceException;
	IkrStaticDomain getIkrStaticDomainByValue(String valueName) throws PersistenceException;	
	int createIkrStaticDomain(IkrStaticDomain staticDomain) throws PersistenceException;
	void updateIkrStaticDomain(IkrStaticDomain staticDomain) throws PersistenceException;
	void deleteIkrStaticDomain(int id, int level) throws PersistenceException;
	void cleanIkrStaticDomains() throws PersistenceException;	
	
	
	//------------------ JOB SCHEDULER STATIC DOMAIN ------------------
	IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(int jobSchedulerStaticDomainId) throws PersistenceException;
	Map<Integer,IkrJobSchedulerStaticDomain> getJobSchedulerStaticDomains() throws PersistenceException;
	IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(String jobSchedulerStaticDomainType) throws PersistenceException;
	void createJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void updateJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void deleteJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) throws PersistenceException;
	void deleteJobSchedulerStaticDomains() throws PersistenceException;	
	
	//------------------ METRIC DOMAIN CONFIG ------------------
//	void addMetricDomainConfigField(MetricDomainConfigField field) throws PersistenceException;
//	void updateMetricDomainConfigField(MetricDomainConfigField field) throws PersistenceException;
//	void deleteMetricDomainConfigField(int id) throws PersistenceException;
//	Map<String, MetricDomainConfigField> getMetricDomainConfigFields(int metricDomainConfigId) throws PersistenceException;	
	List<MetricDomainConfig> getMetricDomainConfigs(int metricDomainId) throws PersistenceException;
	Map<Integer, List<MetricDomainConfig>> getMetricDomainConfigMap() throws PersistenceException;
	List<MetricDomainConfig> getMetricDomainConfigs() throws PersistenceException;
	MetricDomainConfig getMetricDomainConfig(int metricDomainConfigId) throws PersistenceException;	
	void addMetricDomainConfig(MetricDomainConfig config) throws PersistenceException;
	void updateMetricDomainConfig(MetricDomainConfig config) throws PersistenceException;
	void removeMetricDomainConfig(long configId) throws PersistenceException;
	void removeMetricDomainConfigByIkrStaticDomain(int metricDomainId) throws PersistenceException;
//	Map<String, List<String>> getMetricDomainItems() throws PersistenceException;
//	List<String> getMetricDomainItems(int metricDomainConfigId) throws PersistenceException;
	List<MetricDomainResource> loadMetricDomainResources() throws PersistenceException;
	List<MetricDomainResource> getMetricDomainResources(int ikrStaticDomainId) throws PersistenceException;
	MetricDomainResource getMetricDomainResource(int id) throws PersistenceException;
	MetricDomainResource getMetricDomainResource(int metricDomainId, String resourceName) throws PersistenceException;
	int saveMetricDomainResource(MetricDomainResource resource) throws PersistenceException;
	void removeMetricDomainResource(int resourceId) throws PersistenceException;
	int saveIkrCategoryResource(IkrCategoryResource resource) throws PersistenceException;
	void removeIkrCategoryResourceById(int resourceId) throws PersistenceException;
	void removeIkrCategoryResourceByStaticDomainId(int ikrStaticDomainId) throws PersistenceException;
	Map<String, IkrCategoryResource> getIkrCategoryResources(int metricDomainResourceId) throws PersistenceException;	
	int getIkrCategoryResourceId(int ikrCategoryId, int metricDomainResourceId, String name) throws PersistenceException;
	Map<Integer, IkrCategoryResource> getIkrCategoryResourcesById(int metricDomainResourceId) throws PersistenceException;
	Map<Integer, IkrCategoryResource> loadIkrCategoryResources() throws PersistenceException;
	List<IkrCategoryResource> getIkrCategoryResources(List<Integer> categoryResourceIds) throws PersistenceException;
	
	// ------------------ MONITOR ------------------
	MonitorConfig getMonitorConfig(long monitorId) throws PersistenceException;
	Map<Long, MonitorConfig> getMonitorConfigs(int logicalEnvId) throws PersistenceException;
	List<Long> getLastMonitorConfigIds(int maxSize) throws PersistenceException;
	List<Long> getMonitorConfigIds(int logicalEnvId) throws PersistenceException;
	
	long createMonitor(MonitorConfig config) throws PersistenceException;	
	void updateMonitor(MonitorConfig config) throws PersistenceException;
	void deleteMonitor(long monitorId) throws PersistenceException;	
	
	// ------------------ JOB SCHEDULER ------------------
	IkrJobSchedulerConfig getJobSchedulerConfig(int jobSchedulerId) throws PersistenceException;
	Map<Integer, IkrJobSchedulerConfig> getJobSchedulerConfigs(int logicalEnvId) throws PersistenceException;
	
	int createJobScheduler(IkrJobSchedulerConfig config) throws PersistenceException;	
	void updateJobScheduler(IkrJobSchedulerConfig config) throws PersistenceException;
	void deleteJobScheduler(int jobSchedulerId) throws PersistenceException;	
	
	// ------------------ JOB SCHEDULER ATTRIBUTE CONFIG------------------
	void addJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig) throws PersistenceException;
	void updateJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig) throws PersistenceException;
	void deleteJobSchedulerAttributeConfig(int id) throws PersistenceException;
	Map<String, IkrJobSchedulerAttributeConfig> getJobSchedulerAttributeConfigs(int jobStaticDomainId) throws PersistenceException;	

	Map<Integer,List<IkrCategoryResource>> getMonitorActivities(long monitorId) throws PersistenceException;
	
	// ------------------ LOGICAL ENV ------------------
	Map<Integer,LogicalEnv> getLogicalEnvs() throws PersistenceException;
	LogicalEnv getLogicalEnv(int logicalEnvId) throws PersistenceException;
	LogicalEnv getLogicalEnv(String logicalEnvName) throws PersistenceException;
	List<Integer> getLastLogicalenvIds(int maxSize) throws PersistenceException;
	
	void createLogicalEnv(LogicalEnv env) throws PersistenceException;
	void updateLogicalEnv(LogicalEnv env) throws PersistenceException;
	void deleteLogicalEnv(int envId) throws PersistenceException;
	
	// ------------------ CONNECTOR ------------------
	Map<Integer, ConnectorConfig> getConnectorConfigs() throws PersistenceException;
	List<Integer> getConnectorConfigIds() throws PersistenceException;
	List<Integer> getLastConnectorConfigIds(int maxSize) throws PersistenceException;
	ConnectorConfig getConnectorConfig(int connectorId) throws PersistenceException;
	long saveConnector(ConnectorConfig connector) throws PersistenceException;
	void updateConnector(ConnectorConfig connector) throws PersistenceException;
	void deleteConnector(int connectorId) throws PersistenceException;
}