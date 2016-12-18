package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.ikr.model.IkrStaticDomain;

public class MetricDomainManagerBean {
	
	private Map<Integer, List<IkrStaticDomain>> availableMetricCategories;
	
	public void init() {
		availableMetricCategories = new HashMap<Integer, List<IkrStaticDomain>>();
	}
	
	public void setMetricCatgories(int metricDomainId, List<IkrStaticDomain> categories) {
		availableMetricCategories.put(metricDomainId, categories);
	}
	
	public List<IkrStaticDomain> getAvailableMetricCategories(int metricDomainId) {
		return availableMetricCategories.get(metricDomainId);
	}
	
	public synchronized void removeMetricCategory(int metricDomainId, int metricCategoryId) {
		List<IkrStaticDomain> categories = availableMetricCategories.get(metricDomainId);
		
		List<IkrStaticDomain> tmp = new ArrayList<IkrStaticDomain>(categories);
		for (IkrStaticDomain category : tmp) {
			if (metricCategoryId == category.getId())
				categories.remove(category);
		}
	}
}
