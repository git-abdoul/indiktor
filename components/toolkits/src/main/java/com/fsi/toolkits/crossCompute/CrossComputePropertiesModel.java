package com.fsi.toolkits.crossCompute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fsi.toolkits.VarDefModel;

public class CrossComputePropertiesModel implements Serializable {
	private static final long serialVersionUID = 6684044652286073328L;
	
	private String domainType;
	private String metricDomain;
	private String metricCategory;
	
	private List<VarDefModel> varDefinitions;

	public CrossComputePropertiesModel() {
		varDefinitions = new ArrayList<VarDefModel>();
	}
	
	public String getKey() {
		return getDomainType() + "_" + getMetricDomain() + "_" + getMetricCategory();
	}
	
	public void addVarDefinition(VarDefModel model) {
		varDefinitions.add(model);
	}

	public List<VarDefModel> getVarDefinitions() {
		return varDefinitions;
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

}
