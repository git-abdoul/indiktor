package com.fsi.monitoring.indiktor.dao.impl;

import java.util.ArrayList;
import java.util.List;

public class DomainTypeModel {
	private String type;
	private String label;		
	private List<MetricDomainModel> metricDomainModels = new ArrayList<MetricDomainModel>();
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDomainType() {
		return (type!=null && type.length()>0)?type:"";
	}		
	
	public String getLabel() {
		return (label!=null && label.length()>0)?label:"";
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<MetricDomainModel> getMetricDomainModels() {
		return metricDomainModels;
	}
	
	public void addMetricDomain(MetricDomainModel metricDomainModel) {
		metricDomainModels.add(metricDomainModel);
	}
}
