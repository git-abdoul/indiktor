package com.fsi.monitoring.ikr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricDomainConfig
implements Serializable {

	private static final long serialVersionUID = 2300445712241914577L;

	private int id;
	private int ikrStaticDomainId;
	private Collection<String> connectorTypes;
	private String description;
	private String className;
	private boolean useDataSynchronization;
	
	private List<MetricDomainConfigField> fields;
	private List<String> domainItemConfigs;
	private List<MetricDomainConfigExtension> extensionConfigs;
	private List<MetricDomainConfigResource> resources;
	private Map<String, String> attributes;
	 
	public MetricDomainConfig() {
		connectorTypes = new ArrayList<String>();
		domainItemConfigs = new ArrayList<String>();
		fields = new ArrayList<MetricDomainConfigField>();
		extensionConfigs = new ArrayList<MetricDomainConfigExtension>();
		resources = new ArrayList<MetricDomainConfigResource>();
		attributes = new HashMap<String, String>();
	}	
		
	public MetricDomainConfig(int id, int ikrStaticDomainId,
			Collection<String> connectorTypes, List<String> domainItemConfigs,
			String description, String className,boolean useDataSynchronization,
			List<MetricDomainConfigField> fields,
			List<MetricDomainConfigExtension> extensionConfigs,
			List<MetricDomainConfigResource> resources,
			Map<String, String> attributes) {
		super();
		this.id = id;
		this.ikrStaticDomainId = ikrStaticDomainId;
		this.connectorTypes = connectorTypes;
		this.domainItemConfigs = domainItemConfigs;
		this.description = description;
		this.className = className;
		this.useDataSynchronization = useDataSynchronization;
		this.fields = fields;
		this.extensionConfigs = extensionConfigs;
		this.resources = resources;
		this.attributes = attributes;
	}


	public int getId() {
		return id;
	}
	
	public int getIkrStaticDomainId() {
		return ikrStaticDomainId;
	}
	
	public void setIkrStaticDomainId(int ikrStaticDomainId) {
		this.ikrStaticDomainId = ikrStaticDomainId;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Collection<String> getConnectorTypes() {
		return connectorTypes;
	}
	
	public void setConnectorType(Collection<String> connectorTypes) {
		this.connectorTypes = connectorTypes;
	}
	
	public List<String> getDomainItemConfigs() {
		return domainItemConfigs;
	}

	public void setDomainItemConfigs(List<String> domainItemConfigs) {
		this.domainItemConfigs = domainItemConfigs;
	}
	
	public void addExtension(MetricDomainConfigExtension extension) {
		extensionConfigs.add(extension);
	}

	public List<MetricDomainConfigExtension> getExtensionConfigs() {
		return extensionConfigs;
	}
	
	public void setExtendedConfig(List<MetricDomainConfigExtension> extensionConfigs) {
		this.extensionConfigs = extensionConfigs;
	}

	public List<MetricDomainConfigField> getFields() {
		return fields;
	}

	public void setFields(List<MetricDomainConfigField> fields) {
		this.fields = fields;
	}

	public List<MetricDomainConfigResource> getResources() {
		
		
		return resources;
	}

	public void setResources(List<MetricDomainConfigResource> resources) {
		this.resources = resources;
	}

	public boolean isUseDataSynchronization() {
		return useDataSynchronization;
	}

	public void setUseDataSynchronization(boolean useDataSynchronization) {
		this.useDataSynchronization = useDataSynchronization;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
