package com.fsi.monitoring.indiktor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.SystemException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.cache.CacheName;
import com.fsi.monitoring.connector.AbstractConnectorConfig;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.global.IdGenerator;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.IkrVersion;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.dao.DataModelDAO;
import com.fsi.monitoring.indiktor.dao.impl.DomainConfigModelFeeder;
import com.fsi.monitoring.indiktor.dao.impl.DomainTypeModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricCategoryModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricDomainConfigAttributeModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricDomainConfigFieldModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricDomainConfigModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricDomainConfigResourceModel;
import com.fsi.monitoring.indiktor.dao.impl.MetricDomainModel;
import com.fsi.monitoring.indiktor.dao.impl.StaticDomainModelFeeder;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

public class DataModelPMFactory 
implements DataModelPM {

	private static final Logger logger = Logger.getLogger(DataModelPMFactory.class);

	private CacheManager cacheManager;

	private DataModelDAO dataModelDAO = null;
	
	private IdGenerator connectorIdGenerator = null;	
	private IdGenerator metricDomainConfigIdGenerator = null;	
	private IdGenerator sdIdGenerator = null;
	private IdGenerator staticDomainIdGenerator = null;
	private IdGenerator metricDomainResourceIdGenerator = null;	
	private IdGenerator ikrCategoryResourceIdGenerator = null;	
	
	public void setConnectorIdGenerator(IdGenerator connectorIdGenerator) {
		this.connectorIdGenerator = connectorIdGenerator;
	}
	
	public void setStaticDataIdGenerator(IdGenerator sdIdGenerator) {
		this.sdIdGenerator = sdIdGenerator;
	}
	
	public void setMetricDomainConfigIdGenerator(
			IdGenerator metricDomainConfigIdGenerator) {
		this.metricDomainConfigIdGenerator = metricDomainConfigIdGenerator;
	}	

	public void setStaticDomainIdGenerator(IdGenerator staticDomainIdGenerator) {
		this.staticDomainIdGenerator = staticDomainIdGenerator;
	}

	public void setMetricDomainResourceIdGenerator(
			IdGenerator metricDomainResourceIdGenerator) {
		this.metricDomainResourceIdGenerator = metricDomainResourceIdGenerator;
	}	

	public void setIkrCategoryResourceIdGenerator(
			IdGenerator ikrCategoryResourceIdGenerator) {
		this.ikrCategoryResourceIdGenerator = ikrCategoryResourceIdGenerator;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setDataModelDAO(DataModelDAO dataModelDAO) {
		this.dataModelDAO = dataModelDAO;
	}

	public IkrVersion getIkrVersion() throws PersistenceException {
		return dataModelDAO.getIkrVersion();
	}

	public int createIkrStaticDomain(IkrStaticDomain staticDomain)
	throws PersistenceException {
		int nextId = (int)staticDomainIdGenerator.getNextId(1);
		dataModelDAO.createIkrStaticDomain(staticDomain, nextId);
		
//		Ehcache ikrCategoryCache = cacheManager.getCache(CacheName.IkrCategoryCache.name());
//		staticDomain.setId(nextId);
//		ikrCategoryCache.put(new Element(nextId, staticDomain));
		return nextId;
	}
	
	public void addMetricDomainConfig(MetricDomainConfig config)
	throws PersistenceException {
		long nextId = metricDomainConfigIdGenerator.getNextId(1);
		dataModelDAO.addMetricDomainConfig(config, nextId);
	}

	public void updateMetricDomainConfig(MetricDomainConfig config)
	throws PersistenceException {
		dataModelDAO.updateMetricDomainConfig(config);
	}	
	
	public void removeMetricDomainConfig(long configId)
	throws PersistenceException {
		dataModelDAO.removeMetricDomainConfig(configId);
	}
	
	public void removeMetricDomainConfigByIkrStaticDomain(int metricDomainId) throws PersistenceException{
		List<MetricDomainConfig> metricDomainConfigs = dataModelDAO.getMetricDomainConfigs(metricDomainId);
		for (MetricDomainConfig config : metricDomainConfigs) {
			dataModelDAO.removeMetricDomainConfig(config.getId());
		}
		
	}
	
	public void deleteIkrStaticDomain(int id, int level) throws PersistenceException {
		List<Integer> ids = dataModelDAO.getIkrStaticDomain(id, level);
		dataModelDAO.deleteIkrStaticDomain(id, level);
		
		Ehcache ikrCategoryCache = cacheManager.getCache(CacheName.IkrCategoryCache.name());
		for (int staticDomainId : ids) {
			ikrCategoryCache.remove(staticDomainId);
		}
	}

	public void updateIkrStaticDomain(IkrStaticDomain staticDomain)
			throws PersistenceException {
		dataModelDAO.updateIkrStaticDomain(staticDomain);
		Ehcache ikrCategoryCache = cacheManager.getCache(CacheName.IkrCategoryCache.name());
		ikrCategoryCache.remove(staticDomain.getId());
	}	

	public void cleanIkrStaticDomains() throws PersistenceException {
		List<Integer> ids = dataModelDAO.loadIkrStaticDomains();
		dataModelDAO.cleanIkrStaticDomains();
		
		Ehcache ikrCategoryCache = cacheManager.getCache(CacheName.IkrCategoryCache.name());
		for (int id : ids) {
			ikrCategoryCache.remove(id);
		}
	}
	
	public Map<String, MetricDomainResource> getMetricDomainResourcesMap(int ikrStaticDomainId) throws PersistenceException {		
		Map<String, MetricDomainResource> res = new HashMap<String, MetricDomainResource>();		
		List<MetricDomainResource> resources = getMetricDomainResources (ikrStaticDomainId);
		for (MetricDomainResource resource : resources) {
			res.put(resource.getResourceName(), resource);
		}		
		return res;
	}	
	
	public void initIkrStaticDomains(String staticDomainModelFileName, String metricDomainConfigFileName) throws PersistenceException {
		try {
			StaticDomainModelFeeder staticDomainFeeder = (new StaticDomainModelFeeder()).parse(staticDomainModelFileName);
			DomainConfigModelFeeder domainConfigFeeder = (new DomainConfigModelFeeder()).parse(metricDomainConfigFileName);
			
			// Domain Types
			Map<String, Integer> domainTypeIds = new HashMap<String, Integer>();
			int rootDomainId = 0;
			List<DomainTypeModel> domainTypes = staticDomainFeeder.getSupportedDomainTypes();
			for (DomainTypeModel model : domainTypes) {
				IkrStaticDomain ikrStaticDomain = new IkrStaticDomain(0,rootDomainId, model.getDomainType(), model.getLabel(), "");
				int domainTypeId = dataModelDAO.getIkrStaticDomainIdByValue(model.getDomainType());
				if(domainTypeId == 0) {
					domainTypeId = (int)staticDomainIdGenerator.getNextId(1);
					dataModelDAO.createIkrStaticDomain(ikrStaticDomain, domainTypeId);  // Save Domain Type
				}
				else {
					ikrStaticDomain.setId(domainTypeId);
					dataModelDAO.updateIkrStaticDomain(ikrStaticDomain);
				}
				domainTypeIds.put(model.getDomainType(), domainTypeId);
			}
			
			Map<String, Integer> metricDomainIds = new HashMap<String, Integer>();
			Map<String, List<MetricDomainModel>> metricDomains = staticDomainFeeder.getSupportedMetricDomainTypes();
			for (String domainType : metricDomains.keySet()) {
				int parentDomainId = domainTypeIds.get(domainType);
				for (MetricDomainModel model : metricDomains.get(domainType)) {
					IkrStaticDomain ikrStaticDomain = new IkrStaticDomain(0,parentDomainId, model.getType(), model.getLabel(), "");
					int metricDomainId = dataModelDAO.getIkrStaticDomainIdByValue(model.getType());
					if(metricDomainId == 0) {
						metricDomainId = (int)staticDomainIdGenerator.getNextId(1);
						dataModelDAO.createIkrStaticDomain(ikrStaticDomain, metricDomainId);  // Save Metric Domain
					}
					else {
						ikrStaticDomain.setId(metricDomainId);
						dataModelDAO.updateIkrStaticDomain(ikrStaticDomain);
					}
					metricDomainIds.put(model.getType(), metricDomainId);
					Set<String> resources = model.getResources();
					Map<String, Integer> resourcesIds = new HashMap<String, Integer>();
					Map<String, MetricDomainResource> domainResources = getMetricDomainResourcesMap(metricDomainId);
					for (String resourceName : resources) {	
						MetricDomainResource resource = domainResources.get(resourceName);
						int id = 0;
						if (resource == null) {
							id = (int)metricDomainResourceIdGenerator.getNextId(1); 
							dataModelDAO.addMetricDomainResource(new MetricDomainResource(0, metricDomainId, resourceName), id); // Save Resources for Metric Domain							
						}
						else {
							id = resource.getId();
							dataModelDAO.updateMetricDomainResource(new MetricDomainResource(id, metricDomainId, resourceName));
						}
						resourcesIds.put(resourceName, id);
					}
					
					List<MetricCategoryModel> metricCategories = model.getMetricCategories();
					for (MetricCategoryModel cModel : metricCategories) {
						IkrUnitType ikrUnitType = null;
						try {
							ikrUnitType = IkrUnitType.valueOf(cModel.getUnitType());
						} catch (Exception exc) {
							// just in case there is an error in the file
							logger.error("Impossible to create category because of wrong IkrUnitType: " + cModel.getLabel());
							continue;
						}
						
						IkrUnit ikrUnit = null;
						if (ikrUnitType != null && cModel.getUnitType() != null) {
							ikrUnit = ikrUnitType.getIkrUnit(cModel.getUnit());
						}
						
						String indexeStr = cModel.getSearchIndexes();
						List<String> searchIndexes = new ArrayList<String>();
						if (indexeStr!=null && indexeStr.length()>0) {
							String[] indexes = indexeStr.split(":");
							searchIndexes = Arrays.asList(indexes);
						}
						
						IkrCategory ikrCategory = new IkrCategory(0,
																  metricDomainId,
																  cModel.getLabel(),
																  cModel.getLabel(),
																  cModel.getDescription(),
																  ikrUnitType,
																  ikrUnit,
																  0,
																  cModel.isPersistent(),
																  cModel.isArchive(),
																  searchIndexes);
						int categoryId = dataModelDAO.getIkrStaticDomainIdByValue(cModel.getLabel());
						if(categoryId == 0) {
							categoryId = (int)staticDomainIdGenerator.getNextId(1);
							dataModelDAO.createIkrStaticDomain(ikrCategory, categoryId);  // Save Metric Category
						}
						else {
							ikrCategory.setId(categoryId);
							dataModelDAO.updateIkrStaticDomain(ikrCategory);
						}
												
						// Category Resources
						IkrCategoryResource resource = dataModelDAO.getIkrCategoryResource(categoryId);
						if (resource == null) {
							int id = (int)ikrCategoryResourceIdGenerator.getNextId(1);
							dataModelDAO.saveIkrCategoryResource(new IkrCategoryResource(0, categoryId, resourcesIds.get(cModel.getResource()), cModel.getName(), true), id); // Save Resources for Metric Category
						}
						else
							dataModelDAO.updateIkrCategoryResource(new IkrCategoryResource(resource.getId(), categoryId, resourcesIds.get(cModel.getResource()), cModel.getName(), true));
					}					
				}
			}
			
			//Metric Domain Config
			Map<String, List<MetricDomainConfigModel>> metricDomainConfigs = domainConfigFeeder.getSupportedMetricDomainconfigs();
			for (String type : metricDomainConfigs.keySet()) {
				int metricDomainId = metricDomainIds.get(type);
				for (MetricDomainConfigModel configModel : metricDomainConfigs.get(type)) {
					long metricDomainConfiId = metricDomainConfigIdGenerator.getNextId(1);					
					MetricDomainConfig config = dataModelDAO.getMetricDomainConfig(metricDomainId, configModel.getClassname(), configModel.getDescription());
					if (config == null) {
						config = new MetricDomainConfig();
					}
					else {
						metricDomainConfiId = config.getId();
					}
					
					config.setIkrStaticDomainId(metricDomainId);
					config.setClassName(configModel.getClassname());
					config.setUseDataSynchronization(configModel.isUseDataSynchronization());
					String[] connectorTypes = configModel.getConnectorType().split(",");
					config.setConnectorType(Arrays.asList(connectorTypes));
					config.setDescription(configModel.getDescription());
					
					config.setDomainItemConfigs(configModel.getDomainItems());
					
					List<MetricDomainConfigField> fields = new ArrayList<MetricDomainConfigField>();
					for (MetricDomainConfigFieldModel field : configModel.getFields()){
						List<String> fieldTypeValues = new ArrayList<String>();
			        	if (field.getFieldTypeValues()!=null && field.getFieldTypeValues().length()>0){
			        		fieldTypeValues = Arrays.asList(field.getFieldTypeValues().split(","));
			        	}	 
			        	fields.add(new MetricDomainConfigField(0, (int)metricDomainConfiId, field.getName(), field.getLabel(), field.isEnable(), field.getFieldType(),fieldTypeValues));
					}
					config.setFields(fields);	
					
					Map<String, String> attributes = new HashMap<String, String>();
					for (MetricDomainConfigAttributeModel attr : configModel.getAttributes()){
						attributes.put(attr.getKey(), attr.getValue());
					}
					config.setAttributes(attributes);
					
					List<MetricDomainConfigResource> resources = new ArrayList<MetricDomainConfigResource>();
					for (MetricDomainConfigResourceModel resourceModel : configModel.getResources()){						
						MetricDomainResource resource = dataModelDAO.getMetricDomainResource(metricDomainId, resourceModel.getName());
						if (resource==null) {
							int nextId = (int)metricDomainResourceIdGenerator.getNextId(1); 
							resource = new MetricDomainResource(nextId, metricDomainId, resourceModel.getName());
							dataModelDAO.addMetricDomainResource(resource, nextId); // Save Resources for Metric Domain
						}
						resources.add(new MetricDomainConfigResource((int)metricDomainConfiId, resource, resourceModel.isEnable()));
					}
					config.setResources(resources);	
					
					if (config.getId() == 0) {
						dataModelDAO.addMetricDomainConfig(config, metricDomainConfiId);
					}
					else {
						dataModelDAO.updateMetricDomainConfig(config);
					}
				}
			}			
			
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
	public Map<Integer, IkrStaticDomain> loadIkrStaticDomains() {
		List<Integer> ids = null;
		try {
			ids = dataModelDAO.loadIkrStaticDomains();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
		if (ids == null)
			return new HashMap<Integer, IkrStaticDomain>();
		
		Ehcache ikrCategoryCache = cacheManager.getEhcache(CacheName.IkrCategoryCache.name());
		Map<Integer, IkrStaticDomain> ikrStaticDomains = ikrCategoryCache.getAllWithLoader(ids, null);
		
		return ikrStaticDomains;
	}

	public List<IkrStaticDomain> getIkrStaticDomains(int parentId) {
		List<Integer> ids = null;		
		try {
			ids = dataModelDAO.getIkrStaticDomainIds(parentId);
		} catch (PersistenceException e) {
			logger.error(e);
		}
		
		if (ids == null)
			return new ArrayList<IkrStaticDomain>();
		
		Ehcache ikrCategoryCache = cacheManager.getEhcache(CacheName.IkrCategoryCache.name());
		Map<Integer, IkrStaticDomain> ikrStaticDomains = ikrCategoryCache.getAllWithLoader(ids, null);
		
		List<IkrStaticDomain> res = new ArrayList<IkrStaticDomain>();
		if (ikrStaticDomains != null)
			res = new ArrayList<IkrStaticDomain>(ikrStaticDomains.values());
		
		return res;
	}
	
	public Map<Integer, IkrStaticDomain> getIkrStaticDomains(Collection<Integer> ikrStaticDomainIds) throws PersistenceException {
		Ehcache ikrCategoryCache = cacheManager.getEhcache(CacheName.IkrCategoryCache.name());
		Map<Integer, IkrStaticDomain> ikrStaticDomains = ikrCategoryCache.getAllWithLoader(ikrStaticDomainIds, null);		
		return ikrStaticDomains;
	}
	
	public List<Integer> getIkrStaticDomainIds(int parentId) throws PersistenceException {
		return dataModelDAO.getIkrStaticDomainIds(parentId);
	}
	
	public long createMonitor(MonitorConfig config) throws PersistenceException {
		long monitorId = dataModelDAO.createMonitor(config);		
//		Ehcache monitorCache = cacheManager.getCache(CacheName.MonitorCache.name());
//		config.setId(monitorId);
//		monitorCache.put(new Element(monitorId, config));
		return monitorId;
	}	
	
	public void deleteMonitor(long monitorId) throws PersistenceException {
		Ehcache monitorCache = cacheManager.getCache(CacheName.MonitorCache.name());
		dataModelDAO.deleteMonitor(monitorId);
		monitorCache.remove(monitorId);
	}

	public void createLogicalEnv(LogicalEnv env) throws PersistenceException {
		dataModelDAO.createLogicalEnv(env);		
	}

	public void updateLogicalEnv(LogicalEnv env) throws PersistenceException {
		dataModelDAO.updateLogicalEnv(env);		
	}

	public void deleteLogicalEnv(int envId) throws PersistenceException {
		dataModelDAO.deleteLogicalEnv(envId);		
	}

	public MonitorConfig getMonitorConfig(long monitorId)
	throws PersistenceException {
		Ehcache monitorCache = cacheManager.getCache(CacheName.MonitorCache.name());
		Element element = monitorCache.getWithLoader(monitorId, null, null);
		return (MonitorConfig) element.getValue();
	}

	public Map<Long, MonitorConfig> getMonitorConfigs(int logicalEnvId)
	throws PersistenceException {
		Collection<Long> monitorIds = dataModelDAO.loadMonitorConfigs(logicalEnvId);
		Ehcache monitorCache = cacheManager.getCache(CacheName.MonitorCache.name());
		Map<Long, MonitorConfig> monitors = monitorCache.getAllWithLoader(monitorIds, null);
		if (monitors == null)
			monitors = new HashMap<Long, MonitorConfig>();
		return monitors;
	}
	
	public List<Long> getMonitorConfigIds(int logicalEnvId) throws PersistenceException {
		Collection<Long> monitorIds = dataModelDAO.loadMonitorConfigs(logicalEnvId);
		return new ArrayList<Long>(monitorIds);
	}

	public void updateMonitor(MonitorConfig config) throws PersistenceException {
		dataModelDAO.updateMonitor(config);
		Ehcache monitorCache = cacheManager.getEhcache(CacheName.MonitorCache.name());
		monitorCache.remove(config.getId());
	}

	public Map<Integer,LogicalEnv> getLogicalEnvs() throws PersistenceException {
		List<LogicalEnv> envs = dataModelDAO.getLogicalEnvs();	
		Map<Integer,LogicalEnv> res = new HashMap<Integer,LogicalEnv>();		
		for (LogicalEnv env : envs) {
			res.put(env.getId(),env);
		}		
		return res;
	}
	
	public LogicalEnv getLogicalEnv(int logicalEnvId) throws PersistenceException {
		return getLogicalEnvs().get(logicalEnvId);
	}
	
	public LogicalEnv getLogicalEnv(String logicalEnvName) throws PersistenceException {
		Map<Integer,LogicalEnv> envs = getLogicalEnvs();
		
		for (LogicalEnv env : envs.values()) {
			if (env.getName().equals(logicalEnvName)) {
				return env;
			}
		}
		return null;
	}
	
	public ConnectorConfig getConnectorConfig(int connectorId) throws PersistenceException {
		Ehcache connectorCache = cacheManager.getEhcache(CacheName.ConnectorCache.name());
		Element element = connectorCache.getWithLoader(connectorId, null, null);
		return (ConnectorConfig) element.getValue();
	}

	public Map<Integer, ConnectorConfig> getConnectorConfigs() throws PersistenceException {
		List<Integer> connectorConfigIds = dataModelDAO.getConnectorConfigIds();
		Ehcache connectorCache = cacheManager.getEhcache(CacheName.ConnectorCache.name());
		Map<Integer, ConnectorConfig> connectorConfigs = connectorCache.getAllWithLoader(connectorConfigIds, null);		
		return connectorConfigs;
	}
	
	public List<Integer> getConnectorConfigIds() throws PersistenceException {
		return dataModelDAO.getConnectorConfigIds();
	}

	public long saveConnector(ConnectorConfig connector)
	throws PersistenceException {
		long id = connector.getId();
		id = connectorIdGenerator.getNextId(1);	
		AbstractConnectorConfig connectConfig = (AbstractConnectorConfig)connector;	
		connectConfig.setId((int)id);
		dataModelDAO.createConnector(connectConfig);
		
		return id;
	}
	
	public void updateConnector(ConnectorConfig connector) throws PersistenceException{
		Ehcache connectorCache = cacheManager.getEhcache(CacheName.ConnectorCache.name());
		dataModelDAO.updateConnector(connector);			
		connectorCache.remove(connector.getId());
	}
	
	public void deleteConnector(int connectorId) throws PersistenceException {
		dataModelDAO.deleteConnector(connectorId);
		Ehcache connectorCache = cacheManager.getEhcache(CacheName.ConnectorCache.name());
		connectorCache.remove(connectorId);
	}
	
	public List<MetricDomainConfig> getMetricDomainConfigs() throws PersistenceException {
		return  dataModelDAO.loadMetricDomainConfigs();
	}
	
	public Map<Integer, List<MetricDomainConfig>> getMetricDomainConfigMap() throws PersistenceException {
		Map<Integer, List<MetricDomainConfig>> res = new HashMap<Integer, List<MetricDomainConfig>>();
		List<MetricDomainConfig> configs = getMetricDomainConfigs();
		for (MetricDomainConfig config : configs) {
			int metricDomainId = config.getIkrStaticDomainId();
			List<MetricDomainConfig> values = res.get(metricDomainId);
			if (values==null) {
				values = new ArrayList<MetricDomainConfig>();
				res.put(metricDomainId, values);
			}
			values.add(config);
		}		
		return res;
	}

	public List<MetricDomainConfig> getMetricDomainConfigs(int metricDomainId) 
	throws PersistenceException {	
		return dataModelDAO.getMetricDomainConfigs(metricDomainId);
//		
//		Iterator<MetricDomainConfig> resIT = res.iterator();
//		while(resIT.hasNext()) {
//			MetricDomainConfig config = resIT.next();
//			if (config.getIkrStaticDomainId() != metricDomainId) {
//				resIT.remove();
//			}
//		}
//		
//		return res;
	}	
	
	public MetricDomainConfig getMetricDomainConfig(int metricDomainConfigId) 
	throws PersistenceException {	
		return dataModelDAO.getMetricDomainConfig(metricDomainConfigId);
		
//		for(MetricDomainConfig config : configs) {
//			if (config.getId() == metricDomainConfigId) {
//				return config;
//			}
//		}
//		return null;
	}	


	
	public List<MetricDomainResource> loadMetricDomainResources() throws PersistenceException {
		return dataModelDAO.getMetricDomainResources();
	}
	
	public List<MetricDomainResource> getMetricDomainResources(
			int ikrStaticDomainId) throws PersistenceException {
		return dataModelDAO.getMetricDomainResources(ikrStaticDomainId);
	}

	public MetricDomainResource getMetricDomainResource(int metricDomainId, String resourceName)
			throws PersistenceException {
		return dataModelDAO.getMetricDomainResource(metricDomainId, resourceName);
	}
	
	public MetricDomainResource getMetricDomainResource(int id)
	throws PersistenceException {
		return dataModelDAO.getMetricDomainResource(id);
	}

	public int saveMetricDomainResource(MetricDomainResource resource)
			throws PersistenceException {
		int id = resource.getId();
		if (resource.getId() == 0) {
			 id = (int)metricDomainResourceIdGenerator.getNextId(1);			
			dataModelDAO.addMetricDomainResource(resource, id);
		} else {
			dataModelDAO.updateMetricDomainResource(resource);
		}
		
		return id;
	}
	
	public int saveIkrCategoryResource(IkrCategoryResource resource) throws PersistenceException {
		int id = resource.getId();
		if (id==0) {
			 id = (int)ikrCategoryResourceIdGenerator.getNextId(1);
			 dataModelDAO.saveIkrCategoryResource(resource, id);
		} else {
			dataModelDAO.updateIkrCategoryResource(resource);
		}
		
		return id;
	}

	public void removeMetricDomainResource(int resourceId)
			throws PersistenceException {
		dataModelDAO.removeMetricDomainResource(resourceId);
	}
	
	public void removeIkrCategoryResourceById(int resourceId) throws PersistenceException {
		dataModelDAO.deleteIkrCategoryResourceById(resourceId);
	}
	
	public void removeIkrCategoryResourceByStaticDomainId(int ikrStaticDomainId) throws PersistenceException {
		dataModelDAO.deleteIkrCategoryResourceByStaticDomainId(ikrStaticDomainId);
	}

	public Map<Integer, List<IkrCategoryResource>> getMonitorActivities(long monitorId) throws PersistenceException {
		return dataModelDAO.getMonitorActivities(monitorId);
	}

	public IkrStaticDomain getIkrStaticDomain(int ikrStaticDomainId) throws PersistenceException{
		Ehcache ikrCategoryCache = cacheManager.getCache(CacheName.IkrCategoryCache.name());
		Element element = ikrCategoryCache.getWithLoader(ikrStaticDomainId, null, null);
		return (IkrStaticDomain) element.getValue();
	}

	public IkrStaticDomain getIkrStaticDomainByValue(String value) throws PersistenceException{
		int id = dataModelDAO.getIkrStaticDomainIdByValue(value);
		IkrStaticDomain res = getIkrStaticDomain(id);
		return res;
	}
	
	public Map<String, IkrCategoryResource> getIkrCategoryResources(int metricDomainResourceId) throws PersistenceException {
		return dataModelDAO.getIkrCategoryResources(metricDomainResourceId);
	}
	
	public List<IkrCategoryResource> getIkrCategoryResources(List<Integer> categoryResourceIds) throws PersistenceException {
		return dataModelDAO.getIkrCategoryResources(categoryResourceIds);
	}
	
	public Map<Integer, IkrCategoryResource> getIkrCategoryResourcesById(int metricDomainResourceId) throws PersistenceException {
		return dataModelDAO.getIkrCategoryResourcesById(metricDomainResourceId);
	}
	
	public int getIkrCategoryResourceId(int ikrCategoryId, int metricDomainResourceId, String name) throws PersistenceException {
		return dataModelDAO.getIkrCategoryResourceId(ikrCategoryId, metricDomainResourceId, name);
	}
	
	public Map<Integer, IkrCategoryResource> loadIkrCategoryResources() throws PersistenceException {
		return dataModelDAO.loadIkrCategoryResources();
	}

	public IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(int taskStaticDomainId)
			throws PersistenceException {
		return dataModelDAO.getJobSchedulerStaticDomain(taskStaticDomainId);
	}

	public Map<Integer, IkrJobSchedulerStaticDomain> getJobSchedulerStaticDomains()
			throws PersistenceException {
		List<IkrJobSchedulerStaticDomain> taskDomains = dataModelDAO.getJobSchedulerStaticDomains();		
		Map<Integer,IkrJobSchedulerStaticDomain> res = new HashMap<Integer,IkrJobSchedulerStaticDomain>();		
		for (IkrJobSchedulerStaticDomain domain : taskDomains) {
			res.put(domain.getId(),domain);
		}		
		return res;
	}

	public IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain(String taskStaticDomainType)
			throws PersistenceException {
		return dataModelDAO.getJobSchedulerStaticDomain(taskStaticDomainType);
	}

	public void createJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain taskStaticDomain)
			throws PersistenceException {
		dataModelDAO.createJobSchedulerStaticDomain(taskStaticDomain);		
	}

	public void updateJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain taskStaticDomain)
			throws PersistenceException {
		dataModelDAO.updateJobSchedulerStaticDomain(taskStaticDomain);
	}

	public void deleteJobSchedulerStaticDomain(IkrJobSchedulerStaticDomain taskStaticDomain)
			throws PersistenceException {
		dataModelDAO.deleteJobSchedulerStaticDomain(taskStaticDomain);
	}

	public void deleteJobSchedulerStaticDomains() throws PersistenceException {
		dataModelDAO.deleteJobSchedulerStaticDomains();
	}

	public IkrJobSchedulerConfig getJobSchedulerConfig(int taskId)
			throws PersistenceException {
		Ehcache scheduledTaskCache = cacheManager.getCache(CacheName.ScheduledTaskCache.name());

		Element element = scheduledTaskCache.getWithLoader(taskId, null, null);

		return (IkrJobSchedulerConfig) element.getValue();
	}

	public Map<Integer, IkrJobSchedulerConfig> getJobSchedulerConfigs(int logicalEnvId) throws PersistenceException {
		return dataModelDAO.loadJobSchedulerConfigs(logicalEnvId);
	}

	public int createJobScheduler(IkrJobSchedulerConfig config)
			throws PersistenceException {
		int taskId = dataModelDAO.createJobScheduler(config);		
		return taskId;
	}

	public void updateJobScheduler(IkrJobSchedulerConfig config)
			throws PersistenceException {
		dataModelDAO.updateJobScheduler(config);
		Ehcache scheduledTaskCache = cacheManager.getCache(CacheName.ScheduledTaskCache.name());
		scheduledTaskCache.remove(config.getId());	
	}

	public void deleteJobScheduler(int taskId) throws PersistenceException {
		Ehcache scheduledTaskCache = cacheManager.getCache(CacheName.ScheduledTaskCache.name());
		dataModelDAO.deleteJobScheduler(taskId);
		scheduledTaskCache.remove(taskId);		
	}

	public void addJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig)
			throws PersistenceException {
		dataModelDAO.addJobSchedulerAttributeConfig(attrConfig);
	}

	public void updateJobSchedulerAttributeConfig(IkrJobSchedulerAttributeConfig attrConfig)
			throws PersistenceException {
		dataModelDAO.updateJobSchedulerAttributeConfig(attrConfig);
	}

	public void deleteJobSchedulerAttributeConfig(int id)
			throws PersistenceException {
		dataModelDAO.deleteJobSchedulerAttributeConfig(id);
	}

	public Map<String, IkrJobSchedulerAttributeConfig> getJobSchedulerAttributeConfigs(int taskStaticDomainId) throws PersistenceException {
		return dataModelDAO.getJobSchedulerAttributeConfigs(taskStaticDomainId);
	}

	public List<Integer> getLastIkrStaticDomainIds(int maxSize)
			throws PersistenceException {
		return dataModelDAO.getLastIkrStaticDomainIds(maxSize);
	}

	public List<Long> getLastMonitorConfigIds(int maxSize)
			throws PersistenceException {
		return dataModelDAO.getLastMonitorConfigIds(maxSize);
	}

	public List<Integer> getLastLogicalenvIds(int maxSize)
			throws PersistenceException {
		return dataModelDAO.getLastLogicalenvIds(maxSize);
	}

	public List<Integer> getLastConnectorConfigIds(int maxSize)
			throws PersistenceException {
		return dataModelDAO.getLastConnectorConfigIds(maxSize);
	}	
}
