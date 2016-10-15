package com.fsi.monitoring.indiktor.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetricDomainModel {
	private String type;
	private String label;
	private List<MetricCategoryModel> metricCategories = new ArrayList<MetricCategoryModel>();
	private List<MetricDomainConfigModel> configs = new ArrayList<MetricDomainConfigModel>();
	private Set<String> resources = new HashSet<String>();
	
	public void setLabel(String label) {
		this.label = label;
	}		
	
	public String getLabel() {
		return (label!=null && label.length()>0)?label:"";
	}
	
	public void setType(String type) {
		this.type = type;
	}		
	
	public String getType() {
		return (type!=null && type.length()>0)?type:"";
	}

	public void addMetricCategory(MetricCategoryModel metricCategoryModel) {
		metricCategories.add(metricCategoryModel);
		resources.add(metricCategoryModel.getResource());
	}
	
	public void addConfig(MetricDomainConfigModel model) {
		configs.add(model);
	}
	
	public List<MetricCategoryModel> getMetricCategories() {
		return metricCategories;
	}
	
	public List<MetricDomainConfigModel> getConfigs() {
		return configs;
	}

	public Set<String> getResources() {
		return resources;
	}	
}
