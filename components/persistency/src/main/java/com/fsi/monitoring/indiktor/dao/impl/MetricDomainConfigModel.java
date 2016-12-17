package com.fsi.monitoring.indiktor.dao.impl;

import java.util.ArrayList;
import java.util.List;

public class MetricDomainConfigModel {
	private String connectorType;
	private String classname;
	private String description;
	private String item;
	private boolean useDataSynchronization;
	
	private List<MetricDomainConfigFieldModel> fields = new ArrayList<MetricDomainConfigFieldModel>();
	private List<MetricDomainConfigAttributeModel> attributes = new ArrayList<MetricDomainConfigAttributeModel>();
	private List<MetricDomainConfigResourceModel> resources = new ArrayList<MetricDomainConfigResourceModel>();
	
	public String getConnectorType() {
		return connectorType;
	}
	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	
	public void addField(MetricDomainConfigFieldModel field) {
		fields.add(field);
	}
	public List<MetricDomainConfigFieldModel> getFields() {
		return fields;
	}	
	
	public void addAttribute(MetricDomainConfigAttributeModel attribute) {
		attributes.add(attribute);
	}
	public List<MetricDomainConfigAttributeModel> getAttributes() {
		return attributes;
	}	
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public List<String> getDomainItems() {
		List<String> domainItems = new ArrayList<String>();
		if (item!=null && item.length()>0){
			String[] items = item.split(",");
			for (String it : items) {
				domainItems.add(it);
			}
		}
		return domainItems;
	}
	
	public void addResource(MetricDomainConfigResourceModel resource) {
		resources.add(resource);
	}
	public List<MetricDomainConfigResourceModel> getResources() {
		return resources;
	}
	public boolean isUseDataSynchronization() {
		return useDataSynchronization;
	}
	public void setUseDataSynchronization(boolean useDataSynchronization) {
		this.useDataSynchronization = useDataSynchronization;
	}
	
	
}