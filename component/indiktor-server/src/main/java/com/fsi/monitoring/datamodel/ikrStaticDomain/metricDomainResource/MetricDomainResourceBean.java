package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;

public class MetricDomainResourceBean implements Serializable{
	private static final Logger logger = Logger.getLogger(MetricDomainResourceBean.class);
	private static final long serialVersionUID = -4792194618022946443L;
	
	private boolean selected;
	
	private String domainType;	
	private String metricDomain;
	private String resourceName;
	
	private MetricDomainResource metricDomainResource;
	
	private Map<Integer, IkrCategoryResource> ikrCategoryResources;	
	private Map<Integer, IkrStaticDomain> ikrCategories;
	private Map<Integer, IkrCategoryResourceBean> ikrCategoriesWithNoResources;
	
	private List<IkrCategoryResourceBean> categoryResourceBeans;
	
	private Set<Integer> categoryResourcesToDelete;
	
	private Set<String> searchIndexes;
	
	public MetricDomainResourceBean() {
		this.metricDomainResource = new MetricDomainResource();
		this.resourceName = metricDomainResource.getResourceName();		
		
		categoryResourceBeans = new ArrayList<IkrCategoryResourceBean>();
		ikrCategoriesWithNoResources = new HashMap<Integer, IkrCategoryResourceBean>();
		ikrCategoryResources = new HashMap<Integer, IkrCategoryResource>();
		categoryResourcesToDelete = new HashSet<Integer>();
	}
	
	public MetricDomainResourceBean(String domainType, 
									String metricDomain,	
									MetricDomainResource metricDomainResource, 
									Map<Integer, IkrStaticDomain> ikrCategories, 
									Map<Integer, IkrCategoryResource> ikrCategoryResources,
									Map<Integer, IkrCategoryResourceBean> ikrCategoriesWithNoResources) {
		super();
		this.metricDomainResource = metricDomainResource;
		this.domainType = domainType;
		this.metricDomain = metricDomain;
		this.resourceName = metricDomainResource.getResourceName();		
		
		
		this.ikrCategoryResources = ikrCategoryResources;
		this.ikrCategories = ikrCategories;
		this.ikrCategoriesWithNoResources = ikrCategoriesWithNoResources;
		categoryResourcesToDelete = new HashSet<Integer>();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();		
		searchIndexes.add(domainType.toLowerCase());
		searchIndexes.add(metricDomain.toLowerCase());
		searchIndexes.add(resourceName.toLowerCase());
		for (IkrStaticDomain ikrCategory : ikrCategories.values()) {
			IkrCategoryResource resource = ikrCategoryResources.get(ikrCategory.getId());
			searchIndexes.add(ikrCategory.getLabel().toLowerCase());
			searchIndexes.add(resource.getName().toLowerCase());
		}		
	}
	
	public void initCategoryResourceBeans() {
		this.categoryResourceBeans = new ArrayList<IkrCategoryResourceBean>();
		for (IkrStaticDomain ikrCategory : ikrCategories.values()) {
			IkrCategoryResource ikrCategoryResource = ikrCategoryResources.get(ikrCategory.getId());
			if (ikrCategoryResource!=null) {
				categoryResourceBeans.add(new IkrCategoryResourceBean(ikrCategory.getLabel(), ikrCategoryResource));
			}
		}
		
		this.initSearchIndexes();
	}
	
	public void removeAllCategoryResources(ActionEvent action) {
		Iterator<IkrCategoryResourceBean> iterator = categoryResourceBeans.iterator();
		while (iterator.hasNext()) {
			IkrCategoryResourceBean next = iterator.next();
			iterator.remove();
			ikrCategoriesWithNoResources.put(next.getCategoryResource().getIkrStaticDomainId(), next);
			categoryResourcesToDelete.add(next.getCategoryResource().getIkrStaticDomainId());
		}
	}	
	
	public void removeCategoryResource(ActionEvent action) {
		IkrCategoryResourceBean ikrCategoryResourceBean =(IkrCategoryResourceBean)action.getComponent().getAttributes().get("metricCategoryResource");
		Iterator<IkrCategoryResourceBean> iterator = categoryResourceBeans.iterator();
		while (iterator.hasNext()) {
			IkrCategoryResourceBean next = iterator.next();
			if (ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId() == next.getCategoryResource().getIkrStaticDomainId()) {
				iterator.remove();
				ikrCategoriesWithNoResources.put(ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId(), ikrCategoryResourceBean);
				categoryResourcesToDelete.add(ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId());
			}
		}		
	}	
	
