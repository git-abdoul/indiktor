package com.fsi.monitoring.ikr.model;

import java.io.Serializable;

public class MetricDomainConfigResource implements Serializable {
	private static final long serialVersionUID = -4890805216864157805L;
	
	private int metricDomainConfigId;
	private MetricDomainResource resource;
	private boolean enabled;
	
	public MetricDomainConfigResource(int metricDomainConfigId, MetricDomainResource resource, boolean enabled) {
		super();
		this.metricDomainConfigId = metricDomainConfigId;
		this.resource = resource;
		this.enabled = enabled;
	}

	public int getMetricDomainConfigId() {
		return metricDomainConfigId;
	}

	public MetricDomainResource getResource() {
		return resource;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
