package com.fsi.toolkits.crossCompute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CrossComputeDefinitionModel implements Serializable {
	private static final long serialVersionUID = -4961157484265944356L;
	
	private String domainType;
	private String metricDomain;
	private String metricCategory;
	private String unitType;
	private String unit;
	private String description;
	
	private List<MetricModel> metrics;
	
	private String computeName;
	private String computation;	
	
	public CrossComputeDefinitionModel() {
		metrics = new ArrayList<MetricModel>();
	}
	
	public String getKey() {
		return getDomainType() + "_" + getMetricDomain() + "_" + getMetricCategory();
	}
	
	public void setCompute(ComputeModel model) {
		this.computeName = model.getName();
		this.computation = model.getComputation();
	}
	
	public void addMetric(MetricModel model) {
		metrics.add(model);
	}
	
	public List<MetricModel> getMetrics() {
		return metrics;
	}

	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	public String getMetricDomain() {
		return metricDomain;
	}
	public void setMetricDomain(String metricDomain) {
		this.metricDomain = metricDomain;
	}
	public String getMetricCategory() {
		return metricCategory;
	}
	public void setMetricCategory(String metricCategory) {
		this.metricCategory = metricCategory;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getComputeName() {
		return computeName;
	}

	public void setComputeName(String computeName) {
		this.computeName = computeName;
	}

	public String getComputation() {
		return computation;
	}

	public void setComputation(String computation) {
		this.computation = computation;
	}
}