	public void addMetricCategoryResource(ActionEvent action) {
		IkrCategoryResourceBean ikrCategoryResourceBean =(IkrCategoryResourceBean)action.getComponent().getAttributes().get("metricCategoryResource");
		categoryResourceBeans.add(ikrCategoryResourceBean);
		int ikrStaticDomainId = ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId();
		ikrCategoriesWithNoResources.remove(ikrStaticDomainId);
		if (categoryResourcesToDelete.contains(ikrStaticDomainId)) {
			categoryResourcesToDelete.remove(ikrStaticDomainId);
		}
	}	
	
	public void save() {
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			int metricDomainResourceId = dataModelPM.saveMetricDomainResource(metricDomainResource);
			
			for (IkrCategoryResourceBean bean : categoryResourceBeans) {
				IkrCategoryResource ikrCategoryResource = bean.getCategoryResource();
				ikrCategoryResource.setMetricDomainResourceId(metricDomainResourceId);
				dataModelPM.saveIkrCategoryResource(ikrCategoryResource);
			}
			
			Iterator<Integer> iterator = categoryResourcesToDelete.iterator();
			while (iterator.hasNext()) {
				int id = iterator.next();
				dataModelPM.removeIkrCategoryResourceByStaticDomainId(id);
				iterator.remove();				
			}		
			
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getDomainType() {
		return domainType;
	}

	public String getMetricDomain() {
		return metricDomain;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public MetricDomainResource getMetricDomainResource() {
		return metricDomainResource;
	}

	public void setMetricDomainResource(MetricDomainResource metricDomainResource) {
		this.metricDomainResource = metricDomainResource;
	}		

//	public List<IkrStaticDomain> getMetricCategories() {
//		return metricCategories;
//	}
//
//	public void setMetricCategories(List<IkrStaticDomain> metricCategories) {
//		this.metricCategories = metricCategories;
//	}
//
//	public Map<Integer, IkrCategoryResource> getAvailableCategoryResources() {
//		return availableCategoryResources;
//	}
	
	public boolean isRenderIkrCatgoryResourceConfig() {
		return ikrCategoriesWithNoResources.size()>0;
	}
	
//	public int getAvailableCategorySize() {
//		return metricDomainManagerBean.getAvailableMetricCategories(metricDomainResource.getIkrStaticDomainId()).size();
//	}

//	public void setAvailableCategoryResources(Map<Integer, IkrCategoryResource> availableCategoryResources) {
//		this.availableCategoryResources = availableCategoryResources;
//	}
	
	public List<IkrCategoryResourceBean> getCategoryResourceBeans() {
		Collections.sort(categoryResourceBeans, new Comparator<IkrCategoryResourceBean>() {
			public int compare(IkrCategoryResourceBean o1, IkrCategoryResourceBean o2) {
				return o1.getMetricCategory().compareTo(o2.getMetricCategory());
			}
		});
		return categoryResourceBeans;
	}	

	public List<IkrCategoryResourceBean> getIkrCategoriesWithNoResources() {
		List<IkrCategoryResourceBean> values = new ArrayList<IkrCategoryResourceBean>(ikrCategoriesWithNoResources.values());
		Collections.sort(values, new Comparator<IkrCategoryResourceBean>() {
			public int compare(IkrCategoryResourceBean o1, IkrCategoryResourceBean o2) {
				return o1.getMetricCategory().compareTo(o2.getMetricCategory());
			}
		});
		return values;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}	
	
	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}

	public Map<Integer, IkrCategoryResource> getIkrCategoryResources() {
		return ikrCategoryResources;
	}

	public void setIkrCategoryResources(
			Map<Integer, IkrCategoryResource> ikrCategoryResources) {
		this.ikrCategoryResources = ikrCategoryResources;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public void setMetricDomain(String metricDomain) {
		this.metricDomain = metricDomain;
	}

	public void setIkrCategoriesWithNoResources(
			Map<Integer, IkrCategoryResourceBean> ikrCategoriesWithNoResources) {
		this.ikrCategoriesWithNoResources = ikrCategoriesWithNoResources;
	}		
}
