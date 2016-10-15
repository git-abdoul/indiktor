package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainConfig;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigExtension;

public class MetricDomainConfigBean implements Serializable {
	private static final long serialVersionUID = 8613609012192791441L;
	
	private MetricDomainConfig metricDomainConfig;
	private String domainType;
	private String metricDomain;
	private boolean selected;
	
	private Set<String> searchIndexes;
	
	public MetricDomainConfigBean(MetricDomainConfig metricDomainConfig, String domainType, String metricDomain) {
		super();
		this.metricDomainConfig = metricDomainConfig;
		this.domainType = domainType;
		this.metricDomain = metricDomain;
		
		this.initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(metricDomainConfig.getClassName().toLowerCase());
		searchIndexes.add(metricDomainConfig.getDescription().toLowerCase());
		for (String connector : metricDomainConfig.getConnectorTypes()) {
			searchIndexes.add(connector.toLowerCase());
		}
		searchIndexes.add(domainType.toLowerCase());
		searchIndexes.add(metricDomain.toLowerCase());
		for (MetricDomainConfigExtension extended : metricDomainConfig.getExtensionConfigs()){
			if (extended != null) {
				searchIndexes.add(extended.getClassName().toLowerCase());
				searchIndexes.add(extended.getDescription().toLowerCase());
			}
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public MetricDomainConfig getMetricDomainConfig() {
		return metricDomainConfig;
	}
	public String getDomainType() {
		return domainType;
	}
	public String getMetricDomain() {
		return metricDomain;
	}

	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}		
}
