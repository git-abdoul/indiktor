package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.connector.HttpConnectorConfig;
import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;

public class MetricDomainConfigSelectorBean
implements Serializable {

	private static final long serialVersionUID = 4224971416355277243L;

	private final static Logger logger = Logger.getLogger(MetricDomainConfigSelectorBean.class);	
	
	private MonitorConfig monitorConfig;
	
	private Map<Integer,MetricDomainConfig> metricDomainConfigMap;
	private Map<String,Integer> connectorIdMap;
	private Map<Integer,ConnectorConfig> connectorConfigMap;	
	private Map<Integer, Map<String, MetricDomainResource>> metricDomainResourceMap;
	
	private Collection<SelectItem> jmxConnectorItems;
	private Collection<SelectItem> calypsoConnectorItems;
	private Collection<SelectItem> httpConnectorItems;
	private Collection<SelectItem> sysloadConnectorItems;
	private Collection<SelectItem> rdbmsConnectorItems;
	private Collection<SelectItem> systemAgentConnectorItems;	
	
	
	private Collection<SelectItem> metricDomainConfigItems;	
	
	public MetricDomainConfigSelectorBean(MonitorConfig monitorConfig) {
		this.monitorConfig = monitorConfig;		
		initConnector();
	}
	
//	public void changeMetricDomainConfig(ValueChangeEvent e) {
//		Integer newValue = (Integer)e.getNewValue();
//		
//		MetricDomainConfig metricDomainConfig = metricDomainConfigMap.get(newValue);
//		monitorConfig.setMetricDomainConfig(metricDomainConfig);
//		
//		Map<String, MetricDomainResource> metricDomainResources = metricDomainResourceMap.get(metricDomainConfig.getIkrStaticDomainId());
//		monitorConfig.setMetricDomainResources(metricDomainResources);	
//		
//		resetConnectorNames();
//	}
	
	public Map<String, MetricDomainResource> getMetricDomainResources(int metricDomainId) {
		return metricDomainResourceMap.get(metricDomainId);	
	}
	
	public MetricDomainConfig getMetricDomainConfig(int metricDomainConfigId) {
		return metricDomainConfigMap.get(metricDomainConfigId);
	}
	
	public int getMetricDomainConfigId() {
		return monitorConfig.getMetricDomainConfig().getId();
	}
	
	public void setMetricDomainConfigId(int id) {}
	
	public void setRdbmsConnectorId(int connectorId) {
		connectorIdMap.put(RdbmsConnectorConfig.TYPE, connectorId);
	}
	
	public int getRdbmsConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(RdbmsConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public Collection<SelectItem> getSystemAgentConnectorItems() {
		return systemAgentConnectorItems;
	}
	
	public int getSystemAgentConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(SystemAgentConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public void setSystemAgentConnectorId(int connectorId) {
		connectorIdMap.put(SystemAgentConnectorConfig.TYPE, connectorId);
	}	
	
	public Collection<SelectItem> getJmxConnectorItems() {
		return jmxConnectorItems;
	}
	
	public int getJmxConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(JmxConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public void setJmxConnectorId(int connectorId) {
		connectorIdMap.put(JmxConnectorConfig.TYPE, connectorId);
	}
	
	public Collection<SelectItem> getCalypsoConnectorItems() {
		return calypsoConnectorItems;
	}
	
	public int getCalypsoConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(CalypsoConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public void setCalypsoConnectorId(int connectorId) {
		connectorIdMap.put(CalypsoConnectorConfig.TYPE, connectorId);
	}	
	
	public Collection<SelectItem> getHttpConnectorItems() {
		return httpConnectorItems;
	}
	
	public int getHttpConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(HttpConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public void setHttpConnectorId(int connectorId) {
		connectorIdMap.put(HttpConnectorConfig.TYPE, connectorId);
	}	
	
	public Collection<SelectItem> getSysloadConnectorItems() {
		return sysloadConnectorItems;
	}
	
	public int getSysloadConnectorId() {
		Integer id = null;
		if (monitorConfig.getId() > 0) {
			List<Integer> ids = new ArrayList<Integer>( monitorConfig.getConnectorConfigIds());
			if (ids.size()>0)
				id = ids.get(0);
		}
		else {
			id = connectorIdMap.get(SysloadConnectorConfig.TYPE);
		}
		return (id == null? 0 : id);
	}
	
	public void setSysloadConnectorId(int connectorId) {
		connectorIdMap.put(SysloadConnectorConfig.TYPE, connectorId);
	}	
	
	public Collection<SelectItem> getRdbmsConnectorItems() {
		return rdbmsConnectorItems;
	}	
	
	public Collection<SelectItem> getMetricDomainConfigItems() {
		return metricDomainConfigItems;
	}
	
	public boolean isCalypsoConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(CalypsoConnectorConfig.TYPE);	
	}
	
	public boolean isJmxConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(JmxConnectorConfig.TYPE);	
	}

	public boolean isHttpConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(HttpConnectorConfig.TYPE);	
	}
	
	public boolean isSysloadConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(SysloadConnectorConfig.TYPE);	
	}
	
	public boolean isSystemAgentConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(SystemAgentConnectorConfig.TYPE);	
	}
	
	public boolean isRdbmsConnectorRendered() {
		Collection<String> connectorTypes = monitorConfig.getMetricDomainConfig().getConnectorTypes();
		return connectorTypes.contains(RdbmsConnectorConfig.TYPE);	
	}
	
	public void changeMetricDomain(int metricDomainId) {
		MetricDomainConfig metricDomainConfig = initMetricDomainConfigs(metricDomainId);
		monitorConfig.setMetricDomainConfig(metricDomainConfig);		
		resetConnectorNames();
	}	
	
	public void resetConnectorNames() {
		connectorIdMap = new HashMap<String, Integer>();
	}
	
	private void initMetricDomainConfigItems(Collection<MetricDomainConfig> metricDomainConfigs) {
		metricDomainConfigItems = new ArrayList<SelectItem>(metricDomainConfigs.size());
		metricDomainConfigMap = new HashMap<Integer,MetricDomainConfig>();
		
		for (MetricDomainConfig metricDomainConfig: metricDomainConfigs) {
			SelectItem item = new SelectItem(metricDomainConfig.getId(), metricDomainConfig.getDescription());
			metricDomainConfigItems.add(item);
			metricDomainConfigMap.put(metricDomainConfig.getId(), metricDomainConfig);
		}
	}	
	
	private void initConnectorNames(Collection<ConnectorConfig> connectorConfigs) {
		connectorIdMap = new HashMap<String,Integer>();
		connectorConfigMap = new HashMap<Integer,ConnectorConfig>();
		
		Collection<Integer> ids = monitorConfig.getConnectorConfigIds();
		for (ConnectorConfig connectorConfig : connectorConfigs) {
			if(ids.contains(connectorConfig.getId())) {
				connectorIdMap.put(connectorConfig.getType(), connectorConfig.getId());
				connectorConfigMap.put(connectorConfig.getId(), connectorConfig);
			}
		}
	}
	
	private MetricDomainConfig initMetricDomainConfigs(int metricDomainId) {
		List<MetricDomainConfig> metricDomainConfigs = null;
		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			metricDomainConfigs = dataModelPM.getMetricDomainConfigs(metricDomainId);
			initMetricDomainConfigItems(metricDomainConfigs);
		} catch(Exception exc) {
			Log.error(exc);
		}
		
		MetricDomainConfig config = metricDomainConfigs.get(0);
		if (monitorConfig.getMetricDomainConfig().getId() > 0) {
			int lastMetricDomainId = monitorConfig.getMetricDomainConfig().getIkrStaticDomainId();
			if (metricDomainId == lastMetricDomainId) {
				config = monitorConfig.getMetricDomainConfig();
			}
			else {
				
			}
		}
		
		// return a default value
		return config;
	}	
	
