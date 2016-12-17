package com.fsi.monitoring.ikr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class MetricDomainConfigExtension
implements Serializable {

	private static final long serialVersionUID = 2300445712241914577L;

	private int metricDomainConfigId;
	private int priority;
	private String description;
	private String className;
	
	public MetricDomainConfigExtension(int metricDomainConfigId) {
		this.metricDomainConfigId = metricDomainConfigId;
		this.priority = 1;
		description = "";
		className = "";
	}	
		
	public MetricDomainConfigExtension(int metricDomainConfigId, int priority, String description, String className) {
		super();
		this.metricDomainConfigId = metricDomainConfigId;
		this.priority = priority;
		this.description = description;
		this.className = className;
	}


	public int getMetricDomainConfigId() {
		return metricDomainConfigId;
	}	
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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
}
