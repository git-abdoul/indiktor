package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainResource;

import java.io.Serializable;

import com.fsi.monitoring.ikr.model.IkrCategoryResource;

public class IkrCategoryResourceBean implements Serializable {
	private static final long serialVersionUID = 1635851874352187039L;
	
	private String metricCategory;
	private IkrCategoryResource categoryResource;
	
	public IkrCategoryResourceBean(String metricCategory,
			IkrCategoryResource categoryResource) {
		super();
		this.metricCategory = metricCategory;
		this.categoryResource = categoryResource;
	}

	public String getMetricCategory() {
		return metricCategory;
	}

	public IkrCategoryResource getCategoryResource() {
		return categoryResource;
	}
}