//	private Map<String, MetricDomainResource> initMetricDomainResources(int IkrStaticDomainId) {
//		Map<String, MetricDomainResource> metricDomainResources = new HashMap<String, MetricDomainResource>();
//		metricDomainResourceMap = new HashMap<Integer, Map<String,MetricDomainResource>>();
//		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			List<MetricDomainResource> resources = dataModelPM.getMetricDomainResources(IkrStaticDomainId);
//			for (MetricDomainResource resource : resources) {
//				metricDomainResources.put(resource.getResourceName(), resource);
//			}
//			metricDomainResourceMap.put(IkrStaticDomainId, metricDomainResources);
//		} catch(Exception exc) {
//			Log.error(exc);
//		}
//		
//		// return a default value
//		return metricDomainResources;
//	}
	
	private void initConnector() {
		Collection<ConnectorConfig> connectorConfigs = null;		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM);
		try {
			connectorConfigs = dataModelPM.getConnectorConfigs().values();
		} catch(Exception exc) {
			logger.error(exc);
		}
		
		initConnectorItems(connectorConfigs);
		initConnectorNames(connectorConfigs);
	}
	
	private void initConnectorItems(Collection<ConnectorConfig> connectorConfigs) {		
		jmxConnectorItems = new ArrayList<SelectItem>();
		calypsoConnectorItems = new ArrayList<SelectItem>();
		httpConnectorItems = new ArrayList<SelectItem>();
		sysloadConnectorItems = new ArrayList<SelectItem>();
		rdbmsConnectorItems = new ArrayList<SelectItem>();
		systemAgentConnectorItems = new ArrayList<SelectItem>();
		
		for (ConnectorConfig connectorConfig : connectorConfigs) {
			String type = connectorConfig.getType();			
			SelectItem item = new SelectItem(connectorConfig.getId(),connectorConfig.getName());			
			if (type.equals(JmxConnectorConfig.TYPE)) {
				jmxConnectorItems.add(item);
			} else if (type.equals(CalypsoConnectorConfig.TYPE)) {
				calypsoConnectorItems.add(item);
			} else if (type.equals(HttpConnectorConfig.TYPE)) {
				httpConnectorItems.add(item);
			} else if (type.equals(SysloadConnectorConfig.TYPE)) {
				sysloadConnectorItems.add(item);
			} else if (type.equals(RdbmsConnectorConfig.TYPE)) {
				rdbmsConnectorItems.add(item);
			} else if (type.equals(SystemAgentConnectorConfig.TYPE)) {
				systemAgentConnectorItems.add(item);
			}
		}
	}		
	
	public MetricDomainConfig getCurrentMetricDomainConfig() {
		return monitorConfig.getMetricDomainConfig();
	}

	public void update() {
		Collection<Integer> connectorIds = new ArrayList<Integer>();
		for(int connectorId: connectorIdMap.values()) {
			if (connectorId != 0) {
				connectorIds.add(connectorId);
			}
		}
		monitorConfig.setConnectorConfigIds(connectorIds);
	}
	
}
